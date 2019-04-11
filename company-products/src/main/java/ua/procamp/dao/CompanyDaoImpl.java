package ua.procamp.dao;

import org.hibernate.Session;
import ua.procamp.exception.CompanyDaoException;
import ua.procamp.model.Company;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Function;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        return appluQueue(entityManager ->
                entityManager.createQuery("select c from Company c join fetch c.products where c.id = :id", Company.class)
                        .setParameter("id", id)
                        .getSingleResult());
    }

    private <T> T appluQueue(Function<EntityManager, T> entityManagerTFunction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.unwrap(Session.class).setDefaultReadOnly(true);
        entityManager.getTransaction().begin();
        try {
            T result = entityManagerTFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw new CompanyDaoException("Error performing read operation", ex);
        } finally {
            entityManager.close();
        }
    }
}
