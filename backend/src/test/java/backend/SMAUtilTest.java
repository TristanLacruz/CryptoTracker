package backend;

import com.tracker.backend.indicadores.SMAUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SMAUtilTest {

	@Test
	public void testSMA_conDatosValidos() {
	    List<Double> precios = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0);
	    List<Double> resultado = SMAUtil.calcularSMA(precios, 3);
	    
	    assertNotNull(resultado);
	    assertFalse(resultado.isEmpty(), "La lista de SMA no debería estar vacía");

	    double ultimoValor = resultado.get(resultado.size() - 1);
	    assertEquals(40.0, ultimoValor, 0.01); // promedio de 30, 40, 50
	}


	@Test
	public void testSMA_listaVacia() {
	    List<Double> precios = Arrays.asList();
	    assertThrows(IllegalArgumentException.class, () -> SMAUtil.calcularSMA(precios, 3));
	}

}
