package com.gmail.deniska1406sme;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Scanner;

public class App {
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPATest");
    static EntityManager em = emf.createEntityManager();

    public static void main( String[] args ) {

        em.getTransaction().begin();
        try {
            User user1 = new User("Denys");
            User user2 = new User("Alice");

            em.persist(user1);
            em.persist(user2);

            Wallet wallet1 = new Wallet("uah", 200.00);
            Wallet wallet2 = new Wallet("usd", 100.00);
            Wallet wallet3 = new Wallet("eur", 50.00);
            Wallet wallet4 = new Wallet("uah", 500.00);
            Wallet wallet5 = new Wallet("usd", 500.00);
            Wallet wallet6 = new Wallet("eur", 500.00);
            em.persist(wallet1);
            em.persist(wallet2);
            em.persist(wallet3);
            em.persist(wallet4);
            em.persist(wallet5);
            em.persist(wallet6);

            user1.addWallet(wallet1);
            user1.addWallet(wallet2);
            user1.addWallet(wallet3);
            user2.addWallet(wallet4);
            user2.addWallet(wallet5);
            user2.addWallet(wallet6);

            em.persist(user1);
            em.persist(user2);

            ExchangeRate exchangeRate = new ExchangeRate(41.33,45.63,1.0);

            exchangeRate.addWallet(wallet1);
            exchangeRate.addWallet(wallet2);
            exchangeRate.addWallet(wallet3);
            exchangeRate.addWallet(wallet4);
            exchangeRate.addWallet(wallet5);
            exchangeRate.addWallet(wallet6);

            em.persist(exchangeRate);
            em.getTransaction().commit();
        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        showUserWallets(em.getReference(User.class, 1L));
        walletReplenishment(em.getReference(Wallet.class,3L),200.00);
        showUserWallets(em.getReference(User.class, 1L));

        transferMoney(em.getReference(Wallet.class,7L), 100.00, em.getReference(Wallet.class,5L));//user2_dolar -> user1_euro
        transferMoney(em.getReference(Wallet.class,6L), 100.00, em.getReference(Wallet.class,3L));//user2_uah -> user1_uah
        showUserWallets(em.getReference(User.class, 1L));
        showUserWallets(em.getReference(User.class, 2L));

        convertMoney(em.getReference(User.class, 1L));
        showUserWallets(em.getReference(User.class, 1L));

        getSummaryAmount(em.getReference(User.class, 1L));
        System.out.println();

        showUserTransactions(em.getReference(User.class, 1L));



    }

    public static void walletReplenishment(Wallet wallet, Double amount){
        em.getTransaction().begin();
        try {
            wallet.setAmount(wallet.getAmount() + amount);
            Transaction transaction = new Transaction("replenishment", amount, wallet.getCurrency());
            wallet.getUser().addTransaction(transaction);
            em.merge(wallet);
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
        }
    }

    public static void transferMoney(Wallet walletFrom, Double amount, Wallet walletTo){
        Double convertedAmount;

        em.getTransaction().begin();
        try {
            checkAmount(walletFrom,amount);
            walletFrom.setAmount(walletFrom.getAmount() - amount);
            Transaction transaction = new Transaction("transfer", amount, walletFrom.getCurrency());
            walletFrom.getUser().addTransaction(transaction);
            if(!walletFrom.getCurrency().equals(walletTo.getCurrency())){
                convertedAmount = convertAmountOfMoney(walletFrom,amount,walletTo);
                walletTo.setAmount(walletTo.getAmount() + convertedAmount);
            }else {
                walletTo.setAmount(walletTo.getAmount() + amount);
            }
            Transaction transaction2 = new Transaction("transfer", amount, walletTo.getCurrency());
            walletTo.getUser().addTransaction(transaction2);
            em.merge(walletFrom);
            em.merge(walletTo);
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public static Double convertAmountOfMoney(Wallet walletFrom, Double amount, Wallet walletTo){
        String walletFromCurrency = walletFrom.getCurrency();
        String walletToCurrency = walletTo.getCurrency();
        ExchangeRate exchangeRate = walletFrom.getExchangeRate();
        Double convertedAmount = 0.0;

        if ("usd".equals(walletFromCurrency) && "eur".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getEur() / exchangeRate.getUsd();
        } else if ("eur".equals(walletFromCurrency) && "usd".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getUsd() / exchangeRate.getEur();
        } else if ("usd".equals(walletFromCurrency) && "uah".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getUsd() / exchangeRate.getUah();
        } else if ("uah".equals(walletFromCurrency) && "usd".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getUah() / exchangeRate.getUsd();
        } else if ("eur".equals(walletFromCurrency) && "uah".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getEur() / exchangeRate.getUah();
        } else if ("uah".equals(walletFromCurrency) && "eur".equals(walletToCurrency)) {
            convertedAmount = amount * exchangeRate.getUah() / exchangeRate.getEur();
        }
        return convertedAmount;
    }

    public static void convertMoney(User user){
        Scanner sc = new Scanner(System.in);
        System.out.print("Which currency you want to convert?: ");
        String fromCurrency = sc.nextLine();
        System.out.print("Enter amount you want to convert: ");
        String amountInsert = sc.nextLine();
        Double amount = Double.parseDouble(amountInsert);
        System.out.print("In which currency you want to convert " + amountInsert + " " + fromCurrency + "?: ");
        String toCurrency = sc.nextLine();
        Wallet walletFrom = findUserWallet(user,fromCurrency);
        Wallet walletTo = findUserWallet(user,toCurrency);
        Double convertedAmount = convertAmountOfMoney(walletFrom,amount,walletTo);

        em.getTransaction().begin();
        try {
            checkAmount(walletFrom,amount);
            walletFrom.setAmount(walletFrom.getAmount() - amount);
            walletTo.setAmount(walletTo.getAmount() + convertedAmount);
            Transaction transaction = new Transaction("convert", amount, walletFrom.getCurrency());
            Transaction transaction2 = new Transaction("convert", amount, walletTo.getCurrency());
            user.addTransaction(transaction);
            user.addTransaction(transaction2);
            em.merge(user);
            em.merge(walletFrom);
            em.merge(walletTo);
            em.getTransaction().commit();
        }catch (Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public static Wallet findUserWallet(User user,String currency){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Wallet> query = cb.createQuery(Wallet.class);
        Root<Wallet> from = query.from(Wallet.class);

        Predicate userPredicate = cb.equal(from.get("user"), user);
        Predicate currencyPredicate = cb.equal(from.get("currency"), currency);

        query.select(from).where(cb.and(userPredicate,currencyPredicate));

        return em.createQuery(query).getSingleResult();
    }

    public static void checkAmount(Wallet wallet, Double amount){
        if(wallet.getAmount() < amount){
            throw new IllegalArgumentException("The amount in the wallet is less than requested");
        }
    }


    public static void getSummaryAmount(User user){
        Wallet walletUSD = findUserWallet(user,"usd");
        System.out.println(walletUSD);
        Wallet walletEUR = findUserWallet(user,"eur");
        Wallet walletUAH = findUserWallet(user,"uah");

        Double amountFromUSD =  convertAmountOfMoney(walletUSD, walletUSD.getAmount(), walletUAH);
        Double amountFromEUR =  convertAmountOfMoney(walletEUR, walletEUR.getAmount(), walletUAH);

        Double amountResult = amountFromUSD + amountFromEUR + walletUAH.getAmount();
        System.out.print("Total amount of wallets in UAH: " + amountResult);
    }

    public static void showUserWallets(User user){
        user.getWallets().forEach(System.out::println);
    }
    public static void showUserTransactions(User user){
        user.getTransactions().forEach(System.out::println);
    }
}
