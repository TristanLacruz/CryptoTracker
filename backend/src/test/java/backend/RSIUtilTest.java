package backend;

import org.junit.jupiter.api.Test;
import com.tracker.backend.indicadores.RSIUtil;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

/**
 * Clase de prueba para la utilidad RSIUtil.
 * Contiene pruebas unitarias para el cálculo del RSI (Relative Strength Index).
 */
public class RSIUtilTest {

    @Test
    public void testCalcularRSI_conDatosValidos() {
        // Fallo:
        // List<Double> precios = Arrays.asList(
        // 44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10,
        // 45.42, 45.84, 46.08, 45.89, 46.03, 45.61, 46.28
        // );

        // Correcto:
        List<Double> precios = Arrays.asList(
                44.34, 44.09, 44.15, 43.61, 44.33,
                44.83, 45.10, 45.42, 45.84, 46.08,
                45.89, 46.03, 45.61, 46.28, 46.00);

        double rsi = RSIUtil.calcularRSI(precios);

        assertTrue(rsi >= 0 && rsi <= 100, "El RSI debe estar entre 0 y 100");
    }

    /*
     * Prueba para verificar que el método calcularRSI lanza una excepción
     */
    @Test
    public void testCalcularRSI_listaInvalidaLanzaExcepcion() {
        List<Double> precios = Arrays.asList(44.34, 44.09);

        assertThrows(IllegalArgumentException.class, () -> {
            RSIUtil.calcularRSI(precios);
        });
    }
}
