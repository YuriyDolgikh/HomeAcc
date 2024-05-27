package biz.itehnika.model;

import biz.itehnika.model.enums.CurrencyName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime dateTime;
    private Boolean direction;          // True - income, False - waste
    private Boolean status;             // True - Completed, False - Scheduled
    private Double amount;
    private CurrencyName currencyName;
    private String description;


    @OneToOne
    @JoinColumn(name = "paymentCategory_id")
    private PaymentCategory paymentCategory;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Payment(LocalDateTime dateTime,
                   Boolean direction,
                   Boolean status,
                   Double amount,
                   CurrencyName currencyName,
                   String description,
                   PaymentCategory paymentCategory,
                   Account account,
                   Customer customer) {
        this.dateTime = dateTime;
        this.direction = direction;
        this.status = status;
        this.amount = amount;
        this.currencyName = currencyName;
        this.description = description;
        this.paymentCategory = paymentCategory;
        this.account = account;
        this.customer = customer;
    }
}
