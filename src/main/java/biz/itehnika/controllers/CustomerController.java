package biz.itehnika.controllers;

import biz.itehnika.config.AppConfig;
import biz.itehnika.model.Customer;
import biz.itehnika.model.enums.CustomerRole;
import biz.itehnika.services.AccountService;
import biz.itehnika.services.CurrencyService;
import biz.itehnika.services.CustomerService;
import biz.itehnika.services.PaymentCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final PaymentCategoryService paymentCategoryService;
    private final AccountService accountService;
    private final CurrencyService currencyService;

    public CustomerController(CustomerService customerService, PasswordEncoder passwordEncoder, PaymentCategoryService paymentCategoryService, AccountService accountService, CurrencyService currencyService) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.paymentCategoryService = paymentCategoryService;
        this.accountService = accountService;
        this.currencyService = currencyService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        User user = getCurrentUser();
        String login = user.getUsername();
        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("admin", isAdmin(user));

        return "home";
    }


    @GetMapping("/settings")
    public String updateCustomer(Model model) {
        User user = getCurrentUser();

        String login = user.getUsername();
        Customer dbUser = customerService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("roles", dbUser.getRole());
        model.addAttribute("admin", isAdmin(user));
        model.addAttribute("email", dbUser.getEmail());
        model.addAttribute("phone", dbUser.getPhone());
        model.addAttribute("address", dbUser.getAddress());
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(dbUser));
        model.addAttribute("accounts", accountService.getAccountsByCustomer(dbUser));

        return "settings";
    }

    @PostMapping(value = "/settings")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')") // SpEL !!!
    public String updateCustomer(@RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String address,
                         Model model) {
        User user = getCurrentUser();

        String login = user.getUsername();
        Customer dbUser = customerService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("roles", customerService.findByLogin(login).getRole());
        model.addAttribute("admin", isAdmin(user));
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(dbUser));
        model.addAttribute("accounts", accountService.getAccountsByCustomer(dbUser));

        if ( ! customerService.updateCustomer(login, email, phone, address)) {
            model.addAttribute("exists", true);
        }else {
            model.addAttribute("updated", true);
        }
        model.addAttribute("email", dbUser.getEmail());
        return "settings";
    }

    @GetMapping(value = "/update/{login}")     // TODO - update any users from admin page
    @PreAuthorize("hasRole('ROLE_ADMIN')") // SpEL !!!
    public String updateForAdmin (@PathVariable(value = "login") String login, Model model) {
        Customer customer = customerService.findByLogin(login);
        model.addAttribute("login", login);
        model.addAttribute("email", customer.getEmail());
        model.addAttribute("phone", customer.getPhone());
        model.addAttribute("address", customer.getAddress());

        return "updateForAdmin";
    }

    @PostMapping(value = "/updateForAdmin")     // TODO - update any users from admin page
    @PreAuthorize("hasRole('ROLE_ADMIN')") // SpEL !!!
    public String updateForAdmin(@RequestParam String login,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String address,
                                 Model model) {

        Customer customer = customerService.findByLogin(login);
        if (customer != null){

            if ( ! customerService.updateCustomer(login, email, phone, address)) {
                model.addAttribute("exists", true);
                model.addAttribute("login", login);
                model.addAttribute("email", customer.getEmail());
                model.addAttribute("phone", customer.getPhone());
                model.addAttribute("address", customer.getAddress());
            }else {
                model.addAttribute("updated", true);
                model.addAttribute("login", login);
                model.addAttribute("email", email);
                model.addAttribute("phone", phone);
                model.addAttribute("address", address);
            }

        }
        return "updateForAdmin";
    }

    @PostMapping(value = "/register")
    public String newCustomer(@RequestParam String login,
                          @RequestParam String password,
                          @RequestParam String passwordConfirm,
                          @RequestParam String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String address,
                          Model model) {
        if (!password.equals(passwordConfirm)){
            model.addAttribute("difPass", true);
            model.addAttribute("login", login);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            return "register";
        }

        String passHash = passwordEncoder.encode(password);

        if ( ! customerService.addCustomer(login, passHash, CustomerRole.USER, email, phone, address)) {
            model.addAttribute("exists", true);
            model.addAttribute("login", login);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            return "register";
        }
        model.addAttribute("registered", true);
        model.addAttribute("login", login);

        try {
            User user = getCurrentUser();
            if (user != null && isAdmin(user)){     // TODO - try-catch is strange here / need to review
                return "redirect:/admin";
            }
            return "login";
        }catch (Exception e){
            return "login";
        }
    }

    @PostMapping(value = "/delete")     // TODO - ADMIN role required
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCustomers(@RequestParam(name = "toDelete", required = false) List<Long> ids, Model model) {
        if (ids != null && !ids.isEmpty()) {
            customerService.deleteCustomers(ids);
        }
        model.addAttribute("customers", customerService.getAllCustomers());

        return "redirect:/admin";
    }

    @GetMapping("/login")
    public String loginPage() {
        currencyService.addTodayRatesIntoDB();  // TODO - Set rule to actualise exchange rates ( e.g.: every customer login || every one hour)
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        try {
            User user = getCurrentUser();
            if (user != null && isAdmin(user)){
                model.addAttribute("admin", true);
            }
            return "register";
        }catch (Exception e){
            return "register";
        }
    }

    @GetMapping("/admin")   // TODO - it must send all the data needed to edit into admin page
    @PreAuthorize("hasRole('ROLE_ADMIN')") // SpEL !!!
    public String admin(Model model) {
        Customer customerAdmin = customerService.findByLogin(AppConfig.ADMIN_LOGIN);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("paymentCategories", paymentCategoryService.getPaymentCategoriesByCustomer(customerAdmin));
        return "admin";
    }


    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        User user = getCurrentUser();
        model.addAttribute("login", user.getUsername());
        return "unauthorized";
    }

    // ----

    static User getCurrentUser() {
        return (User)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    static boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority()))
                return true;
        }
        return false;
    }

}
