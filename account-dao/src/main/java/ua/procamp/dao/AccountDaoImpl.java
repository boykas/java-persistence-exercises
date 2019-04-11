package ua.procamp.dao;

import ua.procamp.exception.AccountDaoException;
import ua.procamp.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        consumeQuery(entityManager -> entityManager.persist(account));
    }

    @Override
    public Account findById(Long id) {
        return applyQuery(entityManager -> entityManager.find(Account.class, id));
    }

    @Override
    public Account findByEmail(String email) {
        return applyQuery(entityManager -> entityManager.createQuery("select a from Account a where a.email = :email", Account.class)
                .setParameter("email", email)
                .getSingleResult());
    }

    @Override
    public List<Account> findAll() {
        return applyQuery(entityManager -> entityManager.createQuery("select a from Account a", Account.class)
                .getResultList());
    }

    @Override
    public void update(Account account) {
        consumeQuery(entityManager -> entityManager.merge(account));
    }

    @Override
    public void remove(Account account) {
        consumeQuery(entityManager -> {
            Account mergeAccount = entityManager.merge(account);
            entityManager.remove(mergeAccount);
        });
    }

    private void consumeQuery(Consumer<EntityManager> entityManagerConsumer) {
        applyQuery(entityManager -> {
            entityManagerConsumer.accept(entityManager);
            return null;
        });
    }

    private <T> T applyQuery(Function<EntityManager, T> entityManagerFunction) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            T result = entityManagerFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Error performing dao operation. Transaction is rolled back!", ex);
        } finally {
            entityManager.close();
        }
    }

}

