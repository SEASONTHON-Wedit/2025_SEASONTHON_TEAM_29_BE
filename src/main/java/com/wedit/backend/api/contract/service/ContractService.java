package com.wedit.backend.api.contract.service;

import com.wedit.backend.api.contract.dto.request.ContractAvailabilityRequestDTO;
import com.wedit.backend.api.contract.dto.response.*;
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
}
