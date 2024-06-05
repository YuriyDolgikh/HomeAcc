package biz.itehnika.model;

import biz.itehnika.model.enums.AccountType;
import biz.itehnika.model.enums.CurrencyName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private AccountType type;
    @Enumerated(EnumType.STRING)
    private CurrencyName currencyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public Account(String name, String description, AccountType type, CurrencyName currencyName, Customer customer) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.currencyName = currencyName;
        this.customer = customer;
    }
}
