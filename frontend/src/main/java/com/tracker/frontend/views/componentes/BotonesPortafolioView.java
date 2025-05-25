// package com.tracker.frontend.views.componentes;

// import javafx.scene.control.Button;
// import javafx.scene.layout.HBox;

// import java.util.function.Consumer;

// /**
//  * Clase que representa una vista de botones para el portafolio.
//  * Contiene botones para ver la evolución, rendimiento acumulado y gráfico combinado del portafolio.
//  */
// public class BotonesPortafolioView extends HBox {

//     /**
//      * Constructor de la clase BotonesPortafolioView.
//      *
//      * @param onEvolucion  Acción a realizar al hacer clic en el botón de evolución.
//      * @param onRendimiento Acción a realizar al hacer clic en el botón de rendimiento acumulado.
//      * @param onCombinada  Acción a realizar al hacer clic en el botón de gráfico combinado.
//      */
//     public BotonesPortafolioView(Consumer<Void> onEvolucion, Consumer<Void> onRendimiento, Consumer<Void> onCombinada) {
//         Button graficaBtn = new Button("Ver evolución del portafolio");
//         graficaBtn.setOnAction(e -> onEvolucion.accept(null));

//         Button btnRendimiento = new Button("Ver rendimiento acumulado");
//         btnRendimiento.setOnAction(e -> onRendimiento.accept(null));

//         Button btnCombinada = new Button("Gráfico combinado");
//         btnCombinada.setOnAction(e -> onCombinada.accept(null));

//         this.setSpacing(10);
//         this.getChildren().addAll(graficaBtn, btnRendimiento, btnCombinada);
//     }
// }
