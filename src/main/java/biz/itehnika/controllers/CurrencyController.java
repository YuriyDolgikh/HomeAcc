package biz.itehnika.controllers;

import biz.itehnika.services.CurrencyService;
import org.springframework.stereotype.Controller;

@Controller
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }


}
