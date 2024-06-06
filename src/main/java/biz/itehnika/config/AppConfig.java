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
                        encoder.encode("Pass123!"),
                        CustomerRole.ADMIN, "admin@example.com", "", "");

                paymentCategoryService.addPaymentCategory("DEFAULT", "Default payment category", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("SALARY", "Income earned from work", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("HEALTH", "Medicines, clinics, food additives ...", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("BANK", "Banking operations, payment for banking services", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("BEAUTY", "Beauty salons, cosmetics...", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("CAR", "Spare parts, fuel, repairs", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("CHILDREN", "Schools, kindergartens, entertainment, toys", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("GIFT", "Something given or received as a gift", customerService.findByLogin(ADMIN_LOGIN));
//                paymentCategoryService.addPaymentCategory("FREELANCE", "--------------", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("RESTAURANT", "Restaurants, cafes, bars...", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("ENTERTAINMENT", "Clubs, discos, parties", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("TRAVEL", "Hotels, tours...", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("COMMUNAL PAYMENTS", "Rent and utility costs", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("SERVICES", "Services received and rendered", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("TICKETS", "Plane, train, bus, ship", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("FOOD", "Supermarkets, farmers markets, bakeries", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("EQUIPMENTS", "Specialized tools and equipment", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("TRANSPORT", "Taxi and public transport costs", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("HOUSEHOLD", "Various household appliances, dishes", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("HOBBY", "Everything for body and soul", customerService.findByLogin(ADMIN_LOGIN));
                paymentCategoryService.addPaymentCategory("OTHER", "Other income and expenses", customerService.findByLogin(ADMIN_LOGIN));


                customerService.addCustomer("testUser",
                        encoder.encode("222"),
                        CustomerRole.USER, "user@example.com", "", "");

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
