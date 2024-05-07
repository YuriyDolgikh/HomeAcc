package biz.itehnika.services;

import biz.itehnika.config.AppConfig;
import biz.itehnika.model.Customer;
import biz.itehnika.model.CustomerRole;
import biz.itehnika.repos.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findByLogin(String login) {
        return customerRepository.findCustomerByLogin(login);
    }

    @Transactional
    public void deleteCustomers(List<Long> ids) {
        ids.forEach(id -> {
            Optional<Customer> customer = customerRepository.findById(id);
            customer.ifPresent(u -> {
                if ( ! AppConfig.ADMIN_LOGIN.equals(u.getLogin())) {
                    customerRepository.deleteById(u.getId());
                }
            });
        });
    }

    @Transactional
    public boolean addCustomer(String login, String passHash,
                               CustomerRole role, String email,
                               String phone,
                               String address) {
        if (customerRepository.existsCustomerByLogin(login) || customerRepository.existsCustomerByEmail(email))
            return false;

        Customer customer = new Customer(login, passHash, role, email, phone, address);
        customerRepository.save(customer);

        return true;
    }

    @Transactional
    public boolean updateCustomer(String login, String email, String phone, String address) {
        Customer customerToUpdate = customerRepository.findCustomerByLogin(login);

        if (customerRepository.existsCustomerByEmail(email)){

            Customer customerToCheck = customerRepository.findCustomerByEmail(email);
            if (!customerToUpdate.getLogin().equals(customerToCheck.getLogin())){
                return false;
            }
        }

        customerToUpdate.setEmail(email);
        customerToUpdate.setPhone(phone);
        customerToUpdate.setAddress(address);

        customerRepository.save(customerToUpdate);
        return true;
    }

    public boolean existCustomerByLogin(String login) {
        return customerRepository.existsCustomerByLogin(login);
    }
}
