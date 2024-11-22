package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.QA;
import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.QaRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
