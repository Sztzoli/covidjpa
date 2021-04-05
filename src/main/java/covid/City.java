package covid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String zip;

    private String name;

    public City(String zip, String name) {
        this.zip = zip;
        this.name = name;
    }

    public static City lineToCityFromFile(String line) {
        String[] parts = line.split(";");
        return new City(parts[0], parts[1]);
    }
}
