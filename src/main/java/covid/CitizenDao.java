package covid;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class CitizenDao {

    private final EntityManagerFactory factory;

    public CitizenDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveCitizen(Citizen citizen) {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(citizen);
        em.getTransaction().commit();
        em.close();
    }

    public void saveCitizens(List<Citizen> citizens) {
        citizens.forEach(this::saveCitizen);
    }

    public void updateCitizenWithVaccination(Long id, Vaccination vaccination) {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        Citizen citizen = em.getReference(Citizen.class, id);
        vaccination.setCitizen(citizen);
        if (vaccination.getStatus() == VaccinationStatus.SUCCESS) {
            citizen.setLastVaccination(vaccination.getVaccinationDate());
            citizen.setNumberOfVaccinations(citizen.getNumberOfVaccinations() + 1);
        }
        em.persist(vaccination);
        em.getTransaction().commit();
        em.close();
    }

    public Citizen findById(Long id) {
        EntityManager em = factory.createEntityManager();
        Citizen citizen = em.find(Citizen.class, id);
        em.close();
        return citizen;
    }

    public Citizen findByTaj(String taj) {
        EntityManager em = factory.createEntityManager();
        Citizen citizen = em.createQuery("select c from Citizen c where c.taj = :taj", Citizen.class)
                .setParameter("taj", taj)
                .getSingleResult();
        em.close();
        return citizen;
    }

    public Citizen findByIdWithVaccination(Long id) {
        EntityManager em = factory.createEntityManager();
        Citizen citizen = em.createQuery("select c from Citizen c join fetch c.vaccinations where c.id = :id", Citizen.class)
                .setParameter("id", id)
                .getSingleResult();
        em.close();
        return citizen;
    }

    public Citizen findByTajWithVaccination(String taj) {
        EntityManager em = factory.createEntityManager();
        Citizen citizen = em.createQuery("select c from Citizen c join fetch c.vaccinations where c.taj = :taj", Citizen.class)
                .setParameter("taj", taj)
                .getSingleResult();
        em.close();
        return citizen;
    }

    public List<Citizen> findCitizenByZipForVaccination(String zip) {
        LocalDateTime now = LocalDateTime.now();
        EntityManager em = factory.createEntityManager();
        List<Citizen> citizens = em
                .createQuery("select c from Citizen c where c.zip = :zip" +
                                " and (c.numberOfVaccinations < :numberOfVaccination or c.numberOfVaccinations is null) " +
                                "and (c.lastVaccination is null or c.lastVaccination< :lastVaccination)" +
                                " order by c.age desc, c.name"
                        , Citizen.class)
                .setParameter("zip", zip)
                .setParameter("numberOfVaccination", 2)
                .setParameter("lastVaccination", now.minusDays(15))
                .setMaxResults(16)
                .getResultList();
        em.close();
        return citizens;
    }

    public List<Report> listByZipToReport() {
        EntityManager em = factory.createEntityManager();
        List<Object[]> reports = em.createQuery("""
                SELECT c.zip,
                COUNT(case when c.numberOfVaccinations=0 then 1 end) AS zero,
                COUNT(case when c.numberOfVaccinations=1 then 1 end) AS one,
                COUNT(case when c.numberOfVaccinations=2 then 1 end) AS two
                FROM
                Citizen c
                GROUP BY c.zip
                order by zip""", Object[].class)
                .getResultList();
        em.close();
        return reports.stream().map(Report::reportFromObject).collect(Collectors.toList());
    }

}
