package covid;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;

public class CityDao {

    private EntityManagerFactory factory;

    public CityDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveCity(City city) {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(city);
        em.getTransaction().commit();
        em.close();
    }

    public City findById(Long id) {
        EntityManager em = factory.createEntityManager();
        City city = em.find(City.class, id);
        em.close();
        return city;
    }

    public List<String> findCitiesNameByZip(String zip) {
        EntityManager em = factory.createEntityManager();
        List<String> cities = em.createQuery("select c from City c where c.zip = :zip",City.class)
                .setParameter("zip", zip)
                .getResultStream().map(City::getName).collect(Collectors.toList());
        em.close();
        return cities;
    }


}
