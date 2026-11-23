package org.AnsySan.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.AnsySan.account.Account;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "login", nullable = false, unique = true, length = 100)
    private String login;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accountList = new ArrayList<>();

    protected User() {
    }

    public User(String login) {
        this.login = login;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public List<Account> getAccountList() {
        return this.accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public String toString() {
        return "User{id=" + id + ", login='" + this.login + "', accountList=" + this.accountList + "}";
    }

    public void addAccount(Account account) {
        accountList.add(account);
    }

    public void removeAccount(Account closed) {
        accountList.remove(closed);
    }
}
