package biz.itehnika.repos;

import biz.itehnika.model.Customer;
import biz.itehnika.model.Payment;
import biz.itehnika.model.enums.CurrencyName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCustomerOrderByDateTime(Customer customer);
    List<Payment> findByCustomerAndDateTimeBetweenOrderByDateTime(Customer customer, LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByCustomerAndCurrencyName(Customer customer, CurrencyName currencyName);
    List<Payment> findByCustomerAndCurrencyNameInAndDirectionInAndStatusInAndDateTimeBetweenOrderByDateTimeAsc(Customer customer,
                                                                                                                List<CurrencyName> currencyNames,
                                                                                                                List<Boolean> directions,
                                                                                                                List<Boolean> statuses,
                                                                                                                LocalDateTime startDate,
                                                                                                                LocalDateTime endDate);
}
