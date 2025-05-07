package com.yolo.backend.mvc.model.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IPortafolioDAO;
import com.yolo.backend.mvc.model.dao.ITransaccionDAO;
import com.yolo.backend.mvc.model.dto.EvolucionCompletaDTO;
import com.yolo.backend.mvc.model.dto.RendimientoDiarioDTO;
import com.yolo.backend.mvc.model.dto.ValorDiarioDTO;
import com.yolo.backend.mvc.model.entity.Portafolio;
import com.yolo.backend.mvc.model.entity.Transaccion;
import com.yolo.backend.mvc.model.entity.TransactionType;
import com.yolo.backend.mvc.model.exceptions.PortafolioNoEncontradoException;
import com.yolo.backend.mvc.model.services.IPortafolioService;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;

@Service
public class PortafolioServiceImpl implements IPortafolioService {

	@Autowired
	private IPortafolioDAO PortafolioDAO;
	
	@Autowired
	private ITransaccionDAO transaccionDAO;

	@Autowired
	private ICriptomonedaService cryptoService;
	

	@Autowired
	private IPortafolioDAO portafolioDAO;

	@Override
	public List<Portafolio> findAll() {
		return (List<Portafolio>) PortafolioDAO.findAll();
	}

	@Override
	public void save(Portafolio p) {
		PortafolioDAO.save(p);
	}

	@Override
	public Portafolio findById(String id) {
		return PortafolioDAO.findById(id).orElseThrow(() -> new PortafolioNoEncontradoException(id));
	}

	@Override
	public void delete(Portafolio p) {
		PortafolioDAO.delete(p);
	}

	@Override
	public Portafolio update(Portafolio p, String id) {
		Portafolio portafolioActual = this.findById(id);
		portafolioActual.setUsuarioId(p.getUsuarioId());
		portafolioActual.setCriptomonedas(p.getCriptomonedas());
		return portafolioActual;
	}

