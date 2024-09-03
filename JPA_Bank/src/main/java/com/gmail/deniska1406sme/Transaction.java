package com.gmail.deniska1406sme;

import javax.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private Double amount;
    private String currency;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction(String type, Double amount, String currency) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
    }

    public Transaction() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", user=" + user +
                '}';
    }
}
