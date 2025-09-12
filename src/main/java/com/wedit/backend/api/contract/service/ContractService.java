package com.wedit.backend.api.contract.service;

import com.wedit.backend.api.contract.dto.*;
import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.repository.AvailableSlotRepository;
import com.wedit.backend.api.contract.repository.ContractRepository;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.vendor.entity.AvailableSlot;
import com.wedit.backend.api.vendor.entity.enums.TimeSlotStatus;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final AvailableSlotRepository availableSlotRepository;
    private final MemberRepository memberRepository;


    // 사용자가 선택한 모든 달에 대해 계약 가능한 모든 슬롯 조회
    @Transactional(readOnly = true)
    public List<AvailableSlotResponseDTO> getAvailableContractSlots(AvailableSlotsRequestDTO request) {

        int currentYear = LocalDate.now().getYear();

        return request.months().stream()
                .flatMap(month -> {
                    YearMonth yearMonth = YearMonth.of(currentYear, month);
                    LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
                    LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

                    return availableSlotRepository.findByProductIdAndStatusAndStartTimeBetween(
                                    request.productId(), TimeSlotStatus.AVAILABLE, startOfMonth, endOfMonth)
                            .stream();
                })
                .map(AvailableSlotResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 계약 생성
    public ContractCreateResponseDTO createContract(Long memberId, ContractCreateRequestDTO request) {

        Member member = findMemberById(memberId);
        AvailableSlot slot = findAvailableSlotWithLock(request.availableSlotId());

        slot.book();

        Contract contract = Contract.builder()
                .member(member)
                .product(slot.getProduct())
                .executionDateTime(slot.getStartTime())
                .finalPrice(slot.getProduct().getBasePrice())
                .build();

        Contract savedContract = contractRepository.save(contract);

        return new ContractCreateResponseDTO(savedContract.getId(), "");
    }

    public MyContractsResponseDTO getMyContracts(Long memberId, Pageable pageable) {

        Member member = findMemberById(memberId);
        List<Contract> allContracts = contractRepository.findByMemberWithDetails(member);

        Map<LocalDate, List<Contract>> groupedByDate = allContracts.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getExecutionDateTime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<LocalDate> uniqueDates = new ArrayList<>(groupedByDate.keySet());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), uniqueDates.size());

        if (start >= end) {
            return MyContractsResponseDTO.from(Page.empty(pageable));
        }

        List<MyContractsResponseDTO.MyContractsByDate> pageContent = uniqueDates.subList(start, end).stream()
                .map(date -> {
                    List<MyContractsResponseDTO.MyContractItem> items = groupedByDate.get(date).stream()
                            .map(contract -> {
                                String logoUrl = (contract.getProduct().getVendor().getLogoMedia() != null)
                                        ? contract.getProduct().getVendor().getLogoMedia().getMediaKey()
                                        : null;

                                return MyContractsResponseDTO.MyContractItem.from(contract, logoUrl);
                            })
                            .toList();
                    return new MyContractsResponseDTO.MyContractsByDate(date, items);
                })
                .toList();

        Page<MyContractsResponseDTO.MyContractsByDate> pagedResult = new PageImpl<>(pageContent, pageable, uniqueDates.size());

        return MyContractsResponseDTO.from(pagedResult);
    }

    // --- 헬퍼 메서드 ---

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    private AvailableSlot findAvailableSlotWithLock(Long slotId) {
        AvailableSlot slot = availableSlotRepository.findByIdWithLock(slotId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_SLOT.getMessage()));
        if (slot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_BOOKED.getMessage());
        }
        return slot;
    }
}
