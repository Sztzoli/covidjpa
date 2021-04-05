package covid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;


public class CitizenService {


    private final CitizenDao citizenDao;
    private final CityDao cityDao;

    public CitizenService(CitizenDao citizenDao, CityDao cityDao) {
        this.citizenDao = citizenDao;
        this.cityDao = cityDao;
        uploadCities();
    }

    public boolean saveToDatabase(Citizen citizen) {
        if (citizenDao.findByTaj(citizen.getTaj())!=null){
            return false;
        }
        citizenDao.saveCitizen(citizen);
        return true;
    }

    public void saveCityToDataBase(City x) {
        cityDao.saveCity(x);
    }

    public void updateCitizenWithVaccination(Long id, Vaccination vaccination) {
        citizenDao.updateCitizenWithVaccination(id, vaccination);
    }

    public List<String> listCitiesByZip(String zip) {
        return cityDao.findCitiesNameByZip(zip);
    }

    public List<Citizen> listCitizensByZipForVaccination(String zip) {
        return citizenDao.findCitizenByZipForVaccination(zip);
    }

    public List<Report> listReportsByZip() {
        return citizenDao.listByZipToReport();
    }

    public Citizen findCitizenByTaj(String taj) {
        return citizenDao.findByTaj(taj);
    }

    public Citizen findCitizenByIdWithVaccination(Long id) {
        return citizenDao.findByIdWithVaccination(id);
    }


    public void uploadCities() {
        Path path = Path.of("src/main/resources/covid/iranyitoszamok-varosok-2021.csv");
        try (Stream<String> s = Files.lines(path)) {
            s
                    .skip(1)
                    .map(City::lineToCityFromFile)
                    .forEach(this::saveCityToDataBase);
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot read file", ioe);
        }
    }

}
