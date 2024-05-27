package biz.itehnika.services;

import biz.itehnika.model.*;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.repos.PaymentCategoryRepository;
import biz.itehnika.repos.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCategoryRepository paymentCategoryRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentCategoryRepository paymentCategoryRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentCategoryRepository = paymentCategoryRepository;
    }

    @Transactional
    public List<Payment> getPaymentsByCustomer(Customer customer){
        return paymentRepository.findByCustomer(customer);
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


}
