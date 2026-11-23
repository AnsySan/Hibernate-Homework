package org.AnsySan.transaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionManager {
    private final SessionFactory sessionFactory;

    public TransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T execute(Supplier<T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        boolean owner = transaction.getStatus() == TransactionStatus.NOT_ACTIVE;

        if (owner) transaction = session.beginTransaction();

        try {
            T result = action.get();
            if (owner) transaction.commit();
            return result;
        } catch (RuntimeException | Error exception) {
            if (owner && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            throw exception;
        } finally {
            if (owner && session.isOpen()) session.close();
        }
    }
    public void execute(Runnable action) {
        execute(() -> {
            action.run();
            return null;
        });
    }
}
