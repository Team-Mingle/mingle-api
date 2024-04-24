package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CurrencyType {

    KRW(List.of("홍콩", "싱가포르", "영국")),
    HKD(List.of("홍콩")),
    SGD(List.of("싱가포르")),
    RMB(List.of("중국")),
    GBP(List.of("영국"));

    private final List<String> countries;
}
