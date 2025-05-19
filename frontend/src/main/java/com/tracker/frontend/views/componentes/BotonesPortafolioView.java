package com.tracker.frontend.views.componentes;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class BotonesPortafolioView extends HBox {

    public BotonesPortafolioView(Consumer<Void> onEvolucion, Consumer<Void> onRendimiento, Consumer<Void> onCombinada) {
        Button graficaBtn = new Button("📈 Ver evolución del portafolio");
        graficaBtn.setOnAction(e -> onEvolucion.accept(null));

        Button btnRendimiento = new Button("💹 Ver rendimiento acumulado");
        btnRendimiento.setOnAction(e -> onRendimiento.accept(null));

        Button btnCombinada = new Button("📊 Gráfico combinado");
        btnCombinada.setOnAction(e -> onCombinada.accept(null));

        this.setSpacing(10);
        this.getChildren().addAll(graficaBtn, btnRendimiento, btnCombinada);
    }
}
