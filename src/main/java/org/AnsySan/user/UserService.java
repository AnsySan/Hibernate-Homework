package org.AnsySan.user;

import java.util.List;
import java.util.Optional;
import org.AnsySan.account.AccountService;
import org.AnsySan.transaction.TransactionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private final SessionFactory sessionFactory;
    private final TransactionManager transactionManager;
    private final AccountService accountService;

    @Autowired
    public UserService(SessionFactory sessionFactory, TransactionManager transactionManager, AccountService accountService) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.accountService = accountService;
    }

    public User createUser(String login) {
        String correctLogin = validateLogin(login);
        return transactionManager.execute(() ->{
            Session session = sessionFactory.getCurrentSession();
            Long count = session.createQuery("select count(u) from User u where u.login= :login", Long.class)
                    .setParameter("login", correctLogin).getSingleResult();
            if (count > 0){
                throw new IllegalArgumentException("User already exists with login=%s".formatted(correctLogin));
            }
            User user = new User(correctLogin);
            session.persist(user);
            accountService.createAccount(user);
            return user;
        });
    }

    private String validateLogin(String login) {
        if (login != null && !login.isBlank()) {
            return login.trim();
        } else {
            throw new IllegalArgumentException("login must not be blank");
        }
    }

    public Optional<User> findUserById(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID must be > 0");
        return transactionManager.execute(() -> Optional.ofNullable(sessionFactory.getCurrentSession().createQuery(
                        "select distinct u from User u left join fetch u.accountList where u.id = :id", User.class)
                .setParameter("id", id).uniqueResult()));
    }

    public List<User> findAll() {
        return transactionManager.execute(() -> sessionFactory.getCurrentSession()
                .createQuery("select distinct u from User u left join fetch u.accountList order by u.id", User.class)
                .getResultList());
    }
}