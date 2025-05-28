package com.tracker.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase DTO para representar información del mercado de criptomonedas.
 * Contiene detalles como el ID, símbolo, nombre, imagen, precio actual,
 * cambio de precio en 24 horas, capitalización de mercado y volumen total.
 */
public class CryptoMarketDTO {
	private String id;
	private String symbol;
	private String name;
	private String image;
	private boolean favorito;

	@JsonProperty("current_price")
	private double currentPrice;

	@JsonProperty("price_change_percentage_24h")
	private double priceChangePercentage24h;

	@JsonProperty("market_cap")
	private double marketCap;

	@JsonProperty("total_volume")
	private double totalVolume;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol.toUpperCase();
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

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

	public boolean isFavorito() {
		return favorito;
	}

	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}
}