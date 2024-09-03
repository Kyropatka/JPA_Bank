package com.gmail.deniska1406sme;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "Wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String currency;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exchange_id")
    private ExchangeRate exchangeRate;

    public Wallet(String currency, Double amount) {
        this.currency = currency;
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public Wallet() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount.doubleValue();
    }

    public void setAmount(Double amount) {
        this.amount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
