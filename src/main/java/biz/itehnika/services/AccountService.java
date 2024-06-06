package biz.itehnika.services;

import biz.itehnika.model.Account;
import biz.itehnika.model.Customer;
import biz.itehnika.model.PaymentCategory;
import biz.itehnika.model.enums.AccountType;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.repos.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Transactional
    public Account getById(Long id){
        return accountRepository.findById(id).orElseThrow();
    }

    @Transactional
    public List<Account> getAccountsByCurrencyNameAndCustomer(CurrencyName currencyName, Customer customer){
        return accountRepository.findAccountsByCurrencyNameAndCustomer(currencyName, customer);
    }

    @Transactional
    public List<Account> getAccountsByCustomer(Customer customer){
        return accountRepository.findAccountsByCustomer(customer);
    }

    @Transactional
    public Account getAccountByNameAndCustomer(String name, Customer customer){
        return accountRepository.findByNameAndCustomer(name, customer);
    }

    @Transactional
    public boolean addAccount(String name, String description, AccountType type, CurrencyName currencyName, Customer customer){
        Account accountToCheck = accountRepository.findByNameAndCustomer(name, customer);
        if (accountToCheck != null && accountToCheck.getName().equals(name)){
            return false;
        }
        Account account = new Account(name, description, type, currencyName, customer);
        accountRepository.save(account);
        return true;
    }

    @Transactional
    public void deleteAccounts(List<Long> ids) {
        ids.forEach(id -> {
            Optional<Account> account = accountRepository.findById(id);
            account.ifPresent(u -> accountRepository.deleteById(u.getId()));
        });
    }

    @Transactional
    public boolean updateAccount(Long id, String newName, String newDescription, AccountType newType, CurrencyName newCurrencyName, Customer customer) {
        Account accountToUpdate = getById(id);
        Account accountToCheck = getAccountByNameAndCustomer(newName, customer);
        if (accountToCheck != null && !accountToCheck.getName().equals(accountToUpdate.getName())){
            return false;
        }
        accountToUpdate.setName(newName);
        accountToUpdate.setDescription(newDescription);
        accountToUpdate.setType(newType);
        accountToUpdate.setCurrencyName(newCurrencyName);
        accountRepository.save(accountToUpdate);
        return true;
    }



}
