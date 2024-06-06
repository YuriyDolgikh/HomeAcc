package biz.itehnika.controllers;

import biz.itehnika.model.Account;
import biz.itehnika.model.Customer;
import biz.itehnika.model.Payment;
import biz.itehnika.model.PaymentCategory;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.services.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentController {
    public final PaymentService paymentService;
    public final PaymentCategoryService paymentCategoryService;
    public final CustomerService customerService;
    public final CurrencyService currencyService;
    public final AccountService accountService;

    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PaymentController(PaymentService paymentService, PaymentCategoryService paymentCategoryService, CustomerService customerService, CurrencyService currencyService, AccountService accountService) {
        this.paymentService = paymentService;
        this.paymentCategoryService = paymentCategoryService;
        this.customerService = customerService;
        this.currencyService = currencyService;
        this.accountService = accountService;
    }

    @PostMapping(value = "/setPeriod")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String setPeriod(@RequestParam(value = "startDate") String startDate,
                            @RequestParam(value = "endDate") String endDate,
                            Model model) {
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());
        customerService.setWorkPeriod(customer.getId(),LocalDate.parse(startDate), LocalDate.parse(endDate));
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());
        Map<String, Double> statistic = paymentService.getStatistic(customer);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("filters", filters);
        model.addAttribute("payments", paymentService.getPaymentsByCustomerAndAllFilters(customer));
        model.addAttribute("statistic", statistic);
        return "accounting";
    }

    @PostMapping(value = "/setFilters")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String setFilters(@RequestParam(name = "newFilters", required = false) List<String> filtersList, Model model) {
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());

        customerService.setFilter(customer.getId(), customerService.translateFiltersToMap(filtersList));
        Map<String, LocalDate> workPeriod = customerService.getWorkPeriod(customer.getId());
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());
        List<Payment> payments = paymentService.getPaymentsByCustomerAndAllFilters(customer);
        Map<String, Double> statistic = paymentService.getStatistic(customer);
        model.addAttribute("statistic", statistic);
        model.addAttribute("startDate", workPeriod.get("startDate").format(dateFormatter));
        model.addAttribute("endDate", workPeriod.get("endDate").format(dateFormatter));
        model.addAttribute("payments", payments);
        model.addAttribute("filters", filters);
        return "accounting";
    }

    @GetMapping("/accounting")      //TODO Don't set Dates
    public String accounting(Model model) {
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());

        Map<String, LocalDate> workPeriod = customerService.getWorkPeriod(customer.getId());
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());

        LocalDate startDate = workPeriod.get("startDate");
        LocalDate endDate = workPeriod.get("endDate");
        List<Payment> payments = paymentService.getPaymentsByCustomerAndAllFilters(customer);
        Map<String, Double> statistic = paymentService.getStatistic(customer);
        model.addAttribute("statistic", statistic);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("payments", payments);
        model.addAttribute("filters", filters);

        return "accounting";
    }

    @GetMapping("/addNewPayment")
    public String addNewPayment(Model model) {
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());

        model.addAttribute("dateTime", LocalDateTime.now().format(dateTimeFormatter));
        model.addAttribute("accounts", accountService.getAccountsByCustomer(customer));
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(customer));
        Map<String, LocalDate> workPeriod = customerService.getWorkPeriod(customer.getId());
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());
        model.addAttribute("filters", filters);
        model.addAttribute("payments", paymentService.getPaymentsByCustomerAndAllFilters(customer));
        return "addNewPayment";
    }

    @PostMapping(value = "/addNewPayment")
    public String addNewPayment(@RequestParam String dateTime,
                                @RequestParam String accountName,
                                @RequestParam String paymentCategoryName,
                                @RequestParam String description,
                                @RequestParam String direction,
                                @RequestParam String status,
                                @RequestParam Double amount,
                                Model model) {

        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());
        Account account = accountService.getAccountByNameAndCustomer(accountName, customer);
        PaymentCategory paymentCategory = paymentCategoryService.getByNameAndCustomer(paymentCategoryName, customer);
        CurrencyName currencyName = account.getCurrencyName();

        paymentService.addPayment(LocalDateTime.parse(dateTime),
                                Boolean.valueOf(direction),
                                Boolean.valueOf(status),
                                amount,
                                currencyName,
                                description,
                                paymentCategory,
                                account,
                                customer);
        Map<String, LocalDate> workPeriod = customerService.getWorkPeriod(customer.getId());
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());

        model.addAttribute("payments", paymentService.getPaymentsByCustomerAndAllFilters(customer));
        model.addAttribute("added", true);
        model.addAttribute("filters", filters);

        return "redirect:/accounting";
    }

    @PostMapping(value = "/deletePayment")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String deletePayment(@RequestParam(name = "toDelete", required = false) List<Long> ids, Model model) {
        if (ids != null && !ids.isEmpty()) {
            paymentService.deletePayments(ids);
        }
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());
        Map<String, LocalDate> workPeriod = customerService.getWorkPeriod(customer.getId());
        Map<String, Boolean> filters = customerService.getFilters(customer.getId());
        model.addAttribute("filters", filters);
        model.addAttribute("startDate", workPeriod.get("startDate").format(dateFormatter));
        model.addAttribute("endDate", workPeriod.get("endDate").format(dateFormatter));
        model.addAttribute("payments", paymentService.getPaymentsByCustomerAndAllFilters(customer));

        return "redirect:/accounting";
    }

    @GetMapping("/updatePayment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String updatePayment(@PathVariable(value = "id") Long id, Model model) {

        Payment payment = paymentService.getById(id);
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());

        model.addAttribute("id", id);
        model.addAttribute("dateTime", payment.getDateTime());
        model.addAttribute("direction", payment.getDirection());
        model.addAttribute("status", payment.getStatus());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("account", payment.getAccount());
        model.addAttribute("description", payment.getDescription());
        model.addAttribute("paymentCategory", payment.getPaymentCategory());
        model.addAttribute("accounts", accountService.getAccountsByCustomer(customer));
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(customer));

        return "updatePayment";
    }

    @PostMapping(value = "/updatePayment")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public String updatePayment(@RequestParam Long id,
                                @RequestParam String dateTime,
                                @RequestParam Boolean direction,
                                @RequestParam Boolean status,
                                @RequestParam Double amount,
                                @RequestParam String description,
                                @RequestParam String paymentCategoryName,
                                @RequestParam String accountName,
                                Model model) {
        Customer customer = customerService.findByLogin(CustomerController.getCurrentUser().getUsername());
        Account account = accountService.getAccountByNameAndCustomer(accountName, customer);
        PaymentCategory  paymentCategory = paymentCategoryService.getByNameAndCustomer(paymentCategoryName,customer);
        paymentService.updatePayment(id,
                                     LocalDateTime.parse(dateTime),
                                     direction,
                                     status,
                                     amount,
                                     account.getCurrencyName(),
                                     description,
                                     paymentCategory,
                                     account);

        Payment payment = paymentService.getById(id);
        model.addAttribute("updated", true);
        model.addAttribute("id", id);
        model.addAttribute("dateTime", payment.getDateTime());
        model.addAttribute("direction", payment.getDirection());
        model.addAttribute("status", payment.getStatus());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("account", payment.getAccount());
        model.addAttribute("description", payment.getDescription());
        model.addAttribute("paymentCategory", payment.getPaymentCategory());
        model.addAttribute("accounts", accountService.getAccountsByCustomer(customer));
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(customer));

        return "updatePayment";
    }

}
