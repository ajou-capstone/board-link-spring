package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.LikeDeleteResponseDto;
import LinkerBell.campus_market_spring.dto.LikeResponseDto;
import LinkerBell.campus_market_spring.dto.LikeSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.LikeRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    LikeRepository likeRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    LikeService likeService;

    private User user;
    private User other;
    private Item item;
    private Item item2;
    private Item item3;

    @BeforeEach
    public void setUp() {
        user = createUser(1L);
        other = createUser(2L);
        item = createItem(other, 1L);
        item2 = createItem(other, 2L);
        item3 = createItem(other, 3L);
    }

    @Test
    @DisplayName("좋아요 추가하기 테스트")
    public void doLikesTest() {
        // given
        Like like = new Like(1L, user, item);

        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.ofNullable(item));
        given(likeRepository.findByUserAndItem(any(User.class), any(Item.class)))
            .willReturn(Optional.empty());
        given(likeRepository.save(any(Like.class))).willReturn(like);
        // when
        LikeResponseDto responseDto = likeService.likeItem(user.getUserId(), item.getItemId());

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getItemId()).isEqualTo(item.getItemId());
        assertThat(responseDto.getLikeId()).isEqualTo(like.getLikeId());
        assertThat(responseDto.isLike()).isTrue();
    }

    @Test
    @DisplayName("좋아요 추가하기 테스트 그러나 이미 좋아요 기록이 있는 경우")
    public void doLikesTestButLikedBefore() {
        // given
        Like like = new Like(1L, user, item);

        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.ofNullable(item));
        given(likeRepository.findByUserAndItem(any(User.class), any(Item.class)))
            .willReturn(Optional.ofNullable(like));

        // when
        LikeResponseDto responseDto = likeService.likeItem(user.getUserId(), item.getItemId());

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getItemId()).isEqualTo(item.getItemId());
        assertThat(responseDto.getLikeId()).isEqualTo(like.getLikeId());
        assertThat(responseDto.isLike()).isTrue();
    }

    @Test
    @DisplayName("좋아요 취소하기 테스트")
    public void deleteLikeTest() {
        // given
        Like like = new Like(1L, user, item);
        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.ofNullable(item));
        given(likeRepository.findByUserAndItem(any(User.class), any(Item.class)))
            .willReturn(Optional.ofNullable(like));
        // when
        LikeDeleteResponseDto responseDto = likeService.deleteLike(user.getUserId(), item.getItemId());
        // then
        then(likeRepository).should(times(1)).deleteById(assertArg( id -> {
            assertThat(id).isEqualTo(like.getLikeId());
        }));
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getItemId()).isEqualTo(item.getItemId());
        assertThat(responseDto.isLike()).isFalse();
    }

    @Test
    @DisplayName("좋아요 취소하기 테스트 + 좋아요 기록이 없을 경우")
    public void deleteEmptyLikeTest() {
        // given

        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(itemRepository.findById(anyLong())).willReturn(Optional.ofNullable(item));
        given(likeRepository.findByUserAndItem(any(User.class), any(Item.class)))
            .willReturn(Optional.empty());
        // when
        LikeDeleteResponseDto responseDto = likeService.deleteLike(user.getUserId(), item.getItemId());
        // then
        then(likeRepository).should(times(0)).deleteById(anyLong());

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getItemId()).isEqualTo(item.getItemId());
        assertThat(responseDto.isLike()).isFalse();
    }

    private User createUser(Long id) {
        return User.builder()
            .userId(id)
            .role(Role.USER)
            .loginEmail("user" + id + "@example.com")
            .schoolEmail("user" + id + "@ajou.ac.kr")
            .nickname("i'm user" + id)
            .build();
    }

    private Item createItem(User user, Long id) {
        return Item.builder()
            .itemId(id)
            .category(Category.ELECTRONICS_IT)
            .price(40000 + id.intValue())
            .title("dummy item" + id)
            .description("for testing" + id)
            .itemStatus(ItemStatus.FORSALE)
            .thumbnail("default image" + id)
            .user(user)
            .build();
    }
}
