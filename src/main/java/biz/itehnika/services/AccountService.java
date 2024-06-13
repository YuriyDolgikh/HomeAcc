package biz.itehnika.services;

import biz.itehnika.model.Account;
import biz.itehnika.model.Customer;
import biz.itehnika.model.Payment;
import biz.itehnika.model.enums.AccountType;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.repos.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public Map<Long, Double> getAccountBallancesByCustomer(Customer customer){
        Map<Long, Double> ballances = new HashMap<>();
        List<Account> accounts = getAccountsByCustomer(customer);
        for(Account account : accounts){
            Double sum = 0.0;
            for (Payment payment : account.getPayments()){
                if(payment.getDirection()){
                    sum += payment.getAmount();
                }else{
                    sum -= payment.getAmount();
                }
            }
            ballances.put(account.getId(), sum);
        }
        return ballances;
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
