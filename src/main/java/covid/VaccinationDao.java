package covid;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class VaccinationDao {

    private EntityManagerFactory factory;

    public VaccinationDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveVaccination(Vaccination vaccination) {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(vaccination);
        em.getTransaction().commit();
        em.close();
    }

    public Vaccination findById(Long id) {
        EntityManager em = factory.createEntityManager();
        Vaccination vaccination = em.find(Vaccination.class, id);
        em.close();
        return vaccination;
    }
}
