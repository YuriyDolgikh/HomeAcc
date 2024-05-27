package biz.itehnika.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class PaymentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;       //TODO - will be Not null
    private String description;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public PaymentCategory(String name, String description, Customer customer) {
        this.name = name;
        this.description = description;
        this.customer = customer;
    }
}
