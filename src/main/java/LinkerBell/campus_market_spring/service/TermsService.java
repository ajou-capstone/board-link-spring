package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserAndTerms;
import LinkerBell.campus_market_spring.dto.TermsRequestDto;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.TermsRepository;
import LinkerBell.campus_market_spring.repository.UserAndTermsRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import io.jsonwebtoken.lang.Collections;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {

    private final TermsRepository termsRepository;
    private final UserAndTermsRepository userAndTermsRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<TermsResponseDto> getTermsAndUserAgreementInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));
        List<Terms> terms = termsRepository.findAll();
        if (CollectionUtils.isEmpty(terms)) {
            log.error("empty terms array");
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        List<UserAndTerms> userAndTerms = userAndTermsRepository.findAllByUserId(userId);

        if (CollectionUtils.isEmpty(userAndTerms)) {
            userAndTerms.addAll(createUserAndNotAgreementTerms(terms, user));
            userAndTermsRepository.saveAll(userAndTerms);
            return createTermsResponseDto(userAndTerms);
        }

        List<UserAndTerms> updatedUserAndTerms = checkUpdatedTerms(terms, userAndTerms, user);
        userAndTermsRepository.saveAll(updatedUserAndTerms);

        return createTermsResponseDto(updatedUserAndTerms);
    }

    @Transactional
    public void agreeTerms(Long userId, List<TermsRequestDto> terms) {
        List<UserAndTerms> userAndTerms = userAndTermsRepository.findAllByUserId(userId);

        if (Collections.isEmpty(userAndTerms)) {
            throw new CustomException(ErrorCode.USER_TERMS_INFO_NOT_FOUND);
        }

        for (TermsRequestDto term : terms) {
            for (UserAndTerms ut : userAndTerms) {
                log.info("[{}], [{}]", term.getTermId(), ut.getTerms().getTermsId());
                if (Objects.equals(term.getTermId(), ut.getTerms().getTermsId())) {
                    ut.setAgree(term.isAgree());
                }
            }
        }

        userAndTermsRepository.saveAll(userAndTerms);
    }

    private List<UserAndTerms> checkUpdatedTerms(List<Terms> terms, List<UserAndTerms> userAndTerms, User user) {
        LocalDateTime recentAgreementDate = userAndTerms.stream()
            .max(Comparator.comparing(UserAndTerms::getLastModifiedDate))
            .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR)).getLastModifiedDate();

        for (Terms term : terms) {
            if (term.getCreatedDate().isAfter(recentAgreementDate)) {
                userAndTerms.add(UserAndTerms.builder().user(user).terms(term).isAgree(false).build());
            }
            else if (term.getLastModifiedDate().isAfter(recentAgreementDate)) {
                userAndTerms.forEach(ut -> {
                    if (StringUtils.equals(ut.getTerms().getTermsUrl(), term.getTermsUrl())) {
                        ut.setAgree(false);
                    }
                });
            }
        }
        return userAndTerms;
    }

    private List<UserAndTerms> createUserAndNotAgreementTerms(List<Terms> terms, User user) {
        return terms.stream().map(term -> {
            return UserAndTerms.builder().user(user).terms(term).isAgree(false).build();
        }).toList();
    }

    private List<TermsResponseDto> createTermsResponseDto(List<UserAndTerms> userAndTerms) {
        return userAndTerms.stream()
            .map(ut -> {
                return TermsResponseDto.builder().id(ut.getTerms().getTermsId())
                    .url(ut.getTerms().getTermsUrl()).title(ut.getTerms().getTitle())
                    .isRequired(ut.getTerms().isRequired())
                    .isAgree(ut.isAgree()).build();
            }).toList();
    }
}
