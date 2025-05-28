package com.tracker.frontend.views;

import com.tracker.frontend.CryptoTableViewApp;
import com.tracker.frontend.util.InactivityTimer;
import com.tracker.frontend.views.graficos.PanelGraficoView;
import com.tracker.frontend.session.Session;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase que representa la vista de detalles de una criptomoneda.
 * Muestra información detallada sobre la criptomoneda seleccionada.
 */
public class CryptoDetailView {

    private final String cryptoId;
    private final String nombreCrypto;
    private final double precioActual;

    /**
     * Constructor de la clase CryptoDetailView.
     * Inicializa los detalles de la criptomoneda.
     *
     * @param cryptoId El ID de la criptomoneda.
     * @param nombreCrypto El nombre de la criptomoneda.
     * @param precioActual El precio actual de la criptomoneda.
     */
    public CryptoDetailView(String cryptoId, String nombreCrypto, double precioActual) {
        this.cryptoId = cryptoId;
        this.nombreCrypto = nombreCrypto;
        this.precioActual = precioActual;
    }

    /**
     * Muestra la vista de detalles de la criptomoneda.
     */
    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Detalles de: " + cryptoId);

        PanelGraficoView panelGraficos = new PanelGraficoView(cryptoId);
        SimuladorOperacionView panelOperacion = new SimuladorOperacionView(cryptoId, nombreCrypto, precioActual);

        Button btnVolver = new Button("← Volver");
        btnVolver.setOnAction(e -> {
            stage.close();
            try {
                new CryptoTableViewApp().mostrarAppPrincipal(new Stage()); // Vovler a la vista principal
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10, btnVolver, panelGraficos, panelOperacion);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: transparent;");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        layout.setStyle("-fx-background-color: transparent;"); 

        scrollPane.setContent(layout);
        scrollPane.getContent().setStyle("-fx-background-color: transparent;");

        AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
        StackPane root = new StackPane(fondo, scrollPane);

        Scene scene = new Scene(root, 700, 900);

        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        InactivityTimer timer = new InactivityTimer(stage, () -> {
            Session.idToken = null;
            stage.close();
            new LoginFormView().mostrar(new Stage());
        });

        timer.attachToScene(scene);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}





