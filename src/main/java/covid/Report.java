package covid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Report {
    private String zip;
    private Long zeroVaccination;
    private Long oneVaccination;
    private Long twoVaccination;

    public static Report reportFromObject(Object[] report) {
        return new Report((String) report[0], (Long) report[1], (Long) report[2], (Long) report[3]);
    }


}
