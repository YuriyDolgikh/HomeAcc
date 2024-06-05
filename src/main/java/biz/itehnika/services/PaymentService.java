package biz.itehnika.services;

import biz.itehnika.model.*;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.repos.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;

    public PaymentService(PaymentRepository paymentRepository, CustomerService customerService) {
        this.paymentRepository = paymentRepository;
        this.customerService = customerService;
    }

    @Transactional
    public Payment getById(Long id){
        return paymentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public List<Payment> getAllPaymentsByCustomer(Customer customer){
        return paymentRepository.findByCustomer(customer);
    }

    @Transactional
    public List<Payment> getPaymentsByCustomerAndPeriod(Customer customer, LocalDate startDate, LocalDate endDate){
        return paymentRepository.findByCustomerAndDateTimeBetweenOrderByDateTime(customer,
                                              LocalDateTime.of(startDate, LocalTime.MIN),
                                              LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Transactional
    public List<Payment> getPaymentsByCustomerAndAllFilters(Customer customer,
                                                            LocalDate startDate,
                                                            LocalDate endDate
                                                            ){
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());
        List<CurrencyName> currencyNames = new ArrayList<>();
        List<Boolean> directions = new ArrayList<>();
        List<Boolean> statuses = new ArrayList<>();
        if (filters.get("isUAH")) currencyNames.add(CurrencyName.UAH);
        if (filters.get("isEUR")) currencyNames.add(CurrencyName.EUR);
        if (filters.get("isUSD")) currencyNames.add(CurrencyName.USD);
        if (filters.get("isIN")) directions.add(true);
        if (filters.get("isOUT")) directions.add(false);
        if (filters.get("isCompleted")) statuses.add(true);
        if (filters.get("isScheduled")) statuses.add(false);
        return paymentRepository.findByCustomerAndCurrencyNameInAndDirectionInAndStatusInAndDateTimeBetweenOrderByDateTimeAsc(
                                                    customer,
                                                    currencyNames,
                                                    directions,
                                                    statuses,
                                                    LocalDateTime.of(startDate, LocalTime.MIN),
                                                    LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Transactional
    public void addPayment(LocalDateTime dateTime,
                           Boolean direction,
                           Boolean status,
                           Double amount,
                           CurrencyName currencyName,
                           String description,
                           PaymentCategory paymentCategory,
                           Account account,
                           Customer customer){
        Payment payment = new Payment(dateTime, direction, status, amount, currencyName, description, paymentCategory, account, customer);
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayments(List<Long> ids) {
        ids.forEach(id -> {
            Optional<Payment> payment = paymentRepository.findById(id);
            payment.ifPresent(u -> paymentRepository.deleteById(u.getId()));
        });
    }

    @Transactional
    public void updatePayment(Long id,
                                 LocalDateTime dateTime,
                                 Boolean direction,
                                 Boolean status,
                                 Double amount,
                                 CurrencyName currencyName,
                                 String description,
                                 PaymentCategory paymentCategory,
                                 Account account) {
        Payment paymentToUpdate = getById(id);

        paymentToUpdate.setDateTime(dateTime);
        paymentToUpdate.setDirection(direction);
        paymentToUpdate.setStatus(status);
        paymentToUpdate.setAmount(amount);
        paymentToUpdate.setCurrencyName(currencyName);
        paymentToUpdate.setDescription(description);
        paymentToUpdate.setPaymentCategory(paymentCategory);
        paymentToUpdate.setAccount(account);
        paymentRepository.save(paymentToUpdate);
    }

}
