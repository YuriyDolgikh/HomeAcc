package biz.itehnika.config;

import biz.itehnika.model.CustomerRole;
import biz.itehnika.services.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration {

    public static final String ADMIN_LOGIN = "admin";

    @Bean
    public CommandLineRunner demo(final CustomerService customerService,
                                  final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                customerService.addCustomer(ADMIN_LOGIN,
                        encoder.encode("111"),
                        CustomerRole.ADMIN, "admin@it.com", "", "");
                customerService.addCustomer("testUser",
                        encoder.encode("222"),
                        CustomerRole.USER, "user@it.com", "", "");
            }
        };
    }
}
