package biz.itehnika.dto;

import biz.itehnika.model.enums.CustomerRole;
import lombok.Getter;

@Getter
public class CustomerDTO {

    private final String login;
    private final String password;
    private final CustomerRole role;

    private final String email;
    private final String phone;
    private final String address;

    public CustomerDTO(String login, String password, CustomerRole role, String email, String phone, String address) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public static CustomerDTO of(String login, String password, CustomerRole role, String email, String phone, String address){
        return new CustomerDTO(login, password, role, email, phone, address);
    }

}
