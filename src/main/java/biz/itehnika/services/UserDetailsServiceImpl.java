package biz.itehnika.services;

import biz.itehnika.model.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CustomerService customerService;

    public UserDetailsServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Customer customer = customerService.findByLogin(login);
        if (customer == null)
            throw new UsernameNotFoundException(login + " not found");

        List<GrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority(customer.getRole().toString())
        );

        return new User(customer.getLogin(), customer.getPassword(), roles);
    }
}