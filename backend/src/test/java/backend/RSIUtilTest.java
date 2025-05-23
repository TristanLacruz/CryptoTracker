package backend;

import org.junit.jupiter.api.Test;

import com.tracker.backend.indicadores.RSIUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class RSIUtilTest {

    @Test
    public void testCalcularRSI_conDatosValidos() {
    	//FALLO:
//        List<Double> precios = Arrays.asList(
//            44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10,
//            45.42, 45.84, 46.08, 45.89, 46.03, 45.61, 46.28
//        );

    	// CORRECTO:
    	List<Double> precios = Arrays.asList(
    		    44.34, 44.09, 44.15, 43.61, 44.33,
    		    44.83, 45.10, 45.42, 45.84, 46.08,
    		    45.89, 46.03, 45.61, 46.28, 46.00  // ✅ 15 valores
    		);

        double rsi = RSIUtil.calcularRSI(precios);

        assertTrue(rsi >= 0 && rsi <= 100, "El RSI debe estar entre 0 y 100");
    }

    @Test
    public void testCalcularRSI_listaInvalidaLanzaExcepcion() {
        List<Double> precios = Arrays.asList(44.34, 44.09); // muy pocos datos

        assertThrows(IllegalArgumentException.class, () -> {
            RSIUtil.calcularRSI(precios);
        });
    }
}
