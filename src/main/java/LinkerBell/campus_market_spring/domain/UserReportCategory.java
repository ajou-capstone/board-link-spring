package LinkerBell.campus_market_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserReportCategory {
    OTHER("기타"),
    FRAUD("사기"),
    DISPUTE_DURING_TRANSACTION("거래 중 분쟁"),
    RUDE_USER("비매너 사용자"),
    PROFESSIONAL_SELLER("전문판매업자"),
    DATING_PURPOSE_CHAT("연애 목적의 대화"),
    HATE_SPEECH("욕설, 비방, 혐오표현"),
    INAPPROPRIATE_SEXUAL_BEHAVIOR("부적절한 성적 행위");

    private final String description;
}
