package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.DealHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DealHistoryService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public SliceResponse<DealHistoryResponseDto> getAllDealHistory(Long loginUserId,
        Long requestedUserId, Pageable pageable) {
        User loginUser = userRepository.findById(loginUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User requestedUser = userRepository.findById(requestedUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!loginUserId.equals(requestedUserId)) {
            log.error("loginUserId : {}, requestedUserId : {}", loginUserId, requestedUserId);
            throw new CustomException(ErrorCode.NOT_MATCH_LOGIN_USER_AND_REQUESTED_USER);
        }

        Slice<DealHistoryResponseDto> allDealHistory = itemRepository.findAllHistoryByUser(
            requestedUser, pageable).map(item -> DealHistoryResponseDto.builder()
            .itemId(item.getItemId())
            .title(item.getTitle())
            .price(item.getPrice())
            .thumbnail(item.getThumbnail())
            .itemStatus(item.getItemStatus())
            .build()
        );

        return new SliceResponse<>(allDealHistory);
    }

    public SliceResponse<DealHistoryResponseDto> getPurchaseDealHistory(Long loginUserId,
        Long requestedUserId, Pageable pageable) {
        User loginUser = userRepository.findById(loginUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User requestedUser = userRepository.findById(requestedUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!loginUserId.equals(requestedUserId)) {
            log.error("loginUserId : {}, requestedUserId : {}", loginUserId, requestedUserId);
            throw new CustomException(ErrorCode.NOT_MATCH_LOGIN_USER_AND_REQUESTED_USER);
        }

        Slice<DealHistoryResponseDto> purchaseHistory = itemRepository.findPurchaseHistoryByUser(
            requestedUser, pageable).map(item -> DealHistoryResponseDto.builder()
            .itemId(item.getItemId())
            .title(item.getTitle())
            .price(item.getPrice())
            .thumbnail(item.getThumbnail())
            .itemStatus(item.getItemStatus())
            .build()
        );

        return new SliceResponse<>(purchaseHistory);
    }

    public SliceResponse<DealHistoryResponseDto> getSalesDealHistory(Long requestedUserId,
        Pageable pageable) {

        User requestedUser = userRepository.findById(requestedUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Slice<DealHistoryResponseDto> salesHistory = itemRepository.findSalesHistoryByUser(
            requestedUser, pageable).map(item -> DealHistoryResponseDto.builder()
            .itemId(item.getItemId())
            .title(item.getTitle())
            .price(item.getPrice())
            .thumbnail(item.getThumbnail())
            .itemStatus(item.getItemStatus())
            .build()
        );

        return new SliceResponse<>(salesHistory);
    }
}
