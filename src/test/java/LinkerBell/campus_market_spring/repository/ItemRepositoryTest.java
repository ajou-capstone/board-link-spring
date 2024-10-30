package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.*;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.config.QuerydslConfig;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(QuerydslConfig.class)
@Transactional
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    List<Item> items;
    List<User> users;
    List<Campus> campuses;
    List<Like> likes;
    List<ChatRoom> chatRooms;

    @BeforeEach
    void beforeEach() {
        items = new ArrayList<>();
        users = new ArrayList<>();
        campuses = new ArrayList<>();
        likes = new ArrayList<>();
        chatRooms = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Campus campus = Campus.builder()
                    .universityName("campus" + i)
                    .region("수원")
                    .email("abc" + i).build();
            em.persist(campus);
            campuses.add(campus);
        }

        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .nickname("user" + i)
                    .campus(i < 1 ? campuses.get(0) : campuses.get(1))
                    .role(Role.USER)
                    .build();
            em.persist(user);
            users.add(user);
        }

        User noCampusUser = User.builder()
                .nickname("user5")
                .role(Role.USER)
                .build();
        em.persist(noCampusUser);
        users.add(noCampusUser);

        for (int i = 0; i < 25; i++) {
            Item item;
            if (i < 5) {
                item = Item.builder()
                        .user(users.get(0))
                        .campus(campuses.get(0))
                        .category(Category.ELECTRONICS)
                        .title("itemFirst" + i)
                        .price(i)
                        .build();
            } else if (i < 15) {
                item = Item.builder()
                        .user(users.get(1))
                        .campus(campuses.get(1))
                        .category(Category.BOOKS)
                        .title("itemSecond" + i)
                        .price(i)
                        .build();
            } else {
                item = Item.builder()
                        .user(users.get(2))
                        .campus(campuses.get(1))
                        .category(Category.FASHION)
                        .title("itemThird" + i)
                        .price(1)
                        .build();
            }
            em.persist(item);
            System.out.println("item = " + item.getItemId());
            items.add(item);
        }

        for (int i = 2; i < 5; i++) {
            Like like;
            if (i < 4) {
                like = Like.builder()
                        .item(items.get(0))
                        .user(users.get(i))
                        .build();
            } else {
                like = Like.builder()
                        .item(items.get(1))
                        .user(users.get(i))
                        .build();
            }
            em.persist(like);
            System.out.println("like = " + like.getLikeId());
            likes.add(like);
        }

        for (int i = 2; i < 5; i++) {
            ChatRoom chatRoom;
            if (i < 4) {
                chatRoom = ChatRoom.builder()
                        .item(items.get(2))
                        .user(users.get(i))
                        .build();
            } else {
                chatRoom = ChatRoom.builder()
                        .item(items.get(3))
                        .user(users.get(i))
                        .build();
            }
            em.persist(chatRoom);
            System.out.println("chatRoom = " + chatRoom.getChatRoomId());
            chatRooms.add(chatRoom);
        }

    }

    @Test
    @DisplayName("모든 값이 null이고 최신순 정렬일 때 test, 특정 캠퍼스, 좋아요, chat개수 확인 포함 test")
    public void defaultSearchTest() throws Exception {
        //given
        Long userId = users.get(0).getUserId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(4).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(0).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(4).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(3).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(1);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(0).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(3).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("캠퍼스 없는 유저에 대한 test")
    public void noCampusUserSearchTest() throws Exception {
        //given
        Long userId = users.get(5).getUserId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        assertThatThrownBy(() -> {
            itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);
        }).isInstanceOf(CustomException.class)
                .hasMessageContaining("캠퍼스가 존재하지 않습니다.");

        //then

    }

    @Test
    @DisplayName("특정 이름으로 검색하는 test 실행")
    public void itemNameSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getUserId();
        String name = "sec";
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("특정 카테고리로 검색하는 test 실행")
    public void itemCategorySearchTest() throws Exception {
        //given
        Long userId = users.get(1).getUserId();
        String name = null;
        Category category = Category.BOOKS;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("가격 오름차순으로 검색하는 test 실행 또한 같은 가격이면 가장 최근에 등록된 아이템부터 보여준다.")
    public void itemAscendingPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getUserId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("price").ascending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(24).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(2).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(24).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(23).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(2).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(23).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "price"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("가격 내림차순으로 검색하는 test 실행")
    public void itemDescendingPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getUserId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("price").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "price"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("최소 가격에서 최대 가격 사이의 값 출력")
    public void itemMinPriceBetweenMaxPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getUserId();
        String name = null;
        Category category = null;
        Integer minPrice = 6;
        Integer maxPrice = 7;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(7).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(items.get(7).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(6).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(items.get(6).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isFalse();
    }


}