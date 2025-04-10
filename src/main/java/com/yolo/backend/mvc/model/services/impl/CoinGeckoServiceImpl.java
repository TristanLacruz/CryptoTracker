package com.yolo.backend.mvc.model.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;

@Service
public class CoinGeckoServiceImpl {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=50&page=1&sparkline=false";

    public List<CryptoMarketDTO> getMarketData() {
        ResponseEntity<CryptoMarketDTO[]> response = restTemplate.getForEntity(API_URL, CryptoMarketDTO[].class);
        return Arrays.asList(response.getBody());
    }
}
