package biz.itehnika.repos;

import biz.itehnika.model.Currency;
import biz.itehnika.model.CurrencyName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long>  {

    Currency findCurrencyByNameAndDateRate(CurrencyName currencyName, LocalDate dateRate);
    List<Currency> findCurrenciesByDateRate(LocalDate dateRate);

}
