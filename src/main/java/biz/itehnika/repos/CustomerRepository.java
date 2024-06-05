package biz.itehnika.repos;

import biz.itehnika.model.Customer;
import biz.itehnika.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
//    @Query("SELECT u FROM Customer u where u.login = :login")
//    Customer findByLogin(@Param("login") String login);
//
//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Customer u WHERE u.login = :login")
//    boolean existsByLogin(@Param("login") String login);

    Customer findCustomerByLogin(String login);
    Customer findCustomerByEmail(String email);

    boolean existsCustomerByLogin(String login);
    boolean existsCustomerByEmail(String email);
}
