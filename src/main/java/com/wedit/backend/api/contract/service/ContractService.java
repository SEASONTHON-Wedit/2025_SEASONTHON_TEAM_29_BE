package com.wedit.backend.api.contract.service;

import com.wedit.backend.api.contract.dto.*;
import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.repository.AvailableSlotRepository;
import com.wedit.backend.api.contract.repository.ContractRepository;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.vendor.entity.AvailableSlot;
import com.wedit.backend.api.vendor.entity.Product;
import com.wedit.backend.api.vendor.entity.enums.TimeSlotStatus;
import com.wedit.backend.api.vendor.repository.ProductRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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
    private final MediaService mediaService;
    private final ProductRepository productRepository;


    // 사용자가 선택한 모든 달에 대해 계약 가능한 모든 슬롯 조회
    @Transactional(readOnly = true)
    public List<AvailableSlotResponseDTO> getAvailableContractSlots(AvailableSlotsRequestDTO request) {

        int currentYear = LocalDate.now().getYear();

        return request.months().stream()
                .flatMap(month -> {
                    YearMonth yearMonth = YearMonth.of(currentYear, month);
                    LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
                    LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(java.time.LocalTime.MAX);

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

        return new ContractCreateResponseDTO(savedContract.getId());
    }

    // 마이페이지 계약건 탭 페이징 조회
    @Transactional(readOnly = true)
    public MyContractsResponseDTO getMyContracts(Long memberId, Pageable pageable) {

        Member member = findMemberById(memberId);

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "executionDateTime"));
        }

        Page<Contract> contractsPage = contractRepository.findAllContractsByMember(member, pageable);

        // DB단에서 페이징 후 메모리에서 그룹핑
        Map<LocalDate, List<MyContractsResponseDTO.MyContractItem>> groupedByDate = contractsPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        c -> c.getExecutionDateTime().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.mapping(contract -> {
                            String logoUrl = contract.getProduct().getVendor().getLogoMedia() != null
                                    ? mediaService.toCdnUrl(contract.getProduct().getVendor().getLogoMedia().getMediaKey())
                                    : null;
                            return MyContractsResponseDTO.MyContractItem.from(contract, logoUrl);
                        }, Collectors.toList())
                ));

        List<MyContractsResponseDTO.MyContractsByDate> pageContent = groupedByDate.entrySet().stream()
                .map(entry -> new MyContractsResponseDTO.MyContractsByDate(entry.getKey(), entry.getValue()))
                .toList();

        Page<MyContractsResponseDTO.MyContractsByDate> pagedResult = new PageImpl<>(pageContent, pageable, contractsPage.getTotalElements());

        return MyContractsResponseDTO.from(pagedResult);
    }

    // 후기 작성하러 가기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewableContractResponseDTO> getReviewableContracts(Long memberId, Pageable pageable) {

        Member member = findMemberById(memberId);

        // 기본은 이행일 기준 내림차순
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "executionDateTime"));
        }

        Page<Contract> reviewableContractsPage = contractRepository.findReviewableContracts(member, LocalDateTime.now(), pageable);

        return reviewableContractsPage.map(contract -> {
            String logoUrl = contract.getProduct().getVendor().getLogoMedia() != null
                    ? mediaService.toCdnUrl(contract.getProduct().getVendor().getLogoMedia().getMediaKey())
                    : null;

            return ReviewableContractResponseDTO.from(contract, logoUrl);
        });
    }

    // 계약 상세 조회
    @Transactional(readOnly = true)
    public ContractDetailResponseDTO getContractDetail(Long memberId, Long contractId) {

        Contract contract = contractRepository.findContractDetailsByIdAndMemberId(contractId, memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_CONTRACT.getMessage()));

        String repImageUrl = contract.getProduct().getVendor().getRepMedia() != null
                ? mediaService.toCdnUrl(contract.getProduct().getVendor().getRepMedia().getMediaKey())
                : null;

        return ContractDetailResponseDTO.from(contract, repImageUrl);
    }

    public void createSlots(AvailableSlotCreateRequestDTO request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_PRODUCT.getMessage() + " : " + request.productId()));

        final int durationInMinutes = product.getDurationInMinutes();
        if (durationInMinutes <= 0) {
            throw new BadRequestException("상품의 이용 시간이 올바르지 않습니다.");
        }

        List<AvailableSlot> slots = request.startTimes().stream()
                .distinct()
                .map(startTime -> AvailableSlot.builder()
                        .product(product)
                        .startTime(startTime)
                        .endTime(startTime.plusMinutes(durationInMinutes))
                        .build())
                .toList();

        availableSlotRepository.saveAll(slots);

        log.info("{}개의 계약 가능 슬롯이 상품 '{}'(ID:{})에 성공적으로 등록되었습니다.",
                slots.size(), product.getName(), product.getId());
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
