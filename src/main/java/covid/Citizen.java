package covid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citizens")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String zip;

    @Column
    private int age;

    @Column(nullable = false)
    private String email;

    @Column(unique = true)
    private String taj;

    @Column(name = "vac_num")
    private int numberOfVaccinations = 0;

    @Column(name = "last_vaccination")
    private LocalDateTime lastVaccination = null;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "citizen")
    @ToString.Exclude
    private List<Vaccination> vaccinations;

    public Citizen(String name, String zip, int age, String email, String taj) {
        this.name = name;
        this.zip = zip;
        this.age = age;
        this.email = email;
        this.taj = taj;
    }

    public static Citizen readFromFile(String line) {
        String[] parts = line.split(",");
        return new Citizen(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], parts[4]);
    }

    public void addVaccination(Vaccination vaccination) {
        if (vaccinations == null) {
            vaccinations = new ArrayList<>();
        }
        vaccinations.add(vaccination);
        vaccination.setCitizen(this);
        if (vaccination.getStatus() == VaccinationStatus.SUCCESS) {
            lastVaccination = vaccination.getVaccinationDate();
            numberOfVaccinations++;
        }
    }


}
