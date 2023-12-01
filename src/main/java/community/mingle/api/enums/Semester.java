package community.mingle.api.enums;

import community.mingle.api.global.exception.CustomException;
import lombok.Getter;

import static community.mingle.api.global.exception.ErrorCode.SEMESTER_NOT_FOUND;

@Getter
public enum Semester {
    FIRST_SEMESTER_2019(2019, 1),
    SECOND_SEMESTER_2019(2019, 2),
    FIRST_SEMESTER_2020(2020, 1),
    SECOND_SEMESTER_2020(2020, 2),
    FIRST_SEMESTER_2021(2021, 1),
    SECOND_SEMESTER_2021(2021, 2),
    FIRST_SEMESTER_2022(2022, 1),
    SECOND_SEMESTER_2022(2022, 2),
    FIRST_SEMESTER_2023(2023, 1),
    SECOND_SEMESTER_2023(2023, 2),
    FIRST_SEMESTER_2024(2024, 1),
    SECOND_SEMESTER_2024(2024, 2),
    FIRST_SEMESTER_2025(2025, 1),
    SECOND_SEMESTER_2025(2025, 2),
    FIRST_SEMESTER_2026(2026, 1),
    SECOND_SEMESTER_2026(2026, 2),
    FIRST_SEMESTER_2027(2027, 1),
    SECOND_SEMESTER_2027(2027, 2),
    FIRST_SEMESTER_2028(2028, 1),
    SECOND_SEMESTER_2028(2028, 2),
    FIRST_SEMESTER_2029(2029, 1),
    SECOND_SEMESTER_2029(2029, 2),
    FIRST_SEMESTER_2030(2030, 1),
    SECOND_SEMESTER_2030(2030, 2),
    FIRST_SEMESTER_2031(2031, 1),
    SECOND_SEMESTER_2031(2031, 2),
    FIRST_SEMESTER_2032(2032, 1);

    private final int year;
    private final int semester;
    Semester(int year, int semester) {
        this.year = year;
        this.semester = semester;
    }

    public static Semester findSemester(int year, int semester) {
        for (Semester s : Semester.values()) {
            if (s.year == year && s.semester == semester) {
                return s;
            }
        }
        throw new CustomException(SEMESTER_NOT_FOUND);
    }
}
