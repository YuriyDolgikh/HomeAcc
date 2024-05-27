package biz.itehnika.repos;

import biz.itehnika.model.Customer;
import biz.itehnika.model.PaymentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory, Long> {

    PaymentCategory findPaymentCategoryByNameAndCustomer(String name, Customer customer);

    boolean existsPaymentCategoryByNameAndCustomer(String name, Customer customer);

    List<PaymentCategory> findPaymentCategoriesByCustomer(Customer customer);
}
