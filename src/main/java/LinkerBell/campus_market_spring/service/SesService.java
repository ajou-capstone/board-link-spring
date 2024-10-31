package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Campus;
import LinkerBell.campus_market_spring.dto.MailResponseDto;
import LinkerBell.campus_market_spring.dto.SenderEmailDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.CampusRepository;
import com.google.api.services.storage.model.StorageObject.CustomerEncryption;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SesService {
    private final SesV2AsyncClient sesV2AsyncClient;
    private final SpringTemplateEngine templateEngine;
    private final CampusRepository campusRepository;

    private final LoadingCache<String, EmailAndCode> stores = CacheBuilder.newBuilder().maximumSize(100)
        .expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, EmailAndCode>() {
            @Override
            public EmailAndCode load(String key) throws Exception {
                return stores.get(key);
            }
        });

    public MailResponseDto processEmail(String email) {
        if (!validateEmail(email)) {
            throw new CustomException(ErrorCode.INVALID_SCHOOL_EMAIL);
        }
        String code = createCode();
        SendEmailRequest emailRequest = createSendEmailRequest(email, code);
        String messageId = sendEmailAndGetId(emailRequest).join();
        stores.put(messageId, new EmailAndCode(email, code));
        return new MailResponseDto(messageId);
    }

    public String verifyTokenAndCode(String token, String receivedCode) {
        EmailAndCode storedData = stores.getIfPresent(token);
        if (!compareCode(storedData, receivedCode)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return storedData.email();
    }

    private boolean validateEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1);

        List<Campus> campuses = campusRepository.findByEmail(domain);
        return !campuses.isEmpty();
    }

    private boolean compareCode(EmailAndCode storedData, String receivedCode) {
        return storedData != null && storedData.code().equals(receivedCode);
    }

    private CompletableFuture<String> sendEmailAndGetId(SendEmailRequest emailRequest) {
        return sesV2AsyncClient.sendEmail(emailRequest).handle(((sendEmailResponse, throwable) -> {
            if (sendEmailResponse != null) {
                return sendEmailResponse.messageId();
            } else {
                throw new CustomException(ErrorCode.INTERNAL_SEVER_ERROR);
            }
        }));
    }

    private String createCode() {
        int length = 6;
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.INTERNAL_SEVER_ERROR);
        }
    }

    private SendEmailRequest createSendEmailRequest(String email, String code) {
        SenderEmailDto senderEmailDto = SenderEmailDto.builder()
            .from("noreply@linkerbell.co.kr")
            .subject("인증 코드 발송 메일")
            .content(createContext(code))
            .to(email)
            .build();
        return senderEmailDto.toSendRequestDto();
    }

    private String createContext(String code) {
        Context context = new Context();
        context.setVariable("verificationCode", code);
        return templateEngine.process("mailTemplate", context);
    }

    private record EmailAndCode(String email, String code){}
}
