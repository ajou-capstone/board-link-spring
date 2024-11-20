package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemReport;
import LinkerBell.campus_market_spring.domain.ItemReportCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemReportRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemReportRepository itemReportRepository;

    public void reportItem(Long userId, Long itemId, String description,
        ItemReportCategory category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (user.getCampus() != item.getUser().getCampus()) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_CAMPUS_WITH_ITEM_CAMPUS);
        }

        ItemReport itemReport = ItemReport.builder()
            .item(item).description(description)
            .category(category).user(user)
            .isCompleted(false).build();

        itemReportRepository.save(itemReport);
    }
}
