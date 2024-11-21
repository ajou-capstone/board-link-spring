package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserReport;
import LinkerBell.campus_market_spring.domain.UserReportCategory;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserReportRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.utils.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemReportRepository itemReportRepository;
    private final UserReportRepository userReportRepository;

    public void reportItem(Long userId, Long itemId, String description,
        ItemReportCategory category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (Objects.equals(userId, item.getUser().getUserId())) {
            throw new CustomException(ErrorCode.NOT_REPORT_OWN);
        }

        validateEqualUniversity(user.getCampus(), item.getCampus());

        ItemReport itemReport = ItemReport.builder()
            .item(item).description(description)
            .category(category).user(user)
            .isCompleted(false).build();

        itemReportRepository.save(itemReport);
    }

    public void reportUser(Long userId, Long targetId, String description,
        UserReportCategory category) {
        if (Objects.equals(userId, targetId)) {
            throw new CustomException(ErrorCode.NOT_REPORT_OWN);
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User target = userRepository.findById(targetId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        validateEqualUniversity(user.getCampus(), target.getCampus());

        UserReport userReport = UserReport.builder()
            .user(user).target(target)
            .category(category)
            .description(description)
            .isCompleted(false).build();

        userReportRepository.save(userReport);
    }

    private void validateEqualUniversity(Campus userCampus, Campus targetCampus) {
        if (userCampus == null || targetCampus == null) {
            throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
        }
        if (!StringUtils.equals(userCampus.getUniversityName(), targetCampus.getUniversityName())) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_CAMPUS);
        }
    }
}
