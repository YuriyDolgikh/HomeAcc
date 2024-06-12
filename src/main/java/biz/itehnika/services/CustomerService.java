package biz.itehnika.services;

import biz.itehnika.config.AppConfig;
import biz.itehnika.model.Customer;
import biz.itehnika.model.enums.CustomerRole;
import biz.itehnika.repos.CustomerRepository;
import biz.itehnika.repos.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PaymentCategoryService paymentCategoryService;
    private final PaymentRepository paymentRepository;

    public CustomerService(CustomerRepository customerRepository, PaymentCategoryService paymentCategoryService, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.paymentCategoryService = paymentCategoryService;
        this.paymentRepository = paymentRepository;
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
        customer.setFilters(true, true, true, true, true, true, true);
        customerRepository.save(customer);
        paymentCategoryService.initPaymentCategoriesForCustomer(findByLogin(login));

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

    @Transactional(readOnly = true)
    public Map<String, LocalDate> getWorkPeriod(Long customerId){
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Map<String, LocalDate> period = new HashMap<>();
        LocalDate startDate = customer.getStartDate();
        LocalDate endDate = customer.getEndDate();
        ZoneId zoneId = ZoneId.of(System.getProperty("user.timezone"));
        if (startDate == null) {
            startDate = LocalDate.now(zoneId);
        }
        if (endDate == null) {
            endDate = LocalDate.now(zoneId);
        }
        period.put("startDate", startDate);
        period.put("endDate", endDate);
        return period;
    }

    @Transactional
    public void setWorkPeriod(Long customerId, LocalDate startDate, LocalDate endDate){
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        ZoneId zoneId = ZoneId.of(System.getProperty("user.timezone"));
        if (startDate == null) {
            startDate = LocalDate.now(zoneId);
        }
        if (endDate == null) {
            endDate = LocalDate.now(zoneId);
        }
        customer.setStartDate(startDate);
        customer.setEndDate(endDate);
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> getFilters(Long customerId){
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Map<String, Boolean> filters = customer.getFilters();
        return filters;
    }

    @Transactional
    public void setFilter(Long customerId, Map<String, Boolean> filters){
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        customer.setFilters(filters.get("isUAH"),
                            filters.get("isEUR"),
                            filters.get("isUSD"),
                            filters.get("isIN"),
                            filters.get("isOUT"),
                            filters.get("isCompleted"),
                            filters.get("isScheduled"));
        customerRepository.save(customer);
    }

    public Map<String, Boolean> translateFiltersToMap(List<String> filtersList){
        Map<String, Boolean> filters = new HashMap<>();

        filters.put("isUAH", filtersList.contains("UAH"));
        filters.put("isEUR", filtersList.contains("EUR"));
        filters.put("isUSD", filtersList.contains("USD"));
        filters.put("isIN", filtersList.contains("IN"));
        filters.put("isOUT", filtersList.contains("OUT"));
        filters.put("isCompleted", filtersList.contains("Completed"));
        filters.put("isScheduled", filtersList.contains("Scheduled"));

        return filters;
    }

    public static String getCustomerDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

}
