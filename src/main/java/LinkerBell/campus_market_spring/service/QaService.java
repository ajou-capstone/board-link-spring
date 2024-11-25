package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.QaResponseDto;
import LinkerBell.campus_market_spring.dto.QaSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class QaService {

    private final UserRepository userRepository;
    private final QaRepository qaRepository;

    public void postQuestion(Long userId, String title, String description, QaCategory category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        QA qa = QA.builder()
            .user(user)
            .title(title)
            .description(description)
            .category(category)
            .isCompleted(false).build();
        qaRepository.save(qa);
    }

    @Transactional(readOnly = true)
    public SliceResponse<QaSearchResponseDto> getAnswers(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Slice<QA> slice = qaRepository.findQASByUser(user, pageable);

        return new SliceResponse<>(slice.map(QaSearchResponseDto::new));
    }

    @Transactional(readOnly = true)
    public QaResponseDto getAnswerDetails(Long userId, Long qaId) {
        QA qa = qaRepository.findById(qaId)
            .orElseThrow(() -> new CustomException(ErrorCode.QA_NOT_FOUND));

        if (!Objects.equals(userId, qa.getUser().getUserId())) {
            throw new CustomException(ErrorCode.NOT_MATCH_QA_USER);
        }

        return new QaResponseDto(qa);
    }

}
