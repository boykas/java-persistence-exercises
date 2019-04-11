package ua.procamp.dao;

import ua.procamp.model.Photo;
import ua.procamp.model.PhotoComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
     applyQuery(entityManager -> {
         entityManager.persist(photo);
         return null;
     });
    }

    @Override
    public Photo findById(long id) {
       return applyQuery(entityManager -> entityManager.find(Photo.class, id));
    }

    @Override
    public List<Photo> findAll() {
        return applyQuery(entityManager ->
            entityManager.createQuery("select p from Photo p").getResultList()
        );
    }

    @Override
    public void remove(Photo photo) {
        applyQuery(entityManager -> {
            Photo merged = entityManager.merge(photo);
            entityManager.remove(merged);
            return null;
        });
    }

    @Override
    public void addComment(long photoId, String comment) {
        applyQuery(entityManager -> {
            Photo photoReference = entityManager.getReference(Photo.class, photoId);// does not call database
            PhotoComment photoComment = new PhotoComment(comment, photoReference);
            entityManager.persist(photoComment);
            return null;
        });
    }

    private <T> T applyQuery(Function<EntityManager, T> entityManagerTFunction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            T result = entityManagerTFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("");
        } finally {
            entityManager.close();
        }
    }
}
