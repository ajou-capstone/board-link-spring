package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.domain.ChatRoom;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemPhotos;
import LinkerBell.campus_market_spring.domain.ItemStatus;
import LinkerBell.campus_market_spring.domain.Like;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.ItemDetailsViewResponseDto;
import LinkerBell.campus_market_spring.dto.ItemSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
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
            campusRepository.save(campus);
            campuses.add(campus);
        }

        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                .nickname("user" + i)
                .campus(i < 1 ? campuses.get(0) : campuses.get(1))
                .role(Role.USER)
                .build();
            userRepository.save(user);
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
            itemRepository.save(item);
            item.setCreatedDateManually(LocalDateTime.of(2023, 10, 31, 0, 0).plusSeconds(i * 10));
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
            itemPhotosRepository.save(itemPhoto);
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
            likeRepository.save(like);
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
            chatRoomRepository.save(chatRoom);
            chatRooms.add(chatRoom);
        }

    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        campusRepository.deleteAll();
        chatRoomRepository.deleteAll();
        likeRepository.deleteAll();
        itemPhotosRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 값이 null이고 최신순 정렬일 때 test, 특정 캠퍼스, 좋아요, chat개수 확인 포함 test")
    public void defaultSearchTest() throws Exception {
        //given
        Long userId = users.get(0).getCampus().getCampusId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(4).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(0).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(4).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(3).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            1);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(0).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(3).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("특정 이름으로 검색하는 test 실행")
    public void itemNameSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getCampus().getCampusId();
        String name = "sec";
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("특정 카테고리로 검색하는 test 실행")
    public void itemCategorySearchTest() throws Exception {
        //given
        Long userId = users.get(1).getCampus().getCampusId();
        String name = null;
        Category category = Category.BOOKS_EDUCATIONAL_MATERIALS;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("가격 오름차순으로 검색하는 test 실행 또한 같은 가격이면 가장 최근에 등록된 아이템부터 보여준다.")
    public void itemAscendingPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getCampus().getCampusId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("price").ascending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(24).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(2).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(24).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(23).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(2).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(23).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.ASC, "price"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("가격 내림차순으로 검색하는 test 실행")
    public void itemDescendingPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getCampus().getCampusId();
        String name = null;
        Category category = null;
        Integer minPrice = null;
        Integer maxPrice = null;
        Sort sort = Sort.by("price").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(14).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(14).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(13).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(13).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.DESC, "price"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("최소 가격에서 최대 가격 사이의 값 출력")
    public void itemMinPriceBetweenMaxPriceSearchTest() throws Exception {
        //given
        Long userId = users.get(1).getCampus().getCampusId();
        String name = null;
        Category category = null;
        Integer minPrice = 6;
        Integer maxPrice = 7;
        Sort sort = Sort.by("createdDate").descending();
        PageRequest pageRequest = PageRequest.of(0, 2, sort);
        //when
        SliceResponse<ItemSearchResponseDto> itemSearchResponseDtoSliceResponse = itemRepository.itemSearch(
            userId, name, category, minPrice, maxPrice, pageRequest);

        //then
        assertThat(itemSearchResponseDtoSliceResponse.getContent().size()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getTitle()).isEqualTo(
            items.get(7).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(0).getPrice()).isEqualTo(
            items.get(7).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getTitle()).isEqualTo(
            items.get(6).getTitle());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getLikeCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getChatCount()).isEqualTo(
            0);
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getNickname()).isEqualTo(
            users.get(1).getNickname());
        assertThat(itemSearchResponseDtoSliceResponse.getContent().get(1).getPrice()).isEqualTo(
            items.get(6).getPrice());

        assertThat(itemSearchResponseDtoSliceResponse.getCurrentPage()).isEqualTo(0);
        assertThat(itemSearchResponseDtoSliceResponse.getSize()).isEqualTo(2);
        assertThat(itemSearchResponseDtoSliceResponse.getSort()).isEqualTo(
            Sort.by(Sort.Direction.DESC, "createdDate"));
        assertThat(itemSearchResponseDtoSliceResponse.isHasPrevious()).isFalse();
        assertThat(itemSearchResponseDtoSliceResponse.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("아이템 저장 코드 테스트")
    public void saveItemTest() throws Exception {
        //given
        Item item = Item.builder()
            .user(users.get(0))
            .campus(campuses.get(0))
            .category(Category.ELECTRONICS_IT)
            .title("savedItem")
            .price(10000)
            .build();
        //when
        Item savedItem = itemRepository.save(item);

        //then
        assertThat(savedItem.getItemId()).isEqualTo(item.getItemId());
    }

    @Test
    @DisplayName("아이템 사진들 저장 코드 테스트")
    public void saveItemPhotosTest() throws Exception {
        //given
        List<String> images = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            images.add("imageURL" + i);
        }

        List<ItemPhotos> itemPhotos = images.stream().map(image -> {
            ItemPhotos itemPhoto = new ItemPhotos();
            itemPhoto.registerItemPhotos(items.get(0), image);
            return itemPhoto;
        }).toList();
        //when
        List<ItemPhotos> savedItemPhotos = itemPhotosRepository.saveAll(itemPhotos);
        //then
        for (int i = 0; i < itemPhotos.size(); i++) {
            assertThat(savedItemPhotos.get(i).getImageAddress()).isEqualTo(
                itemPhotos.get(i).getImageAddress());
        }
    }

    @Test
    @DisplayName("기본적인 아이템 디테일 보기 테스트")
    public void DefaultItemDetailsTest() throws Exception {
        //given
        Long userId = users.get(0).getUserId();
        Long itemId = items.get(0).getItemId();
        //when
        ItemDetailsViewResponseDto itemDetails = itemRepository.findByItemDetails(userId, itemId);
        //then
        assertThat(itemDetails.getItemId()).isEqualTo(items.get(0).getItemId());
        assertThat(itemDetails.getUserId()).isEqualTo(items.get(0).getUser().getUserId());
        assertThat(itemDetails.getCampusId()).isEqualTo(items.get(0).getCampus().getCampusId());
        assertThat(itemDetails.getNickname()).isEqualTo(items.get(0).getUser().getNickname());
        assertThat(itemDetails.getTitle()).isEqualTo(items.get(0).getTitle());
        assertThat(itemDetails.getDescription()).isEqualTo(items.get(0).getDescription());
        assertThat(itemDetails.getPrice()).isEqualTo(items.get(0).getPrice());
        assertThat(itemDetails.getCategory()).isEqualTo(items.get(0).getCategory());
        assertThat(itemDetails.getThumbnail()).isEqualTo(items.get(0).getThumbnail());
        assertThat(itemDetails.getImages().size()).isEqualTo(2);
        assertThat(itemDetails.getChatCount()).isEqualTo(0);
        assertThat(itemDetails.getLikeCount()).isEqualTo(2);
        assertThat(itemDetails.isLiked()).isFalse();
        assertThat(itemDetails.getItemStatus()).isEqualTo(items.get(0).getItemStatus());
    }

    @Test
    @DisplayName("내가 좋아요 누른 상품 체크 여부 테스트")
    public void ItemDetailsIsLikedTrueTest() throws Exception {
        //given
        Long userId = users.get(2).getUserId();
        Long itemId = items.get(0).getItemId();
        //when
        ItemDetailsViewResponseDto itemDetails = itemRepository.findByItemDetails(userId, itemId);
        //then
        assertThat(itemDetails.getItemId()).isEqualTo(items.get(0).getItemId());
        assertThat(itemDetails.getUserId()).isEqualTo(items.get(0).getUser().getUserId());
        assertThat(itemDetails.getCampusId()).isEqualTo(items.get(0).getCampus().getCampusId());
        assertThat(itemDetails.getNickname()).isEqualTo(items.get(0).getUser().getNickname());
        assertThat(itemDetails.getTitle()).isEqualTo(items.get(0).getTitle());
        assertThat(itemDetails.getDescription()).isEqualTo(items.get(0).getDescription());
        assertThat(itemDetails.getPrice()).isEqualTo(items.get(0).getPrice());
        assertThat(itemDetails.getCategory()).isEqualTo(items.get(0).getCategory());
        assertThat(itemDetails.getThumbnail()).isEqualTo(items.get(0).getThumbnail());
        assertThat(itemDetails.getImages().size()).isEqualTo(2);
        assertThat(itemDetails.getChatCount()).isEqualTo(0);
        assertThat(itemDetails.getLikeCount()).isEqualTo(2);
        assertThat(itemDetails.isLiked()).isTrue();
        assertThat(itemDetails.getItemStatus()).isEqualTo(items.get(0).getItemStatus());
    }

    @Test
    @DisplayName("image count,chatCount,likeCount 0값 들어와지는지 테스트")
    public void ItemDetailsLikeCountChatCountImagesZeroTest() throws Exception {
        //given
        Long userId = users.get(4).getUserId();
        Long itemId = items.get(23).getItemId();
        //when
        ItemDetailsViewResponseDto itemDetails = itemRepository.findByItemDetails(userId, itemId);
        //then
        assertThat(itemDetails.getItemId()).isEqualTo(items.get(23).getItemId());
        assertThat(itemDetails.getUserId()).isEqualTo(items.get(23).getUser().getUserId());
        assertThat(itemDetails.getCampusId()).isEqualTo(items.get(23).getCampus().getCampusId());
        assertThat(itemDetails.getNickname()).isEqualTo(items.get(23).getUser().getNickname());
        assertThat(itemDetails.getTitle()).isEqualTo(items.get(23).getTitle());
        assertThat(itemDetails.getDescription()).isEqualTo(items.get(23).getDescription());
        assertThat(itemDetails.getPrice()).isEqualTo(items.get(23).getPrice());
        assertThat(itemDetails.getCategory()).isEqualTo(items.get(23).getCategory());
        assertThat(itemDetails.getThumbnail()).isEqualTo(items.get(23).getThumbnail());
        assertThat(itemDetails.getImages().size()).isEqualTo(0);
        assertThat(itemDetails.getChatCount()).isEqualTo(0);
        assertThat(itemDetails.getLikeCount()).isEqualTo(0);
        assertThat(itemDetails.isLiked()).isFalse();
        assertThat(itemDetails.getItemStatus()).isEqualTo(items.get(23).getItemStatus());
    }

    @Test
    @DisplayName("없는 아이템 아이디로 검색시 에러 테스트")
    public void itemNotFoundTest() throws Exception {
        //given
        Long itemId = 9999999L;
        //when
        //then
        assertThatThrownBy(() -> {
            itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        })
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("아이템이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("삭제된 아이템의 경우에 대한 에러 테스트")
    public void deletedItemTest() throws Exception {
        //given
        Item deletedItem = Item.builder()
            .user(users.get(2))
            .campus(campuses.get(1))
            .category(Category.FASHION_ACCESSORIES)
            .title("deletedItem")
            .price(10000)
            .isDeleted(true)
            .build();
        Item savedDeletedItem = itemRepository.save(deletedItem);

        //when
        //then
        assertThatThrownBy(() -> {
            if (savedDeletedItem.isDeleted()) {
                throw new CustomException(ErrorCode.DELETED_ITEM_ID);
            }
        })
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("삭제된 아이템입니다.");
    }

    @Test
    @DisplayName("유저와 아이템의 캠퍼스가 다를 경우에 대한 에러 테스트")
    public void UserCampusIsMatchedByItemCampusTest() throws Exception {
        User user = users.get(0);
        Item item = items.get(5);

        assertThatThrownBy(() -> {
            if (user.getCampus().getCampusId() != item.getCampus().getCampusId()) {
                throw new CustomException(ErrorCode.NOT_MATCH_USER_CAMPUS_WITH_ITEM_CAMPUS);
            }
        })
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("아이템의 캠퍼스와 일치하지 않는 캠퍼스입니다.");
        //then
    }

    @Test
    @DisplayName("아이템 아이디를 통해 아이템 사진들을 가져오는 테스트")
    public void findByItem_itemIdTest() throws Exception {
        //given
        List<ItemPhotos> findItemPhotos = itemPhotosRepository.findByItem_itemId(
            items.get(0).getItemId());
        //when

        //then
        assertThat(findItemPhotos.size()).isEqualTo(2);
        assertThat(findItemPhotos.get(0).getImageAddress()).isEqualTo(
            itemPhotos.get(0).getImageAddress());
        assertThat(findItemPhotos.get(1).getImageAddress()).isEqualTo(
            itemPhotos.get(1).getImageAddress());
    }

    @Test
    @DisplayName("유저 아이디와 등로된 아이템 아이디의 유저가 다른경우 에러 테스트")
    public void NotMatchUserIdWithItemUserIdTest() throws Exception {
        //given
        User user = users.get(1);
        Item item = items.get(0);
        //when

        //then
        assertThatThrownBy(() -> {
            if (user.getUserId() != item.getUser().getUserId()) {
                throw new CustomException(ErrorCode.NOT_MATCH_USER_ID_WITH_ITEM_USER_ID);
            }
        }).isInstanceOf(CustomException.class)
            .hasMessageContaining("해당 아이템 게시글의 작성자가 아닙니다.");
    }

    @Test
    @DisplayName("기존 이미지가 존재하고, 새롭게 업데이트할 아이템 이미지가 존재할 때 테스트")
    public void updatedItemWithExistingItemPhotosAndExistingNewImagesTest() throws Exception {
        //given
        Item item = items.get(0);
        List<ItemPhotos> existingItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        List<String> newImageAddresses = new ArrayList<>();
        newImageAddresses.add(existingItemPhotos.get(0).getImageAddress());
        newImageAddresses.add("https://newImage0");

        //when
        updateItemPhotos(existingItemPhotos, newImageAddresses, item);
        List<ItemPhotos> updatedItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        List<String> updatedItemPhotosUrl = updatedItemPhotos.stream().map(
            ItemPhotos::getImageAddress).toList();
        //then
        assertThat(updatedItemPhotos.size()).isEqualTo(2);
        assertThat(updatedItemPhotosUrl).doesNotContain(
            existingItemPhotos.get(1).getImageAddress());
        assertThat(updatedItemPhotosUrl).contains(existingItemPhotos.get(0).getImageAddress(),
            "https://newImage0");
    }

    @Test
    @DisplayName("기존 이미지가 존재하고, 새롭게 업데이트할 아이템 이미지가 존재하지 않을때 테스트")
    public void updatedItemWithExistingItemPhotosAndNotExistingNewImagesTest() throws Exception {
        //given
        Item item = items.get(0);
        List<ItemPhotos> existingItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        List<String> newImageAddresses = new ArrayList<>();

        //when
        updateItemPhotos(existingItemPhotos, newImageAddresses, item);
        List<ItemPhotos> updatedItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        //then
        assertThat(updatedItemPhotos.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("기존 이미지가 존재하지 않고, 새롭게 업데이트할 아이템 이미지가 존재할 때 테스트")
    public void updatedItemWithNotExistingItemPhotosAndExistingNewImagesTest() throws Exception {
        //given
        Item item = items.get(23);
        List<ItemPhotos> existingItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());

        assertThat(existingItemPhotos.size()).isEqualTo(0);

        List<String> newImageAddresses = new ArrayList<>();
        newImageAddresses.add("https://newImage0");
        newImageAddresses.add("https://newImage1");
        newImageAddresses.add("https://newImage2");

        //when
        updateItemPhotos(existingItemPhotos, newImageAddresses, item);
        List<ItemPhotos> updatedItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        List<String> updatedItemPhotosUrl = updatedItemPhotos.stream().map(
            ItemPhotos::getImageAddress).toList();

        //then
        assertThat(updatedItemPhotos.size()).isEqualTo(3);
        assertThat(updatedItemPhotosUrl).contains("https://newImage0", "https://newImage1",
            "https://newImage2");
    }

    @Test
    @DisplayName("기존 이미지가 존재하지 않고, 새롭게 업데이트할 아이템 이미지가 존재하지 않을 때 테스트")
    public void updatedItemWithNotExistingItemPhotosAndNotExistingNewImagesTest() throws Exception {
        //given
        Item item = items.get(23);
        List<ItemPhotos> existingItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());

        assertThat(existingItemPhotos.size()).isEqualTo(0);

        List<String> newImageAddresses = new ArrayList<>();

        //when
        updateItemPhotos(existingItemPhotos, newImageAddresses, item);
        List<ItemPhotos> updatedItemPhotos = itemPhotosRepository.findByItem_itemId(
            item.getItemId());
        List<String> updatedItemPhotosUrl = updatedItemPhotos.stream().map(
            ItemPhotos::getImageAddress).toList();
        //then
        assertThat(updatedItemPhotos.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 업데이트 테스트")
    public void updatedItemPropertiesTest() throws Exception {
        //given
        Item item = items.get(0);
        String title = "updateTitle";
        String description = "updateDescription";
        Integer price = 100000;
        Category category = Category.HOUSEHOLD_ITEMS;
        String thumbnail = "https://updateThumbnail";

        //when
        item.setTitle(title);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);
        item.setThumbnail(thumbnail);

        Item updatedItem = itemRepository.findById(item.getItemId()).get();

        //then
        assertThat(updatedItem.getTitle()).isEqualTo(title);
        assertThat(updatedItem.getDescription()).isEqualTo(description);
        assertThat(updatedItem.getPrice()).isEqualTo(price);
        assertThat(updatedItem.getCategory()).isEqualTo(category);
        assertThat(updatedItem.getThumbnail()).isEqualTo(thumbnail);
    }

    @Test
    @DisplayName("아이템 삭제 테스트")
    public void deleteItemTest() throws Exception {
        //given
        Item item = items.get(0);
        item.setDeleted(true);

        //when
        Item deletedItem = itemRepository.findById(item.getItemId()).get();

        //then
        assertThat(deletedItem.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("이 아이템의 구매자가 채팅방에 존재하는 경우 테스트")
    public void ExistsChatroomUserAndItemTest() throws Exception {
        //given
        Long buyerId = users.get(2).getUserId();
        Long itemId = items.get(2).getItemId();
        //when
        //then
        assertThat(chatRoomRepository.existsByUser_userIdAndItem_itemId(buyerId,
            itemId)).isTrue();
    }

    @Test
    @DisplayName("이 아이템의 구매자가 채팅방에 존재하지 않는 경우 테스트")
    public void NotExistsChatroomUserAndItemTest() throws Exception {
        //given
        Long buyerId = users.get(4).getUserId();
        Long itemId = items.get(2).getItemId();
        //when
        //then
        assertThat(chatRoomRepository.existsByUser_userIdAndItem_itemId(buyerId,
            itemId)).isFalse();
    }

    @Test
    @DisplayName("정상적인 아이템 거래 상태 변경 테스트")
    public void defaultItemStatusChangeTest() throws Exception {
        //given
        User userBuyer = users.get(2);
        Item item = items.get(2);
        ItemStatus itemStatus = ItemStatus.SOLDOUT;
        //when
        item.setItemStatus(itemStatus);
        item.setUserBuyer(userBuyer);
        Item updatedItem = itemRepository.findById(item.getItemId()).get();
        //then
        assertThat(updatedItem.getItemStatus()).isEqualTo(itemStatus);
        assertThat(updatedItem.getUserBuyer()).isEqualTo(userBuyer);
    }

    private void updateItemPhotos(List<ItemPhotos> existingItemPhotos,
        List<String> newImageAddresses,
        Item item) {

        List<String> existingAddresses = existingItemPhotos.stream()
            .map(ItemPhotos::getImageAddress)
            .toList();

        newImageAddresses.stream()
            .filter(address -> !existingAddresses.contains(address))
            .forEach(address -> {
                ItemPhotos newPhoto = new ItemPhotos();
                newPhoto.registerItemPhotos(item, address);
                itemPhotosRepository.save(newPhoto);
            });

        existingItemPhotos.stream()
            .filter(photo -> !newImageAddresses.contains(photo.getImageAddress()))
            .forEach(photo -> {
                itemPhotosRepository.delete(photo);
            });
    }

}
