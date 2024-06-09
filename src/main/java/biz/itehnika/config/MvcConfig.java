package biz.itehnika.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/settings").setViewName("settings");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/addNewCategory").setViewName("addNewCategory");
        registry.addViewController("/addNewAccount").setViewName("addNewAccount");
        registry.addViewController("/updateCategory").setViewName("updateCategory");
        registry.addViewController("/updateAccount").setViewName("updateAccount");
        registry.addViewController("/updatePayment").setViewName("updatePayment");
        registry.addViewController("/accounting").setViewName("accounting");
        registry.addViewController("/setPeriod").setViewName("accounting");
        registry.addViewController("/addNewPayment").setViewName("addNewPayment");
        registry.addViewController("/currencyExchange").setViewName("currencyExchange");
        registry.addViewController("/transfer").setViewName("transfer");
    }

}