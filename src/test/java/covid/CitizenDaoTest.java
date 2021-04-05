package covid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CitizenDaoTest {

    private CitizenDao citizenDao;
    private List<Citizen> citizens;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        citizenDao = new CitizenDao(factory);

        Citizen citizen1 = new Citizen("John Doe", "6000", 70, "jd@gmail.com", "111111110");
        Citizen citizen2 = new Citizen("John Doe", "6000", 69, "jd@gmail.com", "111111220");
        Citizen citizen3 = new Citizen("AJohn Doe", "6000", 69, "jd@gmail.com", "111111330");
        Citizen citizen4 = new Citizen("John Doe", "6033", 30, "jd@gmail.com", "111111440");
        Citizen citizen5 = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111550");
        Citizen citizen6 = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111660");
        Citizen citizen7 = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111770");
        Citizen citizen8 = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111880");
        Citizen citizen9 = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111990");

        citizens = List.of(citizen1, citizen2, citizen3, citizen4, citizen5, citizen6, citizen7, citizen8, citizen9);
    }

    @Test
    void testSaveCitizen() {
        Citizen citizen = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110");
        citizenDao.saveCitizen(citizen);

        Citizen citizenDb = citizenDao.findById(citizen.getId());
        assertEquals("John Doe", citizenDb.getName());
    }

    @Test
    void testSaveCitizenWithVaccination() throws InterruptedException {
        Citizen citizen = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110");
        Vaccination success = Vaccination.vaccinationSuccess(LocalDateTime.of(2021, 3, 31, 10, 0), VaccinationType.PFIZER_BIOTECH);
        Vaccination fail = Vaccination.vaccinationFail(LocalDateTime.of(2021, 2, 12, 10, 0), "beteg");
        citizen.addVaccination(success);
        TimeUnit.SECONDS.sleep(1);
        citizen.addVaccination(fail);
        citizenDao.saveCitizen(citizen);
        assertEquals(2, citizenDao.findByIdWithVaccination(citizen.getId()).getVaccinations().size());
    }

    @Test
    void testUpdateCitizenWithVaccination() {
        Citizen citizen = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110");
        citizenDao.saveCitizen(citizen);
        Vaccination success = Vaccination.vaccinationSuccess(LocalDateTime.of(2021, 3, 31, 10, 0), VaccinationType.PFIZER_BIOTECH);
        citizenDao.updateCitizenWithVaccination(citizen.getId(), success);

        Citizen citizenDb = citizenDao.findByIdWithVaccination(citizen.getId());
        assertEquals(VaccinationType.PFIZER_BIOTECH ,citizenDb.getVaccinations().get(0).getType());
    }

    @Test
    void testSelectByTaj() {
        Citizen citizen = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110");
        citizenDao.saveCitizen(citizen);

        Citizen citizenDb = citizenDao.findByTaj(citizen.getTaj());
        assertEquals("John Doe", citizenDb.getName());
    }

    @Test
    @DisplayName("test for order")
    void testListByCitizenByZipForVaccination() {
        citizenDao.saveCitizens(citizens);
        List<Citizen> citizensDb = citizenDao.findCitizenByZipForVaccination("6000");
        assertEquals(8, citizensDb.size());
        assertEquals(70, citizensDb.get(0).getAge());
        assertEquals("AJohn Doe", citizensDb.get(1).getName());
    }

    @Test
    @DisplayName("test after get vaccination")
    void testListByCitizenByZipForVaccination2() {
        citizenDao.saveCitizens(citizens);
        Vaccination vaccination1 = Vaccination.vaccinationSuccess(LocalDateTime.now().minusDays(1), VaccinationType.PFIZER_BIOTECH);
        citizenDao.updateCitizenWithVaccination(citizens.get(0).getId(), vaccination1);
        List<Citizen> citizensDb = citizenDao.findCitizenByZipForVaccination("6000");
        assertEquals(7, citizensDb.size());
        assertEquals(69, citizensDb.get(0).getAge());
    }

    @Test
    void testReport() {
        citizenDao.saveCitizens(citizens);
        Vaccination vaccination1 = Vaccination.vaccinationSuccess(LocalDateTime.now().minusDays(1), VaccinationType.PFIZER_BIOTECH);
        citizenDao.updateCitizenWithVaccination(citizens.get(0).getId(), vaccination1);
        List<Report> reports = citizenDao.listByZipToReport();
        assertEquals("6000", reports.get(0).getZip());
    }

}