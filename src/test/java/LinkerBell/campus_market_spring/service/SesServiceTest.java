package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.MailResponseDto;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;

@ExtendWith(MockitoExtension.class)
class SesServiceTest {

    @InjectMocks
    SesService sesService;

    @Mock
    SesV2AsyncClient sesV2AsyncClient;

    @Mock
    SpringTemplateEngine springTemplateEngine;

    @Mock
    LoadingCache<String, EmailAndCode> stores;

    @Mock
    CampusRepository campusRepository;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("이메일 전송 성공 테스트")
    public void sendEmailSuccessTest() {
        // given
        String email = "abc@gmail.com";
        String messageId = "1234";
        List<Campus> campuses = new ArrayList<>();
        campuses.add(Campus.builder().campusId(1L).email("gmail.com").universityName("test").build());

        SendEmailResponse response = SendEmailResponse.builder()
            .messageId(messageId).build();
        given(campusRepository.findByEmail(anyString())).willReturn(campuses);
        given(userRepository.findBySchoolEmail(anyString())).willReturn(Optional.empty());
        given(sesV2AsyncClient.sendEmail(any(SendEmailRequest.class)))
            .willReturn(CompletableFuture.completedFuture(response));
        given(springTemplateEngine.process(anyString(), any(IContext.class)))
            .willReturn(anyString());

        // when
        MailResponseDto mailResponseDto = sesService.processEmail(email);
        // then
        assertThat(mailResponseDto.getToken()).isEqualTo(messageId);
    }

    @Test
    @DisplayName("이메일 전송 실패 테스트")
    public void sendEmailFailureTest() {
        // given
        String email = "abc@gmail.com";

        CompletableFuture<SendEmailResponse> futureException = new CompletableFuture<>();
        futureException.completeExceptionally(new RuntimeException("Email sending failed",
            SdkClientException.create("Email sending failed")));
        given(campusRepository.findByEmail(anyString())).willReturn(Lists.newArrayList(
            Campus.builder().build()));
        given(userRepository.findBySchoolEmail(anyString())).willReturn(Optional.empty());
        given(sesV2AsyncClient.sendEmail(any(SendEmailRequest.class))).willReturn(futureException);

        // when & then
        assertThatThrownBy(() -> sesService.processEmail(email))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("서버 내부 오류입니다.");
    }

    @Test
    @DisplayName("허용하지 않는 이메일 형식 에러 테스트")
    public void emailNotValidatedTest() {
        // given
        String email1 = "";
        String email2 = null;
        String email3 = "test!Example.com";
        String email4 = "test@not-exist.com";

        given(campusRepository.findByEmail(anyString())).willReturn(Lists.newArrayList());

        // when & then
        assertThatThrownBy(() -> sesService.processEmail(email1)).isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> sesService.processEmail(email2)).isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> sesService.processEmail(email3)).isInstanceOf(CustomException.class);

        assertThatThrownBy(() -> sesService.processEmail(email4)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("인증 코드 불일치 에러 테스트")
    public void verifyCodeFailureTest() {
        // given
        sendEmailSuccessTest();
        String validCode = "1234";
        String token = "1234";

        // when & then
        assertThatThrownBy(() -> sesService.verifyTokenAndCode(token, validCode))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("인증 코드가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("학교 이메일 중복 에러 테스트")
    public void duplicateSchoolEmailTest() {
        // given
        Campus campus = Campus.builder().email("school.com").universityName("test").region("수원")
            .campusId(1L).build();
        User user = User.builder().userId(1L).campus(campus).build();
        given(campusRepository.findByEmail(anyString())).willReturn(Lists.newArrayList(campus));
        given(userRepository.findBySchoolEmail(anyString())).willReturn(Optional.ofNullable(user));

        // when & then
        assertThatThrownBy(() -> sesService.processEmail("test@school.com"))
            .isInstanceOf(CustomException.class).hasMessageContaining("중복된 학교 이메일입니다.");
    }

    private record EmailAndCode(String email, String code) {}
}