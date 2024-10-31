package LinkerBell.campus_market_spring.dto;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Getter
@Builder
public class SenderEmailDto {

    private String from;
    private String to;
    private String subject;
    private String content;

    public SendEmailRequest toSendRequestDto() {
        return SendEmailRequest.builder()
            .fromEmailAddress(from)
            .destination(createDestination())
            .content(EmailContent.builder()
                .simple(createMessage())
                .build()).build();
    }

    private Destination createDestination() {
        return Destination.builder()
            .toAddresses(to).build();
    }

    private Message createMessage() {
        return Message.builder()
            .subject(createContent(subject))
            .body(createBody()).build();
    }

    private Content createContent(String text) {
        return Content.builder()
            .charset("UTF-8")
            .data(text).build();
    }

    private Body createBody() {
        return Body.builder()
            .html(createContent(content))
            .build();
    }
}
