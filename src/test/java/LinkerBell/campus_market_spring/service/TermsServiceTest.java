package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.domain.UserAndTerms;
import LinkerBell.campus_market_spring.dto.TermsRequestDto;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.TermsRepository;
import LinkerBell.campus_market_spring.repository.UserAndTermsRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @Mock
    TermsRepository termsRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserAndTermsRepository userAndTermsRepository;

    @InjectMocks
    TermsService termsService;

    User user;

    Terms terms1;

    Terms terms2;

    UserAndTerms userAndTerms1;
    UserAndTerms userAndTerms2;


    @BeforeEach
    public void setUp() {
        user = createUser();

        List<Terms> terms = createTerms();
        terms1 = terms.get(0);
        terms2 = terms.get(1);

        userAndTerms1 = UserAndTerms.builder().user(user).terms(terms1).isAgree(true).build();
        userAndTerms2 = UserAndTerms.builder().user(user).terms(terms2).isAgree(true).build();

        userAndTerms1.setCreatedDate(LocalDateTime.now());
        userAndTerms1.setLastModifiedDate(LocalDateTime.now());

        userAndTerms2.setCreatedDate(LocalDateTime.now());
        userAndTerms2.setLastModifiedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자 약관 정보 가져오기 테스트")
    public void getTermsAndUserAgreementInfoTest() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(termsRepository.findAll()).willReturn(Lists.newArrayList(terms1, terms2));
        given(userAndTermsRepository.findAllByUserId(anyLong()))
            .willReturn(Lists.newArrayList(userAndTerms1, userAndTerms2));
        // when
        List<TermsResponseDto> termsResponseDtoList =
            termsService.getTermsAndUserAgreementInfo(user.getUserId());

        // then
        assertThat(termsResponseDtoList.size()).isEqualTo(2);
        assertThat(termsResponseDtoList.get(0).isAgree()).isTrue();
        assertThat(termsResponseDtoList.get(1).isAgree()).isTrue();
    }

    @Test
    @DisplayName("새로운 사용자에 대한 사용자 약관 가져오기 테스트")
    public void getTermsAndUserInfoForNewUserTest() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(userAndTermsRepository.findAllByUserId(anyLong()))
            .willReturn(Lists.newArrayList());
        given(termsRepository.findAll()).willReturn(Lists.newArrayList(terms1, terms2));

        // when
        List<TermsResponseDto> termsResponseDtoList =
            termsService.getTermsAndUserAgreementInfo(user.getUserId());

        // then
        assertThat(termsResponseDtoList.size()).isEqualTo(2);
        assertThat(termsResponseDtoList.get(0).isAgree()).isFalse();
        assertThat(termsResponseDtoList.get(1).isAgree()).isFalse();
    }

    @Test
    @DisplayName("추가되거나 수정된 사용자 약관 정보 가져오기 테스트")
    public void getUpdatedTermsAndUserInfoTest() {
        // given
        Terms terms3 = Terms.builder().termsUrl("test3_url").title("test3").isRequired(false)
            .build();
        terms3.setCreatedDate(LocalDateTime.MAX);
        terms3.setLastModifiedDate(LocalDateTime.MAX);

        terms2.setLastModifiedDate(LocalDateTime.MAX);

        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(userAndTermsRepository.findAllByUserId(anyLong())).willReturn(
            Lists.newArrayList(userAndTerms1, userAndTerms2));
        given(termsRepository.findAll()).willReturn(Lists.newArrayList(terms1, terms2, terms3));
        // when
        List<TermsResponseDto> termsResponseDtoList =
            termsService.getTermsAndUserAgreementInfo(user.getUserId());

        // then
        assertThat(termsResponseDtoList.size()).isEqualTo(3);
        assertThat(termsResponseDtoList.get(0).isAgree()).isTrue();
        assertThat(termsResponseDtoList.get(1).isAgree()).isFalse();
        assertThat(termsResponseDtoList.get(2).isAgree()).isFalse();
    }

    @Test
    @DisplayName("빈 약관 정보 에러 테스트")
    public void emptyTermsErrorTest() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.ofNullable(user));
        given(termsRepository.findAll()).willReturn(Lists.newArrayList());
        // when & then
        assertThatThrownBy(() -> termsService.getTermsAndUserAgreementInfo(anyLong()))
            .isInstanceOf(CustomException.class).hasMessageContaining("서버 내부 오류입니다.");
    }

    @Test
    @DisplayName("빈 사용자 약관 정보 에러 테스트")
    public void emptyUserAndTermsErrorTest() {
        // given
        given(userAndTermsRepository.findAllByUserId(anyLong())).willReturn(Lists.newArrayList());
        // when & then
        assertThatThrownBy(() -> termsService.agreeTerms(1L, Lists.newArrayList()))
            .isInstanceOf(CustomException.class).hasMessageContaining("사용자 약관 동의 정보가 없습니다.");
    }

    private User createUser() {
        return User.builder()
            .userId(1L)
            .loginEmail("test@example.com")
            .build();
    }

    private List<Terms> createTerms() {
        Terms term1 = Terms.builder().title("test1").termsUrl("test1_url")
            .isRequired(true).build();
        Terms term2 = Terms.builder().title("test2").termsUrl("test2_url")
            .isRequired(true).build();
        term1.setCreatedDate(LocalDateTime.now());
        term1.setLastModifiedDate(LocalDateTime.now());
        term2.setCreatedDate(LocalDateTime.now());
        term2.setLastModifiedDate(LocalDateTime.now());
        return Lists.newArrayList(term1, term2);
    }
}
