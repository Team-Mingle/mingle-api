package community.mingle.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum CourseColourRgb {

    FBE9EF(251, 233, 239),
    DCF5DA(220, 245, 218),
    FFEBCD(255, 235, 205),
    E1F6F6(225, 246, 246),
    EFDDEF(239, 221, 239),
    D4E2F3(212, 226, 243),
    EBD6CD(235, 214, 203),
    BDD4C7(189, 212, 199),
    CECEE9(206, 206, 233),
    E7EFCE(231, 239, 206);

    private final int red;
    private final int green;
    private final int blue;

    public String getStringRgb() {
        return this.red + ", " + this.green + ", " + this.blue;
    }

    public static CourseColourRgb getByRgb(String rgb) {
        int[] values = Arrays.stream(rgb.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();

        for (CourseColourRgb color : values()) {
            if (color.red == values[0] && color.green == values[1] && color.blue == values[2]) {
                return color;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for RGB values: " + values[0] + ", " + values[1] + ", " + values[2]);
    }
}
