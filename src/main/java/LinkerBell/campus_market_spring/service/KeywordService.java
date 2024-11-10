package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.repository.KeywordRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public List<Keyword> findKeywordsWithSameItemCampusAndTitle(Item savedItem) {
        return keywordRepository.findKeywordsWithUserAndCampus()
            .stream()
            .filter(k -> k.getUser().getCampus() != null)
            .filter(k -> Objects.equals(k.getUser().getCampus().getCampusId(),
                savedItem.getCampus().getCampusId()))
            .filter(k -> savedItem.getTitle().contains(k.getKeywordName()))
            .toList();
    }
}
