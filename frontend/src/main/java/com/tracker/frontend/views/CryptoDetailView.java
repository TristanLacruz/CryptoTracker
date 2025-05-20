package com.tracker.frontend.views;

import com.tracker.frontend.CryptoTableViewApp;
import com.tracker.frontend.util.InactivityTimer;
import com.tracker.frontend.views.graficos.PanelGraficoView;
import com.tracker.frontend.session.Session;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CryptoDetailView {

	private final String cryptoId;
	private final String nombreCrypto;
	private final double precioActual;

	public CryptoDetailView(String cryptoId, String nombreCrypto, double precioActual) {
		this.cryptoId = cryptoId;
		this.nombreCrypto = nombreCrypto;
		this.precioActual = precioActual;
	}

	public void mostrar() {
		Stage stage = new Stage();
		stage.setTitle("Detalles de: " + cryptoId);

		// Crear componentes principales
		PanelGraficoView panelGraficos = new PanelGraficoView(cryptoId);
		SimuladorOperacionView panelOperacion = new SimuladorOperacionView(cryptoId, nombreCrypto, precioActual);

		// Botón para volver
		Button btnVolver = new Button("← Volver");
		btnVolver.setOnAction(e -> {
			stage.close();
			try {
				new CryptoTableViewApp().mostrarAppPrincipal(new Stage()); // 👈 Vuelve a la vista principal
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		VBox layout = new VBox(10, btnVolver, panelGraficos, panelOperacion);
		layout.setPadding(new Insets(10));
		layout.setStyle("-fx-background-color: transparent;");

		AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
		StackPane root = new StackPane(fondo, layout);

		Scene scene = new Scene(root, 700, 900);


		// ⏱ Inactividad: cerrar sesión y volver al login
		InactivityTimer timer = new InactivityTimer(stage, () -> {
		    Session.idToken = null;
		    stage.close();
		    new LoginFormView().mostrar(new Stage());
		});

		timer.attachToScene(scene); // <- activa el control

		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();

	}

}
