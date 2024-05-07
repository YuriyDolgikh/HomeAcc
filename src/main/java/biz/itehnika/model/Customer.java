package biz.itehnika.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
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
