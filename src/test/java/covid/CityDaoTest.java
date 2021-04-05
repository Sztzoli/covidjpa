package covid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CityDaoTest {

    private CityDao cityDao;
    private City city;

    @BeforeEach
    void init() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        cityDao = new CityDao(factory);
        city = new City("6000","Kecskemét");
    }

    @Test
    void saveAndFindById() {
        cityDao.saveCity(city);
        City cityDb = cityDao.findById(city.getId());
        assertEquals("6000", cityDb.getZip());
    }

    @Test
    void saveAndFindByZip() {
        cityDao.saveCity(city);
        List<String> cities = cityDao.findCitiesNameByZip(city.getZip());
        assertTrue(cities.contains("Kecskemét"));
    }

}