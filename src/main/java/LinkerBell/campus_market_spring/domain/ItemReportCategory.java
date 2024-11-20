package LinkerBell.campus_market_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemReportCategory {
    PROHIBITED_ITEM("거래 금지 물품"),
    NOT_SECONDHAND_POST("중고거래 게시글이 아님"),
    COMMERCIAL_SELLER("전문판매업자"),
    DISPUTE_DURING_TRANSACTION("거래 중 분쟁"),
    FRAUD("사기"),
    OTHER("기타");

    private final String description;
}
