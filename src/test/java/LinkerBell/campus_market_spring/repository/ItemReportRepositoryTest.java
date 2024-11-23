package LinkerBell.campus_market_spring.repository;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import LinkerBell.campus_market_spring.domain.Role;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;

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
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        List<ItemReport> itemReports = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ItemReport itemReport = ItemReport.builder()
                .isCompleted(i % 2 == 0).user(user).item(item)
                .description("test reason " + i)
                .category(ItemReportCategory.values()[(i - 1) % ItemReportCategory.values().length]).build();
            itemReport.setCreatedDate(LocalDateTime.of(2024, LocalDate.now().getMonth(), 30 - i,
                0, 0));
            itemReports.add(itemReport);
        }
        itemReports = itemReportRepository.saveAll(itemReports);
        int i = 0;
        for(ItemReport itemReport : itemReports) {
            LocalDateTime date = LocalDateTime.of(2023, LocalDate.now().getMonth(), 25 - i, 0 ,0);
            Timestamp timestamp = Timestamp.valueOf(date);
            String sql = "UPDATE item_report SET created_date=? WHERE item_report_id= ?;";
            jdbcTemplate.update(sql, timestamp, itemReport.getItemReportId());
            i += 1;
            itemReport.setCreatedDate(date);
            itemReportRepository.save(itemReport);
        }

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

    @Test
    @DisplayName("상품 신고 리스트 테스트")
    public void getItemReportListTest() {
        // given
        Sort sort = Sort.by(Direction.DESC, "createdDate")
            .and(Sort.by(Direction.DESC, "itemReportId"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        // when
        Slice<ItemReport> slice = itemReportRepository.findItemReports(pageable);

        // then
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().size()).isEqualTo(10);
        assertThat(slice.getContent().get(0).getCategory()).isEqualTo(ItemReportCategory.values()[0]);
        assertThat(slice.getContent().get(0).getCreatedDate()).isAfter(slice.getContent().get(1).getCreatedDate());
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