	@Override
	public void anadirCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		Portafolio.getCriptomonedas().merge(simbolo, quantity, Double::sum);
		PortafolioDAO.save(Portafolio);
	}

	@Override
	public void eliminarCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

		Map<String, Double> cryptos = Portafolio.getCriptomonedas();
		double currentAmount = cryptos.getOrDefault(simbolo, 0.0);

		if (currentAmount < quantity) {
			throw new RuntimeException("No tienes suficiente cantidad de " + simbolo);
		}

		double newAmount = currentAmount - quantity;

		if (newAmount == 0) {
			cryptos.remove(simbolo);
		} else {
			cryptos.put(simbolo, newAmount);
		}

		PortafolioDAO.save(Portafolio);
	}

	@Override
	public boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		return Portafolio.getCriptomonedas().getOrDefault(simbolo, 0.0) >= quantity;
	}

	@Override
	public Portafolio getPortafolioDeUsuarioId(String usuarioId) {
	    return PortafolioDAO.findByUsuarioId(usuarioId)
	        .orElseGet(() -> {
	            Portafolio nuevo = new Portafolio();
	            nuevo.setId(usuarioId);
	            nuevo.setUsuarioId(usuarioId);
	            nuevo.setSaldo(10000); // inicial opcional
	            nuevo.setCriptomonedas(new HashMap<>());
	            PortafolioDAO.save(nuevo);
	            return nuevo;
	        });
	}

	
	@Override
	public void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidadCrypto) {
	    Portafolio Portafolio = PortafolioDAO.findById(usuarioId)
	            .orElseGet(() -> {
	                Portafolio nuevoPortafolio = new Portafolio();
	                nuevoPortafolio.setId(usuarioId);
	                nuevoPortafolio.setCriptomonedas(null);
	                return nuevoPortafolio;
	            });

	    Map<String, Double> criptomonedas = Portafolio.getCriptomonedas();

	    // Sumar la cantidad nueva a la ya existente (si existe)
	    double cantidadActual = criptomonedas.getOrDefault(cryptoId, 0.0);
	    criptomonedas.put(cryptoId, cantidadActual + cantidadCrypto);

	    Portafolio.setCriptomonedas(criptomonedas);
	    PortafolioDAO.save(Portafolio);
	}
	
	public List<ValorDiarioDTO> calcularEvolucion(String usuarioId) {
	    List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

	    if (transacciones.isEmpty()) return List.of();

	    LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
	    LocalDate hoy = LocalDate.now();
	    long dias = ChronoUnit.DAYS.between(inicio, hoy);

	    Map<String, Double> cantidades = new HashMap<>();
	    List<ValorDiarioDTO> evolucion = new ArrayList<>();

	    for (int i = 0; i <= dias; i++) {
	        LocalDate fecha = inicio.plusDays(i);

	        // Aplicar transacciones de ese día
	        for (Transaccion tx : transacciones) {
	            if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
	                cantidades.putIfAbsent(tx.getCryptoId(), 0.0);
	                double cantidadActual = cantidades.get(tx.getCryptoId());
	                double cantidadNueva = tx.getTipoTransaccion() == TransactionType.COMPRAR
	                        ? cantidadActual + tx.getCantidadCrypto()
	                        : cantidadActual - tx.getCantidadCrypto();
	                cantidades.put(tx.getCryptoId(), cantidadNueva);
	            }
	        }

	        // Calcular valor total del portafolio ese día
	        double valorDia = 0;
	        for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
	            String criptoId = entry.getKey();
	            double cantidad = entry.getValue();

	            if (cantidad <= 0) continue;

	            // Reemplaza por tu servicio de precios históricos
	            double precio = cryptoService.getPrecioEnFecha(criptoId, fecha);
	            valorDia += precio * cantidad;
	        }

	        evolucion.add(new ValorDiarioDTO(i, valorDia));
	    }

	    return evolucion;
	}
	
	public List<RendimientoDiarioDTO> calcularRendimiento(String usuarioId) {
	    List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

	    if (transacciones.isEmpty()) return List.of();

	    LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
	    LocalDate hoy = LocalDate.now();
	    long dias = ChronoUnit.DAYS.between(inicio, hoy);

	    Map<String, Double> cantidades = new HashMap<>();
	    double inversionAcumulada = 0;
	    List<RendimientoDiarioDTO> lista = new ArrayList<>();

	    for (int i = 0; i <= dias; i++) {
	        LocalDate fecha = inicio.plusDays(i);

	        // Aplicar transacciones del día
	        for (Transaccion tx : transacciones) {
	            if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
	                double cantidad = cantidades.getOrDefault(tx.getCryptoId(), 0.0);

	                if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
	                    inversionAcumulada += tx.getValorTotal();
	                    cantidad += tx.getCantidadCrypto();
	                } else {
	                    cantidad -= tx.getCantidadCrypto();
	                }

	                cantidades.put(tx.getCryptoId(), cantidad);
	            }
	        }

	        // Calcular valor actual del portafolio ese día
	        double valorDia = 0;
	        for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
	            double cantidad = entry.getValue();
	            if (cantidad <= 0) continue;

	            double precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
	            valorDia += cantidad * precio;
	        }

	        double ganancia = valorDia - inversionAcumulada;
	        lista.add(new RendimientoDiarioDTO(i, ganancia));
	    }

	    return lista;
	}
	
	@Override
	public List<EvolucionCompletaDTO> calcularEvolucionCompleta(String usuarioId) {
	    List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

	    if (transacciones.isEmpty()) return List.of();

	    LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
	    LocalDate hoy = LocalDate.now();
	    long dias = ChronoUnit.DAYS.between(inicio, hoy);

	    Map<String, Double> cantidades = new HashMap<>();
	    double inversionAcumulada = 0;

	    List<EvolucionCompletaDTO> lista = new ArrayList<>();

	    for (int i = 0; i <= dias; i++) {
	        LocalDate fecha = inicio.plusDays(i);

	        for (Transaccion tx : transacciones) {
	            if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
	                double actual = cantidades.getOrDefault(tx.getCryptoId(), 0.0);
	                if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
	                    inversionAcumulada += tx.getValorTotal();
	                    actual += tx.getCantidadCrypto();
	                } else {
	                    actual -= tx.getCantidadCrypto();
	                }
	                cantidades.put(tx.getCryptoId(), actual);
	            }
	        }

	        double valorDia = 0;
	        for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
	            if (entry.getValue() <= 0) continue;
	            double precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
	            valorDia += entry.getValue() * precio;
	        }

	        double ganancia = valorDia - inversionAcumulada;
	        lista.add(new EvolucionCompletaDTO(i, valorDia, ganancia));
	    }

	    return lista;
	}

	@Override
	public Portafolio findByUsuarioId(String usuarioId) {
	    return portafolioDAO.findByUsuarioId(usuarioId)
	            .orElseThrow(() -> new RuntimeException("Portafolio no encontrado para el usuario: " + usuarioId));
	}


}
