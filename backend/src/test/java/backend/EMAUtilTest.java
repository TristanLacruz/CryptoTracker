package backend;

import com.tracker.backend.indicadores.EMAUtil;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la utilidad EMAUtil.
 * Contiene pruebas unitarias para el cálculo del EMA (Exponential Moving Average).
 */
public class EMAUtilTest {

    /**
     * Prueba para verificar que el método calcularEMA maneja correctamente una lista vacía.
     */
    @Test
    public void testEMA_conDatosValidos() {
        List<Double> precios = Arrays.asList(10.0, 11.0, 12.0, 13.0, 14.0);
        List<Double> resultado = EMAUtil.calcularEMA(precios, 3);
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty(), "La lista EMA no debería estar vacía");
        double ultimoValor = resultado.get(resultado.size() - 1);
        assertTrue(ultimoValor > 0, "El último valor del EMA debería ser positivo");
    }

    /*
     * Prueba para verificar que el método calcularEMA lanza una excepción
     * cuando se proporciona una lista vacía.
     */
    @Test
    public void testEMA_periodoInvalido() {
        List<Double> precios = Arrays.asList(10.0, 11.0);
        assertThrows(IllegalArgumentException.class, () -> EMAUtil.calcularEMA(precios, 3));
    }
}
