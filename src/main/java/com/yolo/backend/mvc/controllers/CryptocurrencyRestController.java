package com.yolo.backend.mvc.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.yolo.backend.mvc.model.entity.Cryptocurrency;
import com.yolo.backend.mvc.model.services.ICryptocurrencyService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class CryptocurrencyRestController {
	
	@Autowired
	private ICryptocurrencyService cryptoService;
	

	public CryptocurrencyRestController(ICryptocurrencyService cryptoService) {
        this.cryptoService = cryptoService;
    }
	
	@GetMapping("/cryptos")
	public List<Cryptocurrency> getCryptos(){
		return cryptoService.findAll();
	}
	
	@PostMapping("/cryptos")
	@ResponseStatus(HttpStatus.CREATED)
    public Cryptocurrency createCryptos(@RequestBody Cryptocurrency crypto) {
		cryptoService.save(crypto);
        return crypto;
    }
	

    @GetMapping("/price/{symbol}")
    public double getPrice(@PathVariable String symbol) {
        return cryptoService.getCurrentPrice(symbol.toLowerCase());
    }

    @GetMapping("/info/{symbol}")
    public Map<String, Object> getInfo(@PathVariable String symbol) {
        return cryptoService.getCryptoInfo(symbol.toLowerCase());
    }

}
