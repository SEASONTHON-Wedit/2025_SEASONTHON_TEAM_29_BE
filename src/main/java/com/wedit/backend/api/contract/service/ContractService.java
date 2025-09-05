package com.wedit.backend.api.contract.service;

import com.wedit.backend.api.contract.dto.request.ContractAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.request.AvailableTimeRequestDTO;
import com.wedit.backend.api.contract.dto.request.SimpleAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.response.*;
import com.wedit.backend.api.contract.dto.response.AvailableTimeResponseDTO;
import com.wedit.backend.api.contract.dto.response.SimpleAvailabilityResponseDTO;
import com.wedit.backend.api.contract.dto.response.MonthlyTimeAvailabilityDTO;
import com.wedit.backend.api.contract.dto.response.DailyTimeAvailabilityDTO;
import com.wedit.backend.api.contract.dto.response.SimpleAvailabilitySummaryDTO;
import com.wedit.backend.api.contract.dto.response.PaginationInfoDTO;
import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.entity.ContractStatus;
import com.wedit.backend.api.contract.repository.ContractRepository;
import com.wedit.backend.api.reservation.repository.ReservationRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {
    
    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;
    private final VendorRepository vendorRepository;
    
    private static final int DEFAULT_TOTAL_SLOTS = 8; // 기본 하루 총 슬롯 수
    private static final List<ContractStatus> ACTIVE_CONTRACT_STATUSES = 
            Arrays.asList(ContractStatus.PENDING, ContractStatus.CONFIRMED);
    
    // 예약 가능한 시간대 정의
    private static final List<LocalTime> AVAILABLE_TIME_SLOTS = Arrays.asList(
            LocalTime.of(10, 0),   // 10:00
            LocalTime.of(10, 30),  // 10:30
            LocalTime.of(11, 0),   // 11:00
            LocalTime.of(13, 30),  // 13:30
            LocalTime.of(14, 0),   // 14:00
            LocalTime.of(14, 30),  // 14:30
            LocalTime.of(15, 0),   // 15:00
            LocalTime.of(15, 30),  // 15:30
            LocalTime.of(16, 0)    // 16:00
    );
    
    /**
     * 업체의 여러 달 계약 가능 시간 조회 (페이징)
     */
    public Page<ContractAvailabilityResponseDTO> getVendorContractAvailabilities(
            Long vendorId, 
            ContractAvailabilityRequestDTO requestDTO,
            Pageable pageable) {
        
        // 업체 존재 확인
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
        
        // 월별로 정렬된 페이지 처리를 위해 전체 월 목록을 만들고 정렬
        List<Integer> sortedMonths = requestDTO.getMonths().stream()
                .sorted()
                .collect(Collectors.toList());
        
        // 페이징 처리 (월 단위로)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedMonths.size());
        
        if (start >= sortedMonths.size()) {
            return Page.empty(pageable);
        }
        
        List<Integer> pagedMonths = sortedMonths.subList(start, end);
        
        // 각 월별 가용성 계산
        List<ContractMonthlyAvailabilityDTO> monthlyAvailabilities = 
                pagedMonths.stream()
                    .map(month -> calculateMonthlyAvailability(vendor, requestDTO.getYear(), month))
                    .collect(Collectors.toList());
        
        // 요약 정보 계산
        ContractSummaryDTO summary = calculateSummary(monthlyAvailabilities);
        
        // 응답 DTO 생성
        ContractAvailabilityResponseDTO responseDTO = ContractAvailabilityResponseDTO.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .monthlyAvailabilities(monthlyAvailabilities)
                .summary(summary)
                .build();
        
        // Page 객체 생성
        return new org.springframework.data.domain.PageImpl<>(
                Collections.singletonList(responseDTO), 
                pageable, 
                sortedMonths.size()
        );
    }
    
    /**
     * 특정 월의 계약 가용성 계산
     */
    private ContractMonthlyAvailabilityDTO calculateMonthlyAvailability(Vendor vendor, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // 해당 월의 모든 날짜 생성
        List<LocalDate> allDates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            allDates.add(current);
            current = current.plusDays(1);
        }
        
        // 예약 정보 조회
        Map<LocalDate, Long> reservationCounts = getReservationCountsByDates(vendor, allDates);
        
        // 계약 정보 조회  
        Map<LocalDate, Long> contractCounts = getContractCountsByDates(vendor.getId(), allDates);
        
        // 각 날짜별 상세 정보 계산
        List<ContractDateAvailabilityDTO> dateDetails = allDates.stream()
                .map(date -> {
                    int reservedSlots = reservationCounts.getOrDefault(date, 0L).intValue();
                    int contractedSlots = contractCounts.getOrDefault(date, 0L).intValue();
                    int availableSlots = DEFAULT_TOTAL_SLOTS - reservedSlots - contractedSlots;
                    
                    return ContractDateAvailabilityDTO.builder()
                            .date(date)
                            .isAvailable(availableSlots > 0)
                            .totalSlots(DEFAULT_TOTAL_SLOTS)
                            .reservedSlots(reservedSlots)
                            .contractedSlots(contractedSlots)
                            .availableSlots(Math.max(0, availableSlots))
                            .build();
                })
                .collect(Collectors.toList());
        
        // 가용한 날짜 수 계산
        int availableDays = (int) dateDetails.stream()
                .mapToInt(dto -> dto.isAvailable() ? 1 : 0)
                .sum();
        
        return ContractMonthlyAvailabilityDTO.builder()
                .year(year)
                .month(month)
                .totalDays(allDates.size())
                .availableDays(availableDays)
                .dateDetails(dateDetails)
                .build();
    }
    
    /**
     * 예약 수 조회 (날짜별)
     */
    private Map<LocalDate, Long> getReservationCountsByDates(Vendor vendor, List<LocalDate> dates) {
        if (dates.isEmpty()) {
            return Collections.emptyMap();
        }
        
        LocalDate startDate = dates.stream().min(LocalDate::compareTo).orElse(dates.get(0));
        LocalDate endDate = dates.stream().max(LocalDate::compareTo).orElse(dates.get(dates.size() - 1));
        
        return reservationRepository.findAllByVendorAndReservationDateBetween(vendor, startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getReservationDate(),
                        Collectors.counting()
                ));
    }
    
    /**
     * 계약 수 조회 (날짜별)
     */
    private Map<LocalDate, Long> getContractCountsByDates(Long vendorId, List<LocalDate> dates) {
        if (dates.isEmpty()) {
            return Collections.emptyMap();
        }
        
        List<Object[]> results = contractRepository.countByVendorIdAndContractDateInAndStatusIn(
                vendorId, dates, ACTIVE_CONTRACT_STATUSES);
        
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (LocalDate) result[0],
                        result -> (Long) result[1]
                ));
    }
    
    /**
     * 요약 정보 계산
     */
    private ContractSummaryDTO calculateSummary(List<ContractMonthlyAvailabilityDTO> monthlyAvailabilities) {
        int totalMonths = monthlyAvailabilities.size();
        int totalDays = monthlyAvailabilities.stream()
                .mapToInt(ContractMonthlyAvailabilityDTO::getTotalDays)
                .sum();
        int totalAvailableDays = monthlyAvailabilities.stream()
                .mapToInt(ContractMonthlyAvailabilityDTO::getAvailableDays)
                .sum();
        
        double availabilityRate = totalDays > 0 ? (double) totalAvailableDays / totalDays * 100 : 0.0;
        
        return ContractSummaryDTO.builder()
                .totalMonths(totalMonths)
                .totalDays(totalDays)
                .totalAvailableDays(totalAvailableDays)
                .availabilityRate(Math.round(availabilityRate * 100.0) / 100.0) // 소수점 2자리까지
                .build();
    }
    
    /**
     * 특정 날짜의 예약 가능한 시간대 조회
     */
    public AvailableTimeResponseDTO getAvailableTimes(Long vendorId, AvailableTimeRequestDTO requestDTO) {
        // 업체 존재 확인
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
        
        LocalDate targetDate = requestDTO.getDate();
        
        // 해당 날짜에 이미 계약된 시간대 조회
        List<LocalTime> contractedTimes = contractRepository
                .findByVendorIdAndContractDateAndStatusIn(vendorId, targetDate, ACTIVE_CONTRACT_STATUSES)
                .stream()
                .map(contract -> contract.getStartTime())
                .collect(Collectors.toList());
        
        // 예약 가능한 시간대에서 이미 계약된 시간대를 제외
        List<LocalTime> availableTimes = AVAILABLE_TIME_SLOTS.stream()
                .filter(time -> !contractedTimes.contains(time))
                .collect(Collectors.toList());
        
        return AvailableTimeResponseDTO.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .date(targetDate)
                .availableTimes(availableTimes)
                .totalTimeSlots(AVAILABLE_TIME_SLOTS.size())
                .availableTimeSlots(availableTimes.size())
                .build();
    }
    
    /**
     * 여러 월의 간단한 시간대별 가용성 조회 (페이징 적용)
     */
    public SimpleAvailabilityResponseDTO getSimpleMonthlyAvailabilities(
            Long vendorId, 
            SimpleAvailabilityRequestDTO requestDTO) {
        
        // 업체 존재 확인
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
        
        // 모든 요청된 월들의 모든 날짜를 생성하고 정렬
        List<LocalDate> allDates = generateAllRequestedDates(requestDTO.getYear(), requestDTO.getMonths());
        
        // 전체 날짜에 대한 계약 정보를 한번에 조회 (DB 호출 최소화)
        Map<LocalDate, List<Contract>> contractsByDate = getContractsByDates(vendorId, allDates);
        
        // 예약 가능한 날짜들만 필터링
        List<DailyTimeAvailabilityDTO> availableDays = allDates.stream()
                .map(date -> calculateDailyAvailability(date, contractsByDate.get(date)))
                .filter(daily -> daily.getHasAvailableTime()) // 예약 가능한 날짜만
                .collect(Collectors.toList());
        
        // 페이징 적용
        int totalAvailableDays = availableDays.size();
        int startIndex = (requestDTO.getPage() - 1) * requestDTO.getSize();
        int endIndex = Math.min(startIndex + requestDTO.getSize(), totalAvailableDays);
        
        List<DailyTimeAvailabilityDTO> pagedDays = startIndex < totalAvailableDays ? 
                availableDays.subList(startIndex, endIndex) : Collections.emptyList();
        
        // 월별로 그룹화 (페이징된 결과만)
        List<MonthlyTimeAvailabilityDTO> monthlyAvailabilities = groupByMonth(pagedDays, requestDTO.getYear());
        
        // 페이징 정보 생성
        PaginationInfoDTO pagination = createPaginationInfo(requestDTO, totalAvailableDays, pagedDays.size());
        
        // 요약 정보 계산 (전체 데이터 기준)
        SimpleAvailabilitySummaryDTO summary = calculateSimpleSummaryFromDates(allDates, contractsByDate);
        
        return SimpleAvailabilityResponseDTO.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .year(requestDTO.getYear())
                .monthlyAvailabilities(monthlyAvailabilities)
                .summary(summary)
                .pagination(pagination)
                .build();
    }
    
    /**
     * 요청된 연도와 월들의 모든 날짜 생성
     */
    private List<LocalDate> generateAllRequestedDates(Integer year, List<Integer> months) {
        return months.stream()
                .sorted()
                .flatMap(month -> {
                    YearMonth yearMonth = YearMonth.of(year, month);
                    LocalDate startDate = yearMonth.atDay(1);
                    LocalDate endDate = yearMonth.atEndOfMonth();
                    
                    List<LocalDate> monthDates = new ArrayList<>();
                    LocalDate current = startDate;
                    while (!current.isAfter(endDate)) {
                        monthDates.add(current);
                        current = current.plusDays(1);
                    }
                    return monthDates.stream();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 모든 날짜의 계약 정보를 한번에 조회
     */
    private Map<LocalDate, List<Contract>> getContractsByDates(Long vendorId, List<LocalDate> allDates) {
        if (allDates.isEmpty()) {
            return Collections.emptyMap();
        }
        
        LocalDate minDate = allDates.stream().min(LocalDate::compareTo).orElse(allDates.get(0));
        LocalDate maxDate = allDates.stream().max(LocalDate::compareTo).orElse(allDates.get(allDates.size() - 1));
        
        List<Contract> contracts = contractRepository.findByVendorIdAndContractDateBetweenAndStatusIn(
                vendorId, minDate, maxDate, ACTIVE_CONTRACT_STATUSES);
        
        return contracts.stream()
                .collect(Collectors.groupingBy(Contract::getContractDate));
    }
    
    /**
     * 특정 날짜의 시간대 가용성 계산
     */
    private DailyTimeAvailabilityDTO calculateDailyAvailability(LocalDate date, List<Contract> contracts) {
        List<Contract> dailyContracts = contracts != null ? contracts : Collections.emptyList();
        
        // 계약된 시간대 추출
        List<LocalTime> contractedTimes = dailyContracts.stream()
                .map(Contract::getStartTime)
                .collect(Collectors.toList());
        
        // 예약 가능한 시간대 계산
        List<LocalTime> availableTimes = AVAILABLE_TIME_SLOTS.stream()
                .filter(time -> !contractedTimes.contains(time))
                .collect(Collectors.toList());
        
        return DailyTimeAvailabilityDTO.builder()
                .date(date)
                .availableTimes(availableTimes)
                .contractedTimes(contractedTimes)
                .totalTimeSlots(AVAILABLE_TIME_SLOTS.size())
                .availableTimeSlots(availableTimes.size())
                .hasAvailableTime(!availableTimes.isEmpty())
                .build();
    }
    
    /**
     * 페이징된 날짜들을 월별로 그룹화
     */
    private List<MonthlyTimeAvailabilityDTO> groupByMonth(List<DailyTimeAvailabilityDTO> pagedDays, Integer year) {
        Map<Integer, List<DailyTimeAvailabilityDTO>> monthlyGrouped = pagedDays.stream()
                .collect(Collectors.groupingBy(day -> day.getDate().getMonthValue()));
        
        return monthlyGrouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Integer month = entry.getKey();
                    List<DailyTimeAvailabilityDTO> monthDays = entry.getValue();
                    YearMonth yearMonth = YearMonth.of(year, month);
                    
                    return MonthlyTimeAvailabilityDTO.builder()
                            .year(year)
                            .month(month)
                            .totalDays(yearMonth.lengthOfMonth())
                            .dailyAvailabilities(monthDays)
                            .availableDaysInMonth(monthDays.size()) // 현재 페이지에서 보여지는 가용 날짜 수
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 페이징 정보 생성
     */
    private PaginationInfoDTO createPaginationInfo(SimpleAvailabilityRequestDTO requestDTO, 
                                                   int totalAvailableDays, int currentPageItems) {
        int totalPages = (int) Math.ceil((double) totalAvailableDays / requestDTO.getSize());
        
        return PaginationInfoDTO.builder()
                .currentPage(requestDTO.getPage())
                .pageSize(requestDTO.getSize())
                .currentPageItems(currentPageItems)
                .totalItems((long) totalAvailableDays)
                .totalPages(totalPages)
                .isFirst(requestDTO.getPage() == 1)
                .isLast(requestDTO.getPage() >= totalPages)
                .hasNext(requestDTO.getPage() < totalPages)
                .build();
    }
    
    /**
     * 전체 데이터 기준 요약 정보 계산
     */
    private SimpleAvailabilitySummaryDTO calculateSimpleSummaryFromDates(
            List<LocalDate> allDates, Map<LocalDate, List<Contract>> contractsByDate) {
        
        int totalDays = allDates.size();
        int daysWithAvailableTime = 0;
        int fullyBookedDays = 0;
        
        for (LocalDate date : allDates) {
            DailyTimeAvailabilityDTO daily = calculateDailyAvailability(date, contractsByDate.get(date));
            if (daily.getHasAvailableTime()) {
                daysWithAvailableTime++;
            } else {
                fullyBookedDays++;
            }
        }
        
        double availabilityRate = totalDays > 0 ? (double) daysWithAvailableTime / totalDays * 100 : 0.0;
        
        // 요청된 월들의 고유 개수 계산
        Set<Integer> uniqueMonths = allDates.stream()
                .map(date -> date.getMonthValue())
                .collect(Collectors.toSet());
        
        return SimpleAvailabilitySummaryDTO.builder()
                .totalMonths(uniqueMonths.size())
                .totalDays(totalDays)
                .daysWithAvailableTime(daysWithAvailableTime)
                .fullyBookedDays(fullyBookedDays)
                .availabilityRate(Math.round(availabilityRate * 100.0) / 100.0)
                .build();
    }
}
