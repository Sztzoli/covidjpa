package covid;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static covid.InputData.getControlDataFromUser;
import static covid.InputData.getDataFromUser;

public class CitizenController {

    private Scanner scanner = new Scanner(System.in);

    private CitizenService service;

    public CitizenController(CitizenService service) {
        this.service = service;
    }

    public void loadMenu() {
        initMenu();
        menu();
    }

    public void initMenu() {
        String menu =
                """
                        1. Regisztráció
                        2. Tömeges regisztráció
                        3. Generálás
                        4. Oltás
                        5. Oltás meghiúsulás
                        6. Riport""";
        System.out.println(menu);

    }

    public void menu() {

        int menuPoint = getDataFromUser("menü pont",()->Integer.parseInt(scanner.nextLine()));
        switch (menuPoint) {
            case 1 -> registration();
            case 2 -> massRegistration();
            case 3 -> generate();
            case 4 -> vaccination();
            case 5 -> failVaccination();
            case 6 -> report();
        }
        loadMenu();
    }


    public void registration() {
        DataValidator validator = new DataValidator();
        String name = getControlDataFromUser("név:", "hibás név", this::getStringFromScanner, validator::validateName);
        String zip = getControlDataFromUser("zip:", "hibás zip", this::getStringFromScanner, validator::validateZip);
        List<String> cities = service.listCitiesByZip(zip);
        System.out.println(cities);
        int age = getControlDataFromUser("kor:", "hibás kor", this::getIntFromScanner, validator::validateAge);
        String email = getControlDataFromUser("email:", "hibás email", this::getStringFromScanner, validator::validateEmail);
        getControlDataFromUser("email újra:", "hibás email", this::getStringFromScanner, validator::validateSecondEmail);
        String taj = getControlDataFromUser("taj:", "hibás taj", this::getStringFromScanner, validator::validateTaj);

        Citizen citizen = new Citizen(name, zip, age, email, taj);

        service.saveToDatabase(citizen);
    }

    public void massRegistration() {
        String fileName = getDataFromUser("adja meg a fájl nevét", this::getStringFromScanner);
        Path path = Path.of("src/main/resources/covid/" + fileName);
        try (Stream<String> s = Files.lines(path)) {
            s.map(Citizen::readFromFile)
                    .forEach(service::saveToDatabase);
        } catch (IOException ioe) {
            throw new IllegalStateException("File cannot read", ioe);
        }
    }

    public void generate() {
        String zip = getDataFromUser("adja meg az irányítószámót", this::getStringFromScanner);
        String fileName = getDataFromUser("adja meg a fájl nevét", this::getStringFromScanner);
        Path path = Path.of("src/main/resources/covid/" + fileName);
        List<Citizen> citizens = service.listCitizensByZipForVaccination(zip);
        writeToFile(path, citizens);
    }

    public void vaccination() {
        String taj = getDataFromUser("adja meg a tajszámot", this::getStringFromScanner);
        Citizen citizen = service.findCitizenByTaj(taj);
        switch (citizen.getNumberOfVaccinations()) {
            case 0 -> getFirstVaccination(citizen);
            case 1 -> getSecondVaccination(citizen);
            case 2 -> alreadyDoneVaccinations(citizen);
            default -> {
                throw new IllegalStateException();
            }
        }
    }

    public void failVaccination() {
        String taj = getDataFromUser("adja meg a tajszámot", this::getStringFromScanner);
        Citizen citizen = service.findCitizenByTaj(taj);
        LocalDateTime time = getLocalDateTime();
        String note = getDataFromUser("adja meg az okot", this::getStringFromScanner);
        Vaccination vaccination = Vaccination.vaccinationFail(time, note);
        service.updateCitizenWithVaccination(citizen.getId(), vaccination);
    }

    public void report() {
        List<Report> reports = service.listReportsByZip();
        Path path = Path.of("src/main/resources/covid/report.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.write("Irányítószám,0,1,2");
            for (Report report : reports) {
                String line = String.format("%s,%d,%d,%d", report.getZip(), report.getZeroVaccination(), report.getOneVaccination(), report.getTwoVaccination());
                writer.write(line + "\n");
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Files cannot write", ioe);
        }
    }


    private String getStringFromScanner() {
        return scanner.nextLine();
    }

    private Integer getIntFromScanner() {
        return Integer.parseInt(scanner.nextLine());
    }

    private void writeToFile(Path path, List<Citizen> citizens) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.write("Időpont;Név;Irányítószám;Életkor;E-mail cím;TAJ szám");
            LocalTime time = LocalTime.of(8, 0);
            for (Citizen citizen : citizens) {
                String line = String.format("%s, %s, %s, %d, %s, %s", time, citizen.getName(), citizen.getZip(), citizen.getAge(), citizen.getEmail(), citizen.getTaj());
                writer.write(line + "\n");
                time = time.plusMinutes(30);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("File cannot write", ioe);
        }
    }

    private void alreadyDoneVaccinations(Citizen citizen) {
        Citizen citizenDb = service.findCitizenByIdWithVaccination(citizen.getId());
        List<Vaccination> vaccinations = citizenDb.getVaccinations().stream()
                .filter(x -> x.getStatus() == VaccinationStatus.SUCCESS)
                .collect(Collectors.toList());
        System.out.println("Első oltás");
        System.out.println(vaccinations.get(0));
        System.out.println("második oltás");
        System.out.println(vaccinations.get(1));
    }

    private void getSecondVaccination(Citizen citizen) {
        Citizen citizenDb = service.findCitizenByIdWithVaccination(citizen.getId());
        Vaccination vaccinationFirst = citizenDb.getVaccinations().stream()
                .filter(x -> x.getStatus() == VaccinationStatus.SUCCESS)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        System.out.println("Első oltás");
        System.out.println(vaccinationFirst);
        LocalDateTime time = getLocalDateTime();
        Vaccination vaccination = Vaccination.vaccinationSuccess(time, vaccinationFirst.getType());
        service.updateCitizenWithVaccination(citizenDb.getId(), vaccination);
    }

    private void getFirstVaccination(Citizen citizen) {
        LocalDateTime time = getLocalDateTime();
        VaccinationType type = getVaccinationType();
        Vaccination vaccination = Vaccination.vaccinationSuccess(time, type);
        service.updateCitizenWithVaccination(citizen.getId(), vaccination);
    }

    private VaccinationType getVaccinationType() {
        for (int i = 0; i < VaccinationType.values().length; i++) {
            System.out.println((i + 1) + ". " + VaccinationType.values()[i].toString());
        }
        int vaccType = getDataFromUser("válasza ki a oltást", this::getIntFromScanner) - 1;
        VaccinationType type = VaccinationType.values()[vaccType];
        return type;
    }

    private LocalDateTime getLocalDateTime() {
        String date = getDataFromUser("dátum (yyyy.MM.dd HH:mm)", this::getStringFromScanner);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(date, formatter);
        return time;
    }
}
