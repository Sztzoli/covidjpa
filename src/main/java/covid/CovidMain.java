package covid;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CovidMain {

    private final CitizenController controller;

    public CovidMain(CitizenController controller) {
        this.controller = controller;
    }


    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pu");
        CovidMain covidMain = new CovidMain(new CitizenController(new CitizenService(new CitizenDao(factory), new CityDao(factory))));
        covidMain.controller.loadMenu();
    }
}
