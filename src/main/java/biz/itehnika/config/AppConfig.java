package biz.itehnika.config;

import biz.itehnika.model.enums.CustomerRole;
import biz.itehnika.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableMethodSecurity
public class AppConfig {

    public static final String ADMIN_LOGIN = "admin";

    @Bean
    public CommandLineRunner demo(final CustomerService customerService,
                                  final PaymentCategoryService paymentCategoryService,
                                  final CurrencyService currencyService,
                                  final PaymentService paymentService,
                                  final AccountService accountService,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) {
                customerService.addCustomer(ADMIN_LOGIN,
                        encoder.encode("111"),
                        CustomerRole.ADMIN, "admin@it.com", "", "");

                paymentCategoryService.addPaymentCategory("DEFAULT", "Default payment category", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("SALARY", "Income earned from work", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("HEALTH", "Medicines, clinics, food additives ...", customerService.findByLogin(ADMIN_LOGIN));


                customerService.addCustomer("testUser",
                        encoder.encode("222"),
                        CustomerRole.USER, "user@it.com", "", "");

//                currencyService.addTodayRatesIntoDB();  // TODO - Set rule to actualise exchange rates ( e.g.: every customer login)
//                PaymentCategory category = paymentCategoryService.getByNameAndCustomer("DEFAULT", customerService.findByLogin("testUser"));
//                accountService.addAccount("Test ACC", "Просто для теста", AccountType.CASH, CurrencyName.EUR, customerService.findByLogin("testUser"));
//                Account account = accountService.getAccountByNameAndCustomer("Test ACC", customerService.findByLogin("testUser"));
//                paymentService.addPayment(LocalDateTime.now(), false, true, 123.45, CurrencyName.EUR,
//                        "Test transaction", category, account, customerService.findByLogin("testUser"));
//                paymentService.getAllPaymentsByCustomer(customerService.findByLogin("testUser"));

            }
        };
    }
}
