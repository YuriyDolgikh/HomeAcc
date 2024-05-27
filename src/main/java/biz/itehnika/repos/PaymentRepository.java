package biz.itehnika.repos;

import biz.itehnika.model.Customer;
import biz.itehnika.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer(Customer customer);
}
