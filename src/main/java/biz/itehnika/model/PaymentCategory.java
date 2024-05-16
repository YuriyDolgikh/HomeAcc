package biz.itehnika.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@NoArgsConstructor
@ToString
public class PaymentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
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
