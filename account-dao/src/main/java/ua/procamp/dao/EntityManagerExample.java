package ua.procamp.dao;

import ua.procamp.model.Account;
import ua.procamp.model.Card;
import ua.procamp.util.EntityManagerUtil;
import ua.procamp.util.TestDataGenerator;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerExample {

    //Entry point for working with hibernate;

    public static void main(String[] args) {
        EntityManagerFactory singleAccountEntityFactory = Persistence.createEntityManagerFactory("SingleAccountEntityH2");

        EntityManagerUtil entityManagerUtil = new EntityManagerUtil(singleAccountEntityFactory);


        Account account = TestDataGenerator.generateAccount();
        entityManagerUtil.performWithinTx(entityManager -> {

            entityManager.persist(account);

            Card card = new Card();
            card.setName("first");
            card.setHolder(account);
            entityManager.persist(card);

            Card card1 = new Card();
            card1.setName("second");
            card1.setHolder(account);
            entityManager.persist(card1);
        });

        entityManagerUtil.performWithinTx(entityManager -> {
            Account foundAccount = entityManager.find(Account.class, account.getId());
            System.out.println("I and here");
            foundAccount.getCards().forEach(card -> System.out.println(card.getName()));
        });

        singleAccountEntityFactory.close();
    }
}


// entityManager.getTransaction().begin();
//
//        try {
//            Account account = TestDataGenerator.generateAccount();
//            System.out.println(account);
//
//
//            entityManager.persist(account);
//            System.out.println(account);
//
//            Account foundAccount = entityManager.find(Account.class, account.getId());
//            System.out.println(foundAccount);
//
//
//            List<Account> accounts = entityManager
//                    .createQuery("select a from Account a where a.email = :email", Account.class)
//                    .setParameter("email", account.getEmail())
//                    .getResultList();
//
//            System.out.println(accounts);
//
//            entityManager.detach(account);
//            //back to session Return new account;
//            Account merge = entityManager.merge(account);
//
//            entityManager.remove(account);
//            entityManager.getTransaction().commit();
//        } catch (Exception ex) {
//            entityManager.getTransaction().rollback();
//        } finally {
//            entityManager.close();//session close
//            //account is detached
//        }
