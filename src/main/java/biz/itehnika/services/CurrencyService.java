package biz.itehnika.services;

import biz.itehnika.dto.CurrencyDTO;
import biz.itehnika.model.Currency;
import biz.itehnika.model.enums.CurrencyName;
import biz.itehnika.repos.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
public class CurrencyService {


    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public Boolean isRatesExistByDate(LocalDate localDate){
        return currencyRepository.existsByDateRate(localDate);
    }

    @Transactional
    public void addTodayRatesIntoDB(){  //TODO - make this every time when customer do login
        ZoneId zoneId = ZoneId.of("Europe/Kyiv");
        LocalDate localDate = LocalDate.now(zoneId);
        List<Currency> listTodayRates = currencyRepository.findCurrenciesByDateRate(localDate);
        if (!listTodayRates.isEmpty()){
            return;
        }
        Currency currency;
        for (CurrencyDTO currencyDTO : getTodayRatesFromBank()){
            currency = CurrencyDTO.fromDTO(currencyDTO);
            currencyRepository.save(currency);
        }
    }

    @Transactional
    public Currency getCurrencyByNameAndDate(CurrencyName currencyName, LocalDate localDate){
        return currencyRepository.findCurrencyByNameAndDateRate(currencyName, localDate);
    }

    @Transactional
    public Currency getCurrencyByNameToday(CurrencyName currencyName){
        ZoneId zoneId = ZoneId.of(System.getProperty("user.timezone"));
        return currencyRepository.findCurrencyByNameAndDateRate(currencyName, LocalDate.now(zoneId));
    }


    public static List<CurrencyDTO> getTodayRatesFromBank(){
        final RestTemplate restTemplate = new RestTemplate();
        CurrencyDTO[] ratesArray = restTemplate.getForObject("https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11", CurrencyDTO[].class);
        assert ratesArray != null;  // check API PrivatBank is OK
        return Arrays.asList(ratesArray);
    }
}
