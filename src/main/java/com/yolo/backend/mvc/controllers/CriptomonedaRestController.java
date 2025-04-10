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

import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;
import com.yolo.backend.mvc.model.entity.Criptomoneda;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class CriptomonedaRestController {
	
	@Autowired
	private ICriptomonedaService cryptoService;
	

	public CriptomonedaRestController(ICriptomonedaService cryptoService) {
        this.cryptoService = cryptoService;
    }
	
	@GetMapping("/cryptos")
	public List<Criptomoneda> getCryptos(){
		return cryptoService.findAll();
	}
	
	@PostMapping("/cryptos")
	@ResponseStatus(HttpStatus.CREATED)
    public Criptomoneda createCryptos(@RequestBody Criptomoneda crypto) {
		cryptoService.save(crypto);
        return crypto;
    }
	

    @GetMapping("/precio/{simbolo}")
    public double getPrice(@PathVariable String simbolo) {
        return cryptoService.getPrecioActual(simbolo.toLowerCase());
    }

    @GetMapping("/info/{simbolo}")
    public Map<String, Object> getInfo(@PathVariable String simbolo) {
        return cryptoService.getCryptoInfo(simbolo.toLowerCase());
    }

    @GetMapping("/cryptos/market")
    public List<CryptoMarketDTO> getMarketCryptos() {
        return cryptoService.getMarketData();
    }

}
