package LinkerBell.campus_market_spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QaCategory {

    ACCOUNT_INQUIRY("계정 문의"),
    CHAT_AND_NOTIFICATION("채팅, 알림"),
    SECONDHAND_TRANSACTION("중고거래"),
    ADVERTISEMENT_INQUIRY("광고 문의"),
    OTHER("기타");

    private final String description;
}
