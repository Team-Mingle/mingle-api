package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PolicyType {

    PRIVACY_POLICY("privacy"),
    TERMS_AND_CONDITIONS("terms");

    private final String dbPolicyName;
}
