package LinkerBell.campus_market_spring.service;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.ALREADY_SOLD_OUT_ITEM;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.DO_NOT_ROLL_BACK_ITEM_STATUS_FOR_SALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemPhotos;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ItemStatusChangeRequestDto;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.ChatRoomRepository;
import LinkerBell.campus_market_spring.repository.ItemPhotosRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.LikeRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CampusRepository campusRepository;

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    LikeRepository likeRepository;

    @Mock
    ItemPhotosRepository itemPhotosRepository;

    List<Item> items;
    List<User> users;
    List<Campus> campuses;
    List<Like> likes;
    List<ChatRoom> chatRooms;
    List<ItemPhotos> itemPhotos;

    @BeforeEach
    void beforeEach() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        items = new ArrayList<>();
        users = new ArrayList<>();
        campuses = new ArrayList<>();
        likes = new ArrayList<>();
        chatRooms = new ArrayList<>();
        itemPhotos = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Campus campus = Campus.builder()
                .universityName("campus" + i)
                .region("수원")
                .email("abc" + i).build();
            campuses.add(campus);
        }

        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                .nickname("user" + i)
                .campus(i < 1 ? campuses.get(0) : campuses.get(1))
                .role(Role.USER)
                .build();
            users.add(user);
        }

        for (int i = 0; i < 25; i++) {
            Item item;
            if (i < 5) {
                item = Item.builder()
                    .user(users.get(0))
                    .campus(campuses.get(0))
                    .category(Category.ELECTRONICS_IT)
                    .title("itemFirst" + i)
                    .price(i)
                    .thumbnail("https://defaultImage.com")
                    .build();
            } else if (i < 15) {
                item = Item.builder()
                    .user(users.get(1))
                    .campus(campuses.get(1))
                    .category(Category.BOOKS_EDUCATIONAL_MATERIALS)
                    .title("itemSecond" + i)
                    .price(i)
                    .thumbnail("https://defaultImage.com")
                    .build();
            } else {
                item = Item.builder()
                    .user(users.get(2))
                    .campus(campuses.get(1))
                    .category(Category.FASHION_ACCESSORIES)
                    .title("itemThird" + i)
                    .price(1)
                    .thumbnail("https://defaultImage.com")
                    .build();
            }
            item.setCreatedDate(LocalDateTime.of(2023, 10, 31, 0, 0).plusSeconds(i * 10));
            items.add(item);
        }
        int j = 0;
        for (int i = 0; i < 32; i++) {
            ItemPhotos itemPhoto = ItemPhotos.builder()
                .imageAddress("https://testImage" + i)
                .item(items.get(j))
                .build();
            if ((i + 1) % 2 == 0) {
                j++;
            }
            itemPhotos.add(itemPhoto);
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
            chatRooms.add(chatRoom);
        }

    }

    @Test
    @DisplayName("itemStatus가 이미 SOLD_OUT 일때, 테스트")
    public void changeItemStatusAlreadySoldOutTest() throws Exception {
        //given
        User user = users.get(1);
        Item item = items.get(5);
        User userBuyer = users.get(3);
        item.setItemStatus(ItemStatus.SOLDOUT);
        ItemStatusChangeRequestDto itemStatusChangeRequestDto = ItemStatusChangeRequestDto.builder()
            .itemStatus(ItemStatus.SOLDOUT)
            .buyerId(userBuyer.getUserId())
            .build();

        //when
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findById(userBuyer.getUserId())).thenReturn(Optional.of(userBuyer));

        //then
        assertThatThrownBy(() -> {
            itemService.changeItemStatus(user.getUserId(), item.getItemId(),
                itemStatusChangeRequestDto);
        }).isInstanceOf(CustomException.class)
            .hasMessageContaining(ALREADY_SOLD_OUT_ITEM.getMessage());

    }

    @Test
    @DisplayName("item의 구매자가 이미 있을 때 테스트")
    public void changeItemStatusWithExistingItemBuyerTest() throws Exception {
        //given
        User user = users.get(1);
        Item item = items.get(5);
        User userBuyer = users.get(3);
        item.setUserBuyer(userBuyer);
        ItemStatusChangeRequestDto itemStatusChangeRequestDto = ItemStatusChangeRequestDto.builder()
            .itemStatus(ItemStatus.SOLDOUT)
            .buyerId(userBuyer.getUserId())
            .build();

        //when
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findById(userBuyer.getUserId())).thenReturn(Optional.of(userBuyer));

        //then
        assertThatThrownBy(() -> {
            itemService.changeItemStatus(user.getUserId(), item.getItemId(),
                itemStatusChangeRequestDto);
        }).isInstanceOf(CustomException.class)
            .hasMessageContaining(ALREADY_SOLD_OUT_ITEM.getMessage());
    }

    @Test
    @DisplayName("변경하길 원하는 item 상태가 SOLD_OUT이 아닐때 ")
    public void ChangingItemStatusIsNotSoldOutTest() throws Exception {
        //given
        User user = users.get(1);
        Item item = items.get(5);
        User userBuyer = users.get(3);
        ItemStatusChangeRequestDto itemStatusChangeRequestDto = ItemStatusChangeRequestDto.builder()
            .itemStatus(ItemStatus.FORSALE)
            .buyerId(userBuyer.getUserId())
            .build();

        //when
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findById(userBuyer.getUserId())).thenReturn(Optional.of(userBuyer));

        //then
        assertThatThrownBy(() -> {
            itemService.changeItemStatus(user.getUserId(), item.getItemId(),
                itemStatusChangeRequestDto);
        }).isInstanceOf(CustomException.class)
            .hasMessageContaining(DO_NOT_ROLL_BACK_ITEM_STATUS_FOR_SALE.getMessage());
    }

    @Test
    @DisplayName("정상적인 item 변경 상태 테스트 ")
    public void DefaultItemStatusChangeTest() throws Exception {
        //given
        User user = users.get(1);
        Item item = items.get(5);
        User userBuyer = users.get(3);
        ItemStatusChangeRequestDto itemStatusChangeRequestDto = ItemStatusChangeRequestDto.builder()
            .itemStatus(ItemStatus.SOLDOUT)
            .buyerId(userBuyer.getUserId())
            .build();

        //when
        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.findById(userBuyer.getUserId())).thenReturn(Optional.of(userBuyer));
        when(chatRoomRepository.existsByUser_userIdAndItem_itemId(userBuyer.getUserId(),
            item.getItemId()))
            .thenReturn(true);

        //then
        itemService.changeItemStatus(user.getUserId(), item.getItemId(),
            itemStatusChangeRequestDto);

        assertThat(item.getItemStatus()).isEqualTo(itemStatusChangeRequestDto.getItemStatus());
        assertThat(item.getUserBuyer()).isEqualTo(userBuyer);
    }
}
