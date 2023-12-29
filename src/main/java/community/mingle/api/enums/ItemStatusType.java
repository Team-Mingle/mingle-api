package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@RequiredArgsConstructor
public enum ItemStatusType {
    SELLING("판매중"),
    RESERVED("예약중"),
    SOLDOUT("판매완료"),
    INACTIVE("삭제됨"),
    NOTIFIED("신고중"),
    REPORTED("신고됨"),
    DELETED("운영진 삭제");

    private final String name;

}
