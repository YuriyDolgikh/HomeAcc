package biz.itehnika.dto;

import biz.itehnika.model.Currency;
import biz.itehnika.model.CurrencyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CurrencyDTO {

    private String ccy;
    private String base_ccy;
    private Double buy;
    private Double sale;

    public CurrencyDTO(String ccy, String base_ccy, Double buy, Double sale) {
        this.ccy = ccy;
        this.base_ccy = base_ccy;
        this.buy = buy;
        this.sale = sale;
    }

    public static Currency fromDTO(CurrencyDTO currencyDTO){
        return new Currency(CurrencyName.valueOf(currencyDTO.getCcy()),
                            CurrencyName.valueOf(currencyDTO.getBase_ccy()),
                            currencyDTO.getBuy(),
                            currencyDTO.getSale(),
                            LocalDate.now()
                           );
    }
}
