package biz.itehnika.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Data
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CurrencyName name;
    @Enumerated(EnumType.STRING)
    private CurrencyName nameBase;
    private Double buyRate;
    private Double saleRate;
    private LocalDate dateRate;

    public Currency(CurrencyName name, CurrencyName nameBase, Double buyRate, Double saleRate, LocalDate dateRate) {
        this.name = name;
        this.nameBase = nameBase;
        this.buyRate = buyRate;
        this.saleRate = saleRate;
        this.dateRate = dateRate;
    }

}
