package backend;

import com.tracker.backend.indicadores.MACDUtil;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la utilidad MACDUtil.
 * Contiene pruebas unitarias para el cálculo del MACD (Moving Average
 * Convergence Divergence).
 */
public class MACDUtilTest {

	/**
	 * Prueba para verificar que el método calcularMACD maneja correctamente una
	 * lista de precios válida.
	 */
	@Test
	public void testMACD_conDatosValidos() {
		List<Double> precios = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			precios.add(100.0 + i);
		}

		Map<String, List<Double>> resultado = MACDUtil.calcularMACD(precios);
		assertNotNull(resultado);

		List<Double> macd = resultado.get("macd");
		List<Double> signal = resultado.get("signal");

		assertNotNull(macd);
		assertNotNull(signal);
		assertFalse(macd.isEmpty(), "La lista MACD no debería estar vacía");
		assertFalse(signal.isEmpty(), "La lista de señal no debería estar vacía");

		assertTrue(signal.size() <= macd.size(), "La señal debe tener menos o igual tamaño que MACD");

		double ultimoMacd = macd.get(macd.size() - 1);
		double ultimaSignal = signal.get(signal.size() - 1);
		assertTrue(Double.isFinite(ultimoMacd));
		assertTrue(Double.isFinite(ultimaSignal));
	}

	/*
	 * Prueba para verificar que el método calcularMACD lanza una excepción
	 */
	@Test
	public void testMACD_datosInsuficientes() {
		List<Double> precios = Arrays.asList(10.0, 11.0);
		assertThrows(IllegalArgumentException.class, () -> MACDUtil.calcularMACD(precios));
	}
}
