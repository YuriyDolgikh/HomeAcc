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
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private AccountType type;
    private CurrencyName currencyName;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private List<Payment> payments = new ArrayList<>();

    public Account(String name, String description, AccountType type, CurrencyName currencyName, Customer customer) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.currencyName = currencyName;
        this.customer = customer;
    }
}
