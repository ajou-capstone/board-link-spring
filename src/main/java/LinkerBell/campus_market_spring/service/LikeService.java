package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.LikeDeleteResponseDto;
import LinkerBell.campus_market_spring.dto.LikeResponseDto;
import LinkerBell.campus_market_spring.dto.LikeSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.LikeRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public LikeResponseDto likeItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        Like like = likeRepository.findByUserAndItem(user, item)
            .orElseGet(() -> {
                Like newLike = Like.builder()
                    .user(user).item(item).build();
                return likeRepository.save(newLike);
            });

        return LikeResponseDto.builder()
            .isLike(true)
            .likeId(like.getLikeId())
            .itemId(itemId).build();
    }

    public SliceResponse<LikeSearchResponseDto> getLikes(Long userId, Pageable pageable) {
        return likeRepository.findAllByUserId(userId, pageable);
    }

    public LikeDeleteResponseDto deleteLike(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        likeRepository.findByUserAndItem(user, item).ifPresent(like -> {
            likeRepository.deleteById(like.getLikeId());
        });

        return LikeDeleteResponseDto.builder()
            .itemId(itemId)
            .isLike(false)
            .build();
    }
}
