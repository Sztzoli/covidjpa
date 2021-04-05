package covid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccinations")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vac_date")
    private LocalDateTime vaccinationDate;

    @Enumerated(EnumType.STRING)
    private VaccinationStatus status;

    private String note;

    @Enumerated(EnumType.STRING)
    private VaccinationType type;

    @ManyToOne
    @ToString.Exclude
    private Citizen citizen;

    public Vaccination(LocalDateTime vaccinationDate, VaccinationStatus status, String note, VaccinationType type) {
        this.vaccinationDate = vaccinationDate;
        this.status = status;
        this.note = note;
        this.type = type;
    }

    public static Vaccination vaccinationSuccess(LocalDateTime vaccinationDate, VaccinationType type) {
        return new Vaccination(vaccinationDate, VaccinationStatus.SUCCESS, null, type);
    }

    public static Vaccination vaccinationFail(LocalDateTime vaccinationDate, String note) {
        return new Vaccination(vaccinationDate, VaccinationStatus.FAIL, note, null);
    }

}
