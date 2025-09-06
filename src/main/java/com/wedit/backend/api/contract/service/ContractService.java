package com.wedit.backend.api.contract.service;

import com.wedit.backend.api.contract.dto.request.ContractAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.request.AvailableTimeRequestDTO;
import com.wedit.backend.api.contract.dto.request.SimpleAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.request.ContractCreateRequestDTO;
import com.wedit.backend.api.contract.dto.request.SimpleContractRequestDTO;
import com.wedit.backend.api.contract.dto.response.*;
import com.wedit.backend.api.contract.dto.response.AvailableTimeResponseDTO;
import com.wedit.backend.api.contract.dto.response.SimpleAvailabilityResponseDTO;
import com.wedit.backend.api.contract.dto.response.MonthlyTimeAvailabilityDTO;
import com.wedit.backend.api.contract.dto.response.DailyTimeAvailabilityDTO;
import com.wedit.backend.api.contract.dto.response.SimpleAvailabilitySummaryDTO;
import com.wedit.backend.api.contract.dto.response.PaginationInfoDTO;
import com.wedit.backend.api.contract.dto.response.TimeSlotAvailabilityDTO;
import com.wedit.backend.api.contract.dto.response.TimeSlotAvailabilityListResponseDTO;
import com.wedit.backend.api.contract.dto.response.FilterInfoDTO;
import com.wedit.backend.api.contract.dto.response.ContractCreateResponseDTO;
import com.wedit.backend.api.contract.dto.response.SimpleContractResponseDTO;
import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.entity.ContractStatus;
import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.repository.ContractRepository;
import com.wedit.backend.api.reservation.repository.ReservationRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.api.vendor.dto.details.WeddingHallDetailsDTO;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final MemberRepository memberRepository;

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
     * 시간대별 가용성 조회 (각 시간대를 개별 항목으로 반환)
     */
    public TimeSlotAvailabilityListResponseDTO getTimeSlotAvailabilities(
            Long vendorId,
            SimpleAvailabilityRequestDTO requestDTO) {

        // 업체 존재 확인
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

        // 모든 요청된 월들의 모든 날짜를 생성하고 정렬
        List<LocalDate> allDates = generateAllRequestedDates(requestDTO.getYear(), requestDTO.getMonths());

        // 전체 날짜에 대한 계약 정보를 한번에 조회
        Map<LocalDate, List<Contract>> contractsByDate = getContractsByDates(vendorId, allDates);

        // 업체의 최소 가격 정보 추출
        Integer minimumAmount = extractMinimumAmount(vendor);

        // 모든 날짜와 시간대 조합을 생성
        List<TimeSlotAvailabilityDTO> allTimeSlots = generateAllTimeSlots(
                allDates, contractsByDate, vendor, minimumAmount);

        // 예약 가능한 시간대만 필터링 (availableOnly가 true인 경우)
        List<TimeSlotAvailabilityDTO> availableTimeSlots = allTimeSlots.stream()
                .filter(TimeSlotAvailabilityDTO::getIsAvailable)
                .collect(Collectors.toList());

        // 페이징 적용
        int totalAvailableSlots = availableTimeSlots.size();
        int startIndex = (requestDTO.getPage() - 1) * requestDTO.getSize();
        int endIndex = Math.min(startIndex + requestDTO.getSize(), totalAvailableSlots);

        List<TimeSlotAvailabilityDTO> pagedTimeSlots = startIndex < totalAvailableSlots ?
                availableTimeSlots.subList(startIndex, endIndex) : Collections.emptyList();

        // 페이징 정보 생성
        PaginationInfoDTO pagination = PaginationInfoDTO.builder()
                .currentPage(requestDTO.getPage())
                .pageSize(requestDTO.getSize())
                .currentPageItems(pagedTimeSlots.size())
                .totalItems((long) totalAvailableSlots)
                .totalPages((int) Math.ceil((double) totalAvailableSlots / requestDTO.getSize()))
                .isFirst(requestDTO.getPage() == 1)
                .isLast(requestDTO.getPage() >= Math.ceil((double) totalAvailableSlots / requestDTO.getSize()))
                .hasNext(requestDTO.getPage() < Math.ceil((double) totalAvailableSlots / requestDTO.getSize()))
                .build();

        // 필터 정보 생성
        FilterInfoDTO filter = FilterInfoDTO.builder()
                .year(requestDTO.getYear())
                .months(requestDTO.getMonths())
                .availableOnly(true)
                .totalTimeSlots(AVAILABLE_TIME_SLOTS.size())
                .totalDays(allDates.size())
                .totalAvailableSlots(totalAvailableSlots)
                .build();

        return TimeSlotAvailabilityListResponseDTO.builder()
                .timeSlots(pagedTimeSlots)
                .pagination(pagination)
                .filter(filter)
                .build();
    }

    /**
     * 업체의 최소 가격 정보 추출
     */
    private Integer extractMinimumAmount(Vendor vendor) {
        try {
            if (vendor.getDetails() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                WeddingHallDetailsDTO details = objectMapper.readValue(
                        vendor.getDetails(), WeddingHallDetailsDTO.class);
                return details.getMinimumAmount();
            }
        } catch (Exception e) {
            log.warn("업체 {}의 최소 가격 정보를 파싱할 수 없습니다: {}", vendor.getId(), e.getMessage());
        }
        return 5000000; // 기본값: 500만원
    }

    /**
     * 모든 날짜와 시간대 조합을 생성
     */
    private List<TimeSlotAvailabilityDTO> generateAllTimeSlots(
            List<LocalDate> allDates,
            Map<LocalDate, List<Contract>> contractsByDate,
            Vendor vendor,
            Integer minimumAmount) {

        List<TimeSlotAvailabilityDTO> timeSlots = new ArrayList<>();

        for (LocalDate date : allDates) {
            List<Contract> dailyContracts = contractsByDate.getOrDefault(date, Collections.emptyList());
            Set<LocalTime> contractedTimes = dailyContracts.stream()
                    .map(Contract::getStartTime)
                    .collect(Collectors.toSet());

            for (LocalTime time : AVAILABLE_TIME_SLOTS) {
                boolean isAvailable = !contractedTimes.contains(time);

                // 기본 보증인원 계산 (최소가격 기준)
                Integer expectedGuests = calculateExpectedGuests(minimumAmount);

                // 예상 식대 계산 (1인당 약 70,000원 가정)
                Integer expectedMealCost = expectedGuests * 70000;

                TimeSlotAvailabilityDTO timeSlot = TimeSlotAvailabilityDTO.builder()
                        .date(date)
                        .time(time)
                        .isAvailable(isAvailable)
                        .minimumAmount(minimumAmount)
                        .expectedGuests(expectedGuests)
                        .expectedMealCost(expectedMealCost)
                        .vendorId(vendor.getId())
                        .vendorName(vendor.getName())
                        .build();

                timeSlots.add(timeSlot);
            }
        }

        return timeSlots;
    }

    /**
     * 최소 가격 기준으로 예상 보증인원 계산
     */
    private Integer calculateExpectedGuests(Integer minimumAmount) {
        // 대관료를 제외한 1인당 평균 비용을 약 30,000원으로 가정
        // 최소가격에서 기본 대관료(약 200만원)을 제외하고 계산
        int baseCost = 2000000;
        int remainingAmount = Math.max(minimumAmount - baseCost, 0);
        int costPerPerson = 30000;

        int guests = remainingAmount / costPerPerson;

        // 최소 50명, 최대 500명으로 제한
        return Math.max(50, Math.min(guests, 500));
    }
    
    /**
     * 시간대 예약 가능 여부 검증
     */
    private void validateTimeSlotAvailability(Long vendorId, LocalDate contractDate, LocalTime startTime) {
        List<Contract> existingContracts = contractRepository.findByVendorIdAndContractDateAndStatusIn(
                vendorId, contractDate, ACTIVE_CONTRACT_STATUSES);
        
        boolean isTimeSlotTaken = existingContracts.stream()
                .anyMatch(contract -> contract.getStartTime().equals(startTime));
        
        if (isTimeSlotTaken) {
            throw new IllegalStateException("해당 시간대는 이미 예약되어 있습니다.");
        }
    }

    @Transactional
    public SimpleContractResponseDTO createSimpleContract(
            Long vendorId,
            Long memberId,
            SimpleContractRequestDTO requestDTO) {
        
        // 업체 존재 확인
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
        
        // 회원 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        
        // 해당 시간대가 이미 계약되어 있는지 확인
        validateTimeSlotAvailability(vendorId, requestDTO.getContractDate(), requestDTO.getContractTime());
        
        // 업체의 최소 가격 정보 추출
        Integer minimumAmount = extractMinimumAmount(vendor);
        
        // 기본값으로 계약 정보 설정
        LocalTime endTime = requestDTO.getContractTime().plusHours(4); // 기본 4시간
        Long totalAmount = minimumAmount.longValue();
        Long depositAmount = totalAmount / 5; // 20% 계약금
        
        // 계약 엔티티 생성
        Contract contract = Contract.builder()
                .member(member)
                .vendor(vendor)
                .contractDate(requestDTO.getContractDate())
                .startTime(requestDTO.getContractTime())
                .endTime(endTime)
                .status(ContractStatus.PENDING)
                .totalAmount(totalAmount)
                .depositAmount(depositAmount)
                .specialRequests(requestDTO.getSpecialRequests())
                .build();
        
        // 계약 저장
        Contract savedContract = contractRepository.save(contract);
        
        // 성공 메시지 생성
        String successMessage = String.format("%d년 %d월 %d일 %s 시간대로 계약이 완료되었습니다.",
                requestDTO.getContractDate().getYear(),
                requestDTO.getContractDate().getMonthValue(),
                requestDTO.getContractDate().getDayOfMonth(),
                requestDTO.getContractTime().toString()
        );
        
        // 응답 DTO 생성
        return SimpleContractResponseDTO.builder()
                .contractId(savedContract.getId())
                .vendorName(vendor.getName())
                .memberName(member.getName())
                .contractDate(savedContract.getContractDate())
                .contractTime(savedContract.getStartTime())
                .status(savedContract.getStatus())
                .createdAt(savedContract.getCreatedAt())
                .specialRequests(savedContract.getSpecialRequests())
                .message(successMessage)
                .build();
    }
}
