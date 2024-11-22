package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.NotificationHistory;
import LinkerBell.campus_market_spring.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@DataJpaTest
class NotificationHistoryRepositoryTest {

    @Autowired
    NotificationHistoryRepository notificationHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    List<User> users;
    List<Item> items;
    List<NotificationHistory> notificationHistories;

    @BeforeEach
    void beforeEach() {
        users = new ArrayList<>();
        items = new ArrayList<>();
        notificationHistories = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            User user = User.builder()
                .nickname("user" + i)
                .build();
            userRepository.save(user);
            users.add(user);
        }

        for (int i = 0; i < 2; i++) {
            Item item = Item.builder()
                .title("item" + i)
                .build();
            itemRepository.save(item);
            items.add(item);
        }
        for (int i = 0; i < 6; i++) {
            NotificationHistory notificationHistory;
            if (i < 3) {
                notificationHistory = NotificationHistory.builder()
                    .user(users.get(0))
                    .item(items.get(0))
                    .title("notificationHistory" + i)
                    .build();
            } else {
                notificationHistory = NotificationHistory.builder()
                    .user(users.get(1))
                    .item(items.get(1))
                    .title("notificationHistory" + i)
                    .build();
            }
            notificationHistoryRepository.save(notificationHistory);
            notificationHistories.add(notificationHistory);
        }

    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        notificationHistoryRepository.deleteAll();
    }

    @Test
    @DisplayName("userId를 검색해서 첫 페이지 알림 히스토리 정보를 가져오는 테스트")
    public void findByUser_UserIdFirstTest() throws Exception {
        //given
        int page = 0;
        int size = 2;
        Sort sort = Sort.by(Sort.Order.desc("createdDate"),
            Sort.Order.desc("notificationHistoryId"));
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Slice<NotificationHistory> slice = notificationHistoryRepository.findByUser_UserId(
            users.get(0).getUserId(), pageable);

        //then
        assertThat(slice.getContent().size()).isEqualTo(size);
        assertThat(slice.getContent().get(0)).isEqualTo(notificationHistories.get(2));
        assertThat(slice.getContent().get(1)).isEqualTo(notificationHistories.get(1));
        assertThat(slice.getPageable()).isEqualTo(pageable);
        assertThat(slice.getPageable().hasPrevious()).isFalse();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("userId를 검색해서 두번쨰 알림 히스토리 정보를 가져오는 테스트")
    public void findByUser_UserIdSecondTest() throws Exception {
        //given
        int page = 1;
        int size = 2;
        Sort sort = Sort.by(Sort.Order.desc("createdDate"),
            Sort.Order.desc("notificationHistoryId"));
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Slice<NotificationHistory> slice = notificationHistoryRepository.findByUser_UserId(
            users.get(0).getUserId(), pageable);

        //then
        assertThat(slice.getContent().size()).isEqualTo(size - 1);
        assertThat(slice.getContent().get(0)).isEqualTo(notificationHistories.get(0));
        assertThat(slice.getPageable()).isEqualTo(pageable);
        assertThat(slice.getPageable().hasPrevious()).isTrue();
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("notificationHistoryId를 사용해서 notificationHistory 가져오는 테스트")
    public void findByIdWithUserAndItem() throws Exception {
        //given
        NotificationHistory notificationHistory = notificationHistoryRepository.findByIdWithUserAndItem(
                notificationHistories.get(0).getNotificationHistoryId())
            .orElseGet(() -> fail("실패입니다."));
        //when

        //then
        assertThat(notificationHistory).isEqualTo(notificationHistories.get(0));

    }

    @Test
    @DisplayName("userId로 delete되는지 테스트")
    public void deleteByUserId() throws Exception {
        //given

        //when
        int deleteCount = notificationHistoryRepository.deleteByUserId(users.get(0).getUserId());
        //then
        assertThat(deleteCount).isEqualTo(3);
        em.flush();
        em.clear();

        assertThat(notificationHistoryRepository.findById(
            notificationHistories.get(0).getNotificationHistoryId()))
            .isEmpty();
        assertThat(notificationHistoryRepository.findById(
            notificationHistories.get(1).getNotificationHistoryId()))
            .isEmpty();
        assertThat(notificationHistoryRepository.findById(
            notificationHistories.get(2).getNotificationHistoryId()))
            .isEmpty();
    }


}