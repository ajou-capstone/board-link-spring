package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemReportRepositoryTest {

    @Autowired
    private ItemReportRepository itemReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User other;
    private Item item;
    private Campus campus;

    @BeforeEach
    public void setUp() {
        campus = createCampus();
        campus = campusRepository.save(campus);
        user = createUser(1L, campus);
        other = createUser(2L, campus);
        user = userRepository.save(user);
        other = userRepository.save(other);

        item = createItem(other);
        item = itemRepository.save(item);
    }

    @Test
    @DisplayName("상품 신고 저장 테스트")
    public void saveItemReportTest() {
        // given
        ItemReport itemReport = ItemReport.builder()
            .user(user).item(item).description("test reason").isCompleted(false).build();

        // when
        ItemReport findItemReport = itemReportRepository.save(itemReport);
        // then
        assertThat(findItemReport).isNotNull();
        assertThat(findItemReport).isEqualTo(itemReport);
    }

    private Campus createCampus() {
        return Campus.builder()
            .campusId(1L)
            .email("testUniv@example.com")
            .region("korea")
            .universityName("testUniv")
            .build();
    }

    private User createUser(Long userId, Campus campus) {
        return User.builder()
            .userId(userId)
            .role(Role.USER)
            .loginEmail("testEmail")
            .campus(campus)
            .schoolEmail("testSchool")
            .nickname("user" + userId).build();
    }

    private Item createItem(User user) {
        return Item.builder()
            .campus(user.getCampus())
            .user(user)
            .title("testItem")
            .description("testItemDescription")
            .price(10000000).build();
    }
}
