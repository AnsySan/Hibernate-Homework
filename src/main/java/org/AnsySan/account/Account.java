package org.AnsySan.account;

import jakarta.persistence.*;
import org.AnsySan.user.User;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "money_amount", nullable = false)
    private int moneyAmount;

    protected Account() {
    }

    public Account(User user, int moneyAmount) {
        if (user == null) throw new IllegalArgumentException("user must not be null");
        setMoneyAmount(moneyAmount);
        this.user = user;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getUserId() {
        return user == null ? null : user.getId();
    }

    public int getMoneyAmount() {
        return this.moneyAmount;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public String toString() {
        return "Account{id=" + this.id + ", userId=" + getUserId() + ", moneyAmount=" + this.moneyAmount + "}";
    }

    public User getUser() {
        return user;
    }
}