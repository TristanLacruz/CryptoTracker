package com.tracker.frontend.views.portafolio;

import com.tracker.frontend.views.graficos.GraficoCombinadoView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Clase que representa una vista de botones para mostrar gráficos relacionados con el portafolio.
 * Contiene botones para ver la evolución del portafolio, rendimiento acumulado y gráfico combinado.
 */
public class BotonesGraficosView extends HBox {

    public BotonesGraficosView(String usuarioId) {
        Button graficaBtn = new Button("Ver evolución del portafolio");
        graficaBtn.setOnAction(e -> new GraficoEvolucionView(usuarioId).mostrar());

        Button rendimientoBtn = new Button("Ver rendimiento acumulado");
        rendimientoBtn.setOnAction(e -> new GraficoRendimientoView(usuarioId).mostrar());

        Button combinadaBtn = new Button("Gráfico combinado");
        combinadaBtn.setOnAction(e -> new GraficoCombinadoView(usuarioId).mostrar());

        this.setSpacing(10);
        this.getChildren().addAll(graficaBtn, rendimientoBtn, combinadaBtn);
    }
}
