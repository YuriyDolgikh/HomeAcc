package biz.itehnika.model;

import biz.itehnika.model.enums.CustomerRole;
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
public class Customer {
    @Id
    @GeneratedValue
    private Long id;

    private String login;
    private String password;

    @Enumerated(EnumType.STRING)
    private CustomerRole role;

    private String email;
    private String phone;
    private String address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<PaymentCategory> paymentCategories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Payment> payments = new ArrayList<>();

    public Customer(String login, String password, CustomerRole role,
                    String email, String phone, String address) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

}
