package com.gmail.deniska1406sme;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    Double usd;
    Double eur;
    Double uah;

    @OneToMany(mappedBy = "exchangeRate")
    private List<Wallet> wallets = new ArrayList<Wallet>();


    public ExchangeRate() {
    }

    public ExchangeRate(Double usd, Double eur, Double uah) {
        this.usd = usd;
        this.eur = eur;
        this.uah = uah;
    }

    public void addWallet(Wallet w) {
        wallets.add(w);
        w.setExchangeRate(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getEur() {
        return eur;
    }

    public void setEur(Double eur) {
        this.eur = eur;
    }

    public Double getUah() {
        return uah;
    }

    public void setUah(Double uah) {
        this.uah = uah;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "usd=" + usd +
                ", eur=" + eur +
                ", uah=" + uah +
                '}';
    }
}
