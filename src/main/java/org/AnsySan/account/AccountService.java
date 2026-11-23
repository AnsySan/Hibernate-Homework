package org.AnsySan.account;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.AnsySan.transaction.TransactionManager;
import org.AnsySan.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountService extends AbstractAccountService {
    private final SessionFactory sessionFactory;
    private final TransactionManager transactionManager;
    private final AccountProperties properties;

    @Autowired
    public AccountService(SessionFactory sessionFactory, TransactionManager transactionManager, AccountProperties properties) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.properties = properties;
    }

    public void createAccount(User user) {
        this.validatePositiveId(user.getId());
        transactionManager.execute(() -> {
            Session session = sessionFactory.getCurrentSession();
            User userAccount = session.find(User.class, user.getId());
            if (userAccount == null) {
                throw new IllegalArgumentException("User not found: id=" + user.getId());
            } else {
                Account account = new Account(userAccount, properties.getDefaultAmount());
                userAccount.addAccount(account);
                session.persist(account);
                return account;
            }
        });
    }

    public Account createAccountForUser(int userId) {
        this.validatePositiveId(userId);
        return transactionManager.execute(() -> {
            User user = sessionFactory.getCurrentSession().find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("No such user: id=" + userId);
            } else {
                Account account = new Account(user, properties.getDefaultAmount());
                user.addAccount(account);
                sessionFactory.getCurrentSession().persist(account);
                return account;
            }
        });
    }

    public Account deposit(int accountId, int amount) {
        validate(accountId, amount);
        return transactionManager.execute(() -> {
            Account account = requireAccount(accountId);
            account.setMoneyAmount(Math.addExact(account.getMoneyAmount(), amount));
            return account;
        });
    }

    public void withdraw(int accountId, int amount) {
        validate(accountId, amount);
        transactionManager.execute(() -> {
            Account account = requireAccount(accountId);
            account.setMoneyAmount(Math.addExact(account.getMoneyAmount(), amount));
            return account;
        });
    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        this.validatePositiveId(fromAccountId);
        this.validatePositiveId(toAccountId);
        this.validatePositiveAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("source and target account id must be different");
        } else {
            transactionManager.execute(() -> {
                Account from = requireAccount(fromAccountId), to = requireAccount(toAccountId);
                if (amount > from.getMoneyAmount()) {
                    throw new IllegalArgumentException("insufficient funds on account id=%s, moneyAmount=%s, attempted withdraw=%s".formatted(from.getId(), from.getMoneyAmount(), amount));
                } else {
                    int received = from.getUserId().equals(to.getUserId()) ? amount
                            : (int) Math.round(amount * (1.0 - properties.getTransferCommission()));
                    int sourceAfter = Math.subtractExact(from.getMoneyAmount(), amount);
                    int targetAfter = Math.addExact(to.getMoneyAmount(), received);
                    from.setMoneyAmount(sourceAfter);
                    to.setMoneyAmount(targetAfter);
                }
            });
        }
    }

    public Account close(int id) {
        this.validatePositiveId(id);
        return transactionManager.execute(() -> {
            Session session = sessionFactory.getCurrentSession();
            Account closed = requireAccount(id);
            User owner = closed.getUser();
            List<Account> accounts = session.createQuery("select a from Account a where a.user.id = :uid order by a.id", Account.class)
                    .setParameter("uid", owner.getId()).getResultList();
            if (accounts.size() <= 1) throw new IllegalStateException("Can't close the only one account");
            Account target = accounts.stream().filter(account -> !account.getId().equals(id)).min(Comparator.comparing(Account::getId)).orElseThrow();
            target.setMoneyAmount(Math.addExact(target.getMoneyAmount(), closed.getMoneyAmount()));
            owner.removeAccount(closed);
            session.remove(closed);
            return closed;
        });
    }

    public Optional<Account> findAccountById(Integer idAccount) {
        this.validatePositiveId(idAccount);
        return transactionManager.execute(() -> Optional.ofNullable(sessionFactory.getCurrentSession().find(Account.class, idAccount)));
    }


    public List<Account> getUserAccounts(Integer userId) {
        validatePositiveId(userId);
        return transactionManager.execute(() -> sessionFactory.getCurrentSession()
                .createQuery("select a from Account a where a.user.id = :uid order by a.id", Account.class)
                .setParameter("uid", userId).getResultList());
    }

    public double getTransferCommission() {
        return properties.getTransferCommission();
    }

    private Account requireAccount(int id) {
        Account account = sessionFactory.getCurrentSession().find(Account.class, id);
        if (account == null) throw new IllegalArgumentException("Account with id=%s not found".formatted(id));
        return account;
    }
}