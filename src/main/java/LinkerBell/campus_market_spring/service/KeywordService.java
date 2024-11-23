package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.Keyword;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.KeywordRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.KeywordRegisterResponseDto;
import LinkerBell.campus_market_spring.dto.KeywordResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.KeywordRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
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
    private final UserRepository userRepository;

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

    public KeywordRegisterResponseDto addKeyword(Long userId,
        KeywordRegisterRequestDto keywordRegisterRequestDto) {
        User user = getUserWithCampus(userId);
        String keywordName = keywordRegisterRequestDto.getKeywordName();
        List<Keyword> savedKeywords = keywordRepository.findKeywordByUser_UserId(userId);

        validateSavedKeywordCount(savedKeywords);

        validateDuplicateKeywordName(savedKeywords, keywordName);

        Keyword newKeyword = createNewKeyword(keywordName, user);

        Keyword savedKeyword = keywordRepository.save(newKeyword);
        return KeywordRegisterResponseDto.builder().keywordId(savedKeyword.getKeywordId()).build();
    }

    public List<KeywordResponseDto> getKeywords(Long userId) {
        User user = getUserWithCampus(userId);
        List<Keyword> savedKeywords = keywordRepository.findKeywordByUser_UserId(userId);
        return keywordsTokeywordResponseDtoList(savedKeywords);
    }

    public void deleteKeyword(Long userId, Long keywordId) {
        User user = getUserWithCampus(userId);
        Keyword keywordToDelete = keywordRepository.findById(keywordId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_KEYWORD_ID));

        validateUserAndKeywordUser(user, keywordToDelete);

        keywordRepository.delete(keywordToDelete);
    }

    private void validateUserAndKeywordUser(User user, Keyword keywordToDelete) {
        if(isMatchedByUserAndKeywordUser(user, keywordToDelete)) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_AND_KEYWORD_USER);
        }
    }

    private boolean isMatchedByUserAndKeywordUser(User user, Keyword keywordToDelete) {
        return !Objects.equals(user.getUserId(), keywordToDelete.getUser().getUserId());
    }

    private List<KeywordResponseDto> keywordsTokeywordResponseDtoList(List<Keyword> savedKeywords) {
        return savedKeywords.stream()
            .map(keyword -> KeywordResponseDto.builder()
                .keywordId(keyword.getKeywordId())
                .keywordName(keyword.getKeywordName())
                .build())
            .toList();
    }

    private Keyword createNewKeyword(String keywordName, User user) {
        return Keyword.builder()
            .keywordName(keywordName)
            .user(user)
            .build();
    }

    private void validateDuplicateKeywordName(List<Keyword> savedKeyword, String keywordName) {

        if (isDuplicatedKeywordName(savedKeyword, keywordName)) {
            throw new CustomException(ErrorCode.DUPLICATE_KEYWORD_NAME);
        }
    }

    private boolean isDuplicatedKeywordName(List<Keyword> savedKeyword, String keywordName) {
        return savedKeyword.stream()
            .anyMatch(keyword -> keyword.getKeywordName().equals(keywordName));
    }

    private void validateSavedKeywordCount(List<Keyword> savedKeyword) {
        if (savedKeyword.size() >= 20) {
            throw new CustomException(ErrorCode.INVALID_KEYWORD_COUNT);
        }
    }

    private User getUserWithCampus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getCampus() == null) {
            throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
        }
        return user;
    }

}
