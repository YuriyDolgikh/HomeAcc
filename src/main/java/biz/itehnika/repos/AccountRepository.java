package biz.itehnika.repos;

import biz.itehnika.model.Account;
import biz.itehnika.model.Customer;
import biz.itehnika.model.enums.CurrencyName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAccountsByCustomer(Customer customer);
    List<Account> findAccountsByCurrencyNameAndCustomer(CurrencyName currencyName, Customer customer);

    boolean existsAccountByNameAndCustomer(String name, Customer customer);

    Account findByNameAndCustomer(String name, Customer customer);
}
