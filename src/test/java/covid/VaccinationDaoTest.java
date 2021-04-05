package covid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VaccinationDaoTest {

    private VaccinationDao vaccinationDao;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        vaccinationDao = new VaccinationDao(factory);
    }

    @Test
    void testSaveVaccination() {
        Vaccination success = Vaccination.vaccinationSuccess(LocalDateTime.of(2021,3,31,10,0) ,VaccinationType.PFIZER_BIOTECH);
        Vaccination fail = Vaccination.vaccinationFail(LocalDateTime.of(2021,2,12,10,0),"beteg");

        vaccinationDao.saveVaccination(success);
        vaccinationDao.saveVaccination(fail);

        Vaccination vaccinationDb = vaccinationDao.findById(success.getId());
        assertEquals(VaccinationType.PFIZER_BIOTECH, vaccinationDb.getType());
    }


}