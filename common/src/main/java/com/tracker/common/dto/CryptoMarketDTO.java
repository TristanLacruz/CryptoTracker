package com.tracker.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CryptoMarketDTO {
    private String id;
    private String symbol;
    private String name;
    private String image;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;

    @JsonProperty("market_cap")
    private double marketCap;

    @JsonProperty("total_volume")
    private double totalVolume;

    // Getters y setters (mantén los nombres en camelCase)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol.toUpperCase(); }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // Getters y setters deben coincidir exactamente con las anotaciones
    public double getCurrentPrice() {
        return currentPrice;
    }

    @JsonProperty("current_price")
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    @JsonProperty("price_change_percentage_24h")
    public void setPriceChangePercentage24h(double priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    public double getMarketCap() {
        return marketCap;
    }

    @JsonProperty("market_cap")
    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    @JsonProperty("total_volume")
    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }
    
       

}