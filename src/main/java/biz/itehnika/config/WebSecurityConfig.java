package biz.itehnika.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers( "/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers( "/settings",
                                          "/home",
                                          "/addNewCategory",
                                          "/addNewPayment",
                                          "/accounting",
                                          "/addNewAccount").hasAnyRole("USER", "ADMIN")

                        .requestMatchers( "/register", "/images/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exept) -> exept
                        .accessDeniedPage("/unauthorized")
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                                .loginProcessingUrl("/j_spring_security_check")
                                .failureUrl("/login?error")
                                .usernameParameter("j_login")
                                .passwordParameter("j_password")
                        .successForwardUrl("/home")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
                        .logoutSuccessUrl("/Login")
                        .permitAll());

        return http.build();
    }

}