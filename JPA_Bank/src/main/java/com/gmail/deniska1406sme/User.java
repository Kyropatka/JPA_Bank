package com.gmail.deniska1406sme;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    public User() {
    }

    public User(String userName) {
        this.userName = userName;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setUser(this);
    }

    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
        wallet.setUser(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                '}';
    }
}
