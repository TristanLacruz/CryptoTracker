// package com.tracker.backend.mvc.model.services.impl;

// import java.util.Arrays;
// import java.util.List;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
// import com.tracker.common.dto.CryptoMarketDTO;

// @Service
// public class CoinGeckoServiceImpl {

//     private final RestTemplate restTemplate = new RestTemplate();
//     private final String API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=50&page=1&sparkline=false";

//     public List<CryptoMarketDTO> getMarketData() {
//     List<String> ids = List.of("bitcoin", "ethereum", "binancecoin", "cardano", "ripple", "tether", "dogecoin", "polkadot", "solana", "usd-coin");
//     String idsParam = String.join(",", ids);

//     String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + idsParam + "&vs_currencies=eur";

//     try {
//         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

//         if (!response.getStatusCode().is2xxSuccessful()) {
//             System.err.println("CoinGecko devolvió: " + response.getStatusCode());
//             return Collections.emptyList();
//         }

//         JSONObject json = new JSONObject(response.getBody());
//         List<CryptoMarketDTO> result = new ArrayList<>();

//         for (String id : ids) {
//             if (json.has(id)) {
//                 double precio = json.getJSONObject(id).getDouble("eur");

//                 CryptoMarketDTO dto = new CryptoMarketDTO();
//                 dto.setId(id);
//                 dto.setSymbol(id);
//                 dto.setCurrent_price(precio);

//                 result.add(dto);
//             }
//         }

//         return result;

//     } catch (Exception e) {
//         System.err.println("⚠️ Error al obtener market data: " + e.getMessage());
//         return Collections.emptyList();
//     }
// }

// }
