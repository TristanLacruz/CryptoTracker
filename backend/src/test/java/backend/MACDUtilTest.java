package backend;

import com.tracker.backend.indicadores.MACDUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MACDUtilTest {

	@Test
	public void testMACD_conDatosValidos() {
	    List<Double> precios = new ArrayList<>();
	    for (int i = 0; i < 50; i++) {
	        precios.add(100.0 + i); // 50 precios crecientes
	    }

	    Map<String, List<Double>> resultado = MACDUtil.calcularMACD(precios);
	    assertNotNull(resultado);

	    List<Double> macd = resultado.get("macd");
	    List<Double> signal = resultado.get("signal");

	    assertNotNull(macd);
	    assertNotNull(signal);
	    assertFalse(macd.isEmpty(), "La lista MACD no debería estar vacía");
	    assertFalse(signal.isEmpty(), "La lista de señal no debería estar vacía");

	    // Nueva verificación lógica
	    assertTrue(signal.size() <= macd.size(), "La señal debe tener menos o igual tamaño que MACD");

	    double ultimoMacd = macd.get(macd.size() - 1);
	    double ultimaSignal = signal.get(signal.size() - 1);
	    assertTrue(Double.isFinite(ultimoMacd));
	    assertTrue(Double.isFinite(ultimaSignal));
	}



    @Test
    public void testMACD_datosInsuficientes() {
        List<Double> precios = Arrays.asList(10.0, 11.0);
        assertThrows(IllegalArgumentException.class, () -> MACDUtil.calcularMACD(precios));
    }
}
