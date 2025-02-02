package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.DealHistoryResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.ReviewRepository;
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
    private final ReviewRepository reviewRepository;

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

        Slice<DealHistoryResponseDto> allDealHistory = getDealHistoryResponseDtos(
            itemRepository.findAllHistoryByUser(
                requestedUser, pageable), requestedUser);

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

        Slice<DealHistoryResponseDto> purchaseHistory = getDealHistoryResponseDtos(
            itemRepository.findPurchaseHistoryByUser(
                requestedUser, pageable), requestedUser);

        return new SliceResponse<>(purchaseHistory);
    }

    public SliceResponse<DealHistoryResponseDto> getSalesDealHistory(Long requestedUserId,
        Pageable pageable) {

        User requestedUser = userRepository.findById(requestedUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Slice<DealHistoryResponseDto> salesHistory = getDealHistoryResponseDtos(
            itemRepository.findSalesHistoryByUser(
                requestedUser, pageable), requestedUser);

        return new SliceResponse<>(salesHistory);
    }

    private Slice<DealHistoryResponseDto> getDealHistoryResponseDtos(Slice<Item> itemRepository,
        User requestedUser) {
        Slice<DealHistoryResponseDto> dealHistory = itemRepository.map(item -> {
                ItemStatus itemStatus = item.getItemStatus();
                Long buyerId;

                // isReviewed, buyerId 결정하기
                boolean isReviewed = false;

                if (itemStatus == ItemStatus.SOLDOUT) { // 팔린 아이템일 때
                    if (reviewRepository.existsByUserAndItem(requestedUser,
                        item)) { // 그 아이템이랑 유저의 리뷰가 있으면
                        isReviewed = true;
                    }
                    buyerId = item.getUserBuyer().getUserId();
                } else { // 안팔렸으면
                    buyerId = -1L; // 구매자가 없으므로
                }

                return DealHistoryResponseDto.builder()
                    .itemId(item.getItemId())
                    .title(item.getTitle())
                    .userId(item.getUser().getUserId())
                    .buyerId(buyerId)
                    .nickname(item.getUser().getNickname())
                    .price(item.getPrice())
                    .thumbnail(item.getThumbnail())
                    .itemStatus(itemStatus)
                    .createdAt(item.getCreatedDate())
                    .modifiedAt(item.getLastModifiedDate())
                    .isReviewed(isReviewed)
                    .build();
            }
        );
        return dealHistory;
    }
}
