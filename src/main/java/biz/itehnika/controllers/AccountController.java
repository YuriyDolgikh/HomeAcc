package biz.itehnika.controllers;

import biz.itehnika.model.Account;
import biz.itehnika.model.Customer;
import biz.itehnika.model.enums.AccountType;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.services.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AccountController {

    public final AccountService accountService;
    public final CustomerService customerService;
    public final PaymentCategoryService paymentCategoryService;
    public final CurrencyService currencyService;

    public AccountController(AccountService accountService, CustomerService customerService, PaymentCategoryService paymentCategoryService, CurrencyService currencyService) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.paymentCategoryService = paymentCategoryService;
        this.currencyService = currencyService;
    }

    @GetMapping("/addNewAccount")
    public String addNewAccount(Model model) {
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("currencyNames", CurrencyName.values());
        return "addNewAccount";
    }

    @PostMapping(value = "/addNewAccount")
    public String addNewAccount(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String type,
                                @RequestParam String currencyName,
                                Model model) {

        User user = CustomerController.getCurrentUser();

        Customer customer = customerService.findByLogin(user.getUsername());

        if ( !accountService.addAccount(name, description, AccountType.valueOf(type), CurrencyName.valueOf(currencyName), customer)) {
            model.addAttribute("exists", true);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("type", type);
            model.addAttribute("currencyName", currencyName);
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("currencyNames", CurrencyName.values());
            return "addNewAccount";
        }
        model.addAttribute("registered", true);

        return "redirect:/settings";
    }



    @PostMapping(value = "/deleteAccount")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String deleteAccount(@RequestParam(name = "toDelete", required = false) List<Long> ids, Model model) {
        if (ids != null && !ids.isEmpty()) {
            accountService.deleteAccounts(ids);
        }
        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());
        model.addAttribute("accounts", accountService.getAccountsByCustomer(customer));

        if (CustomerController.isAdmin(user)){      // TODO probably no need redirect to /admin
            return "redirect:/admin";
        }
        return "redirect:/settings";
    }

    @GetMapping("/updateAccount/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String updateAccount(@PathVariable(value = "id") Long id, Model model) {

        Account account = accountService.getById(id);

        model.addAttribute("name", account.getName());
        model.addAttribute("description", account.getDescription());
        model.addAttribute("type", account.getType());
        model.addAttribute("currencyName", account.getCurrencyName());
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("currencyNames", CurrencyName.values());

        return "updateAccount";
    }

    @PostMapping(value = "/updateAccount")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')") // SpEL !!!
    public String updateAccount(@RequestParam() Long id,
                                @RequestParam String name,
                                @RequestParam String description,
                                @RequestParam String type,
                                @RequestParam String currencyName,
                                Model model) {
        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());

        if ( ! accountService.updateAccount(id, name, description, AccountType.valueOf(type), CurrencyName.valueOf(currencyName), customer)) {
            Account account = accountService.getById(id);
            model.addAttribute("exists", true);
            model.addAttribute("name", account.getName());
            model.addAttribute("description", account.getDescription());
            model.addAttribute("type", account.getType());
            model.addAttribute("currencyName", account.getCurrencyName());
            model.addAttribute("accountTypes", AccountType.values());
            model.addAttribute("currencyNames", CurrencyName.values());
            model.addAttribute("id", id);
            return "updateAccount";
        }else {
            return "redirect:/settings";
        }
    }

    @GetMapping("/accountsStatistic")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String accountsStatistic(Model model){
        User user = CustomerController.getCurrentUser();
        Customer customer = customerService.findByLogin(user.getUsername());

        model.addAttribute("accountsUAH", accountService.getAccountsByCurrencyNameAndCustomer(CurrencyName.UAH, customer));
        model.addAttribute("accountsEUR", accountService.getAccountsByCurrencyNameAndCustomer(CurrencyName.EUR, customer));
        model.addAttribute("accountsUSD", accountService.getAccountsByCurrencyNameAndCustomer(CurrencyName.USD, customer));
        model.addAttribute("totalUAH", accountService.getTotalByCurrencyNameAndCustomer(CurrencyName.UAH, customer));
        model.addAttribute("totalEUR", accountService.getTotalByCurrencyNameAndCustomer(CurrencyName.EUR, customer));
        model.addAttribute("totalUSD", accountService.getTotalByCurrencyNameAndCustomer(CurrencyName.USD, customer));
        model.addAttribute("balances", accountService.getAccountbalancesByCustomer(customer));

        return "accountsStatistic";
    }

}
