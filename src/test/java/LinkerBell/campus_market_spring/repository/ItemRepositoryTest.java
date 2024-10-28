package LinkerBell.campus_market_spring.repository;

import LinkerBell.campus_market_spring.domain.*;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.config.QuerydslConfig;
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

@DataJpaTest
@Import(QuerydslConfig.class)
@Transactional
//@Rollback(false)
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
            System.out.println(" = " + campus.getCampusId());
            campuses.add(campus);
        }
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .nickname("user" + i)
                    .campus(i < 1 ? campuses.get(0) : campuses.get(1))
                    .role(Role.USER)
                    .build();
            em.persist(user);
            System.out.println("user = " + user.getUserId());
            users.add(user);
        }
        for (int i = 0; i < 10; i++) {
            Item item;
            if (i < 5) {
                item = Item.builder()
                        .user(users.get(0))
                        .campus(campuses.get(0))
                        .category(Category.ELECTRONICS)
                        .title("item" + i)
                        .price(i)
                        .build();
            } else {
                item = Item.builder()
                        .user(users.get(1))
                        .campus(campuses.get(1))
                        .category(Category.BOOKS)
                        .title("item" + i)
                        .price(i)
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
    @DisplayName("모든 값이 null일 때, test")
    public void defaultTest() throws Exception {
        //given
        Long userId = 1L;
        String name = null;
        Category category = null;
        Integer minPrice = 0;
        Integer maxPrice = Integer.MAX_VALUE;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0,2,sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(userId, name, category, minPrice, maxPrice, pageRequest);

        //then

        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(items.get(4).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(users.get(0).getNickname());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(items.get(3).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(1);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(users.get(0).getNickname());
    }


}