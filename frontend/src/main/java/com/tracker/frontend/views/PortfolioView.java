package com.tracker.frontend.views;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.common.dto.CriptoPosesionDTO;
import com.tracker.frontend.CryptoTableViewApp;
import com.tracker.frontend.session.Session;
import com.tracker.frontend.util.InactivityTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Clase que representa la vista del portafolio de un usuario.
 * Muestra el saldo disponible, balance total, activos y un gráfico de evolución del portafolio.
 */
public class PortfolioView {

    private final String usuarioId;

    public PortfolioView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Muestra la vista del portafolio.
     * Crea una ventana con el saldo disponible, balance total, activos y un gráfico de evolución.
     */
    public void mostrar() {
        Circle puntoSaldo = new Circle(6, Color.LIME);
        Label textoSaldo = new Label("Saldo disponible (€)");
        textoSaldo.setTextFill(Color.WHITE);
        HBox leyendaSaldo = new HBox(5, puntoSaldo, textoSaldo);
        leyendaSaldo.setAlignment(Pos.CENTER_LEFT);

        Circle puntoBalance = new Circle(6, Color.MAGENTA);
        Label textoBalance = new Label("Balance total (€)");
        textoBalance.setTextFill(Color.WHITE);
        HBox leyendaBalance = new HBox(5, puntoBalance, textoBalance);
        leyendaBalance.setAlignment(Pos.CENTER_LEFT);

        VBox leyendaPersonalizada = new VBox(leyendaSaldo, leyendaBalance);
        leyendaPersonalizada.setSpacing(5);
        leyendaPersonalizada.setPadding(new Insets(5, 0, 0, 10));

        Label saldoLabel = new Label("💶 Saldo disponible: €0.00");
        Label balanceLabel = new Label("📊 Balance total: €0.00");

        Label nombreUsuarioLabel = new Label("Cargando nombre del usuario...");
        nombreUsuarioLabel.getStyleClass().add("label");

        Stage stage = new Stage();
        stage.setTitle("Mi Portafolio");

        Label tituloActivos = new Label("Mis activos:");
        tituloActivos.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: lime;");
        HBox contenedorTituloActivos = new HBox(tituloActivos);
        contenedorTituloActivos.setAlignment(Pos.CENTER);
        contenedorTituloActivos.setPadding(new Insets(5));
        contenedorTituloActivos.setStyle("-fx-background-color: #111111; -fx-background-radius: 10;");

        TableView<CriptoPosesionDTO> tableView = new TableView<>();
        ObservableList<CriptoPosesionDTO> data = FXCollections.observableArrayList();

        Label totalLabel = new Label("Total del portafolio: €0.00");
        Button btnGenerarReporte = new Button("Generar Reporte");
        btnGenerarReporte.setOnAction(e -> generarReporteExcel());

        Button btnVolver = new Button("Volver");
        btnVolver.setOnAction(e -> {
            new CryptoTableViewApp().mostrarAppPrincipal(new Stage());
            stage.close(); 
        });

        TableColumn<CriptoPosesionDTO, String> simboloCol = new TableColumn<>("Criptomoneda");
        simboloCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("simbolo"));
        simboloCol.setMaxWidth(1f * Integer.MAX_VALUE * 33); 

        TableColumn<CriptoPosesionDTO, Double> cantidadCol = new TableColumn<>("Cantidad");
        cantidadCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantidad"));
        cantidadCol.setMaxWidth(1f * Integer.MAX_VALUE * 33); 
        cantidadCol.setCellFactory(tc -> new TableCell<CriptoPosesionDTO, Double>() {
            @Override
            protected void updateItem(Double cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                setText(empty || cantidad == null ? null : String.format("%,.6f", cantidad));
            }
        });

        TableColumn<CriptoPosesionDTO, Double> valorCol = new TableColumn<>("Valor Total (€)");
        valorCol.setMaxWidth(1f * Integer.MAX_VALUE * 34);
        valorCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("valorTotal"));
        valorCol.setCellFactory(tc -> new TableCell<CriptoPosesionDTO, Double>() {
            @Override
            protected void updateItem(Double cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                setText(empty || cantidad == null ? null : String.format("%,.6f", cantidad));
            }
        });

        tableView.getColumns().addAll(simboloCol, cantidadCol, valorCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(data);

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Días");
        yAxis.setLabel("Capital (€)");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Evolución del saldo");
        lineChart.setLegendVisible(true);
        lineChart.setLegendSide(Side.BOTTOM);
        System.out.println("¿Leyenda visible?: " + lineChart.isLegendVisible());

        XYChart.Series<Number, Number> saldoSeries = new XYChart.Series<>();

        XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<>();
        balanceSeries.setName("Balance total");

        saldoSeries.setName("Saldo disponible (€)");
        balanceSeries.setName("Balance total (€)");
        lineChart.getData().addAll(saldoSeries, balanceSeries);

        PauseTransition delay = new PauseTransition(Duration.millis(500));
        delay.setOnFinished(event -> {
            lineChart.applyCss();
            lineChart.layout();

            Node legend = lineChart.lookup(".chart-legend");
            if (legend != null && legend instanceof VBox) {
                VBox legendBox = (VBox) legend;
                for (Node child : legendBox.getChildren()) {
                    if (child instanceof Label) {
                        Label label = (Label) child;

                        String text = label.getText();
                        Color color;

                        if (text.contains("Saldo")) {
                            color = Color.LIME;
                        } else if (text.contains("Balance")) {
                            color = Color.MAGENTA;
                        } else {
                            continue;
                        }

                        Circle dot = new Circle(6, color);
                        Label textLabel = new Label(text);
                        textLabel.setTextFill(Color.WHITE);
                        HBox hbox = new HBox(5, dot, textLabel);
                        hbox.setAlignment(Pos.CENTER_LEFT);

                        int index = legendBox.getChildren().indexOf(label);
                        legendBox.getChildren().set(index, hbox);
                    }
                }
            }
        });
        delay.play();

        Node saldoLine = saldoSeries.getNode().lookup(".chart-series-line");
        if (saldoLine != null) {
            saldoLine.setStyle("-fx-stroke: #00FF00; -fx-stroke-width: 2px;"); 
        }

        Node balanceLine = balanceSeries.getNode().lookup(".chart-series-line");
        if (balanceLine != null) {
            balanceLine.setStyle("-fx-stroke: #FF00FF; -fx-stroke-width: 2px;"); 
        }

        VBox contenedorTabla = new VBox(10, contenedorTituloActivos, tableView);
        contenedorTabla.setAlignment(Pos.TOP_CENTER);
        contenedorTabla.setPrefWidth(400);
        contenedorTabla.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contenedorTabla, Priority.ALWAYS); 

        contenedorTabla.setPrefWidth(400);
        contenedorTabla.setAlignment(Pos.CENTER_LEFT);

        Label tituloResumen = new Label("Resumen:");
        tituloResumen.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: lime;");
        HBox contenedorTituloResumen = new HBox(tituloResumen);
        contenedorTituloResumen.setAlignment(Pos.CENTER);
        contenedorTituloResumen.setPadding(new Insets(5));
        contenedorTituloResumen.setStyle("-fx-background-color: #111111; -fx-background-radius: 10;");

        VBox infoContent = new VBox(10,
                nombreUsuarioLabel,
                saldoLabel,
                balanceLabel,
                totalLabel,
                btnGenerarReporte,
                btnVolver);
        infoContent.setAlignment(Pos.TOP_LEFT);
        infoContent.setPadding(new Insets(15));
        infoContent.setStyle(
                "-fx-background-color: #111111; -fx-background-radius: 10; -fx-border-color: #00FF00; -fx-border-width: 2; -fx-border-radius: 10;");

        VBox infoBox = new VBox(10, contenedorTituloResumen, infoContent);
        infoBox.setAlignment(Pos.TOP_CENTER);
        infoBox.setMaxWidth(Double.MAX_VALUE);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        tableView.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tableView, Priority.ALWAYS);

        HBox contenidoInferior = new HBox(20, contenedorTabla, infoBox);
        contenidoInferior.setPadding(new Insets(10));
        contenidoInferior.setAlignment(Pos.CENTER);

        VBox rootContent = new VBox(10, lineChart, leyendaPersonalizada, contenidoInferior);
        rootContent.setPadding(new Insets(10));
        rootContent.setStyle("-fx-background-color: transparent;");

        AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
        StackPane root = new StackPane(fondo, rootContent);

        Scene scene = new Scene(root, 800, 600);
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

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                String urlNombre = "http://localhost:8080/api/usuarios/" + usuarioId + "/nombre";
                HttpRequest nombreRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlNombre))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();

                HttpResponse<String> nombreResponse = client.send(nombreRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode jsonNombre = mapper.readTree(nombreResponse.body());
                System.out.println("🔎 Respuesta de /usuarios/{id}/nombre: " + nombreResponse.body());

                String nombre = null;
                if (jsonNombre.has("nombre")) {
                    nombre = jsonNombre.get("nombre").asText();
                }

                String nombreFinal = (nombre != null) ? nombre : "Nombre no disponible";
                String textoFinal = "Portafolio de: " + nombreFinal;

                String urlSaldo = "http://localhost:8080/api/portafolio/" + usuarioId + "/saldo";
                HttpRequest saldoRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlSaldo))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> saldoResponse = client.send(saldoRequest, HttpResponse.BodyHandlers.ofString());
                double saldoEuros = Double.parseDouble(saldoResponse.body());

                Platform.runLater(() -> {
                    nombreUsuarioLabel.setText(textoFinal);
                });

                String urlTransacciones = "http://localhost:8080/api/transacciones/usuario?usuarioId=" + usuarioId;
                HttpRequest transaccionesRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlTransacciones))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> transaccionesResponse = client.send(transaccionesRequest,
                        HttpResponse.BodyHandlers.ofString());
                List<JsonNode> transacciones = mapper.readValue(transaccionesResponse.body(),
                        new TypeReference<List<JsonNode>>() {
                        });

                String urlActivos = "http://localhost:8080/api/transacciones/" + usuarioId + "/activos";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlActivos))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                List<CriptoPosesionDTO> lista = mapper.readValue(response.body(),
                        new TypeReference<List<CriptoPosesionDTO>>() {
                        });
                double valorCriptos = lista.stream().mapToDouble(CriptoPosesionDTO::getValorTotal).sum();

                double compras = transacciones.stream()
                        .filter(t -> t.get("tipoTransaccion").asText().equalsIgnoreCase("COMPRAR"))
                        .mapToDouble(t -> t.get("valorTotal").asDouble())
                        .sum();

                double ventas = transacciones.stream()
                        .filter(t -> t.get("tipoTransaccion").asText().equalsIgnoreCase("VENDER"))
                        .mapToDouble(t -> t.get("valorTotal").asDouble())
                        .sum();

                double valorActual = saldoEuros + valorCriptos;
                double capitalInicial = 10_000.00;
                double beneficioNeto = valorActual - capitalInicial;
                double rendimientoPct = (beneficioNeto / capitalInicial) * 100;

                String resumen = String.format(
                        "Valor en criptomonedas: €%,.2f\n" +
                                "Rendimiento: %+.2f € (%.2f%%)\n",
                        valorCriptos, beneficioNeto, rendimientoPct);

                Platform.runLater(() -> {
                    data.clear();
                    data.addAll(lista);
                    nombreUsuarioLabel.setText("Portafolio de: " + usuarioId);
                    saldoLabel.setText(String.format("Saldo disponible: €%,.2f", saldoEuros));
                    balanceLabel.setText(String.format("Balance total: €%,.2f", valorActual));
                    totalLabel.setText(resumen);
                });

                String urlEvolucion = "http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion-completa";
                HttpRequest evRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlEvolucion))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> evResponse = client.send(evRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode evolucion = mapper.readTree(evResponse.body());

                Platform.runLater(() -> {
                    saldoSeries.getData().clear();
                    balanceSeries.getData().clear();

                    for (JsonNode punto : evolucion) {
                        System.out.println("➡ Punto recibido: " + punto.toPrettyString());
                        int dia = punto.get("dia").asInt();
                        double saldo = punto.get("saldoEuros").asDouble();
                        double valorTotal = punto.get("valorTotal").asDouble();

                        System.out.printf("📈 GRAFICO >> Día: %d | Saldo: %.2f | Balance: %.2f%n", dia, saldo,
                                valorTotal);
                        System.out.printf("📊 LABELS >> Saldo: %.2f | Criptos: %.2f | Balance: %.2f%n",
                                saldoEuros, valorCriptos, valorActual);

                        saldoSeries.getData().add(new XYChart.Data<>(dia, saldo));
                        balanceSeries.getData().add(new XYChart.Data<>(dia, valorTotal));
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de conexión");
                    alert.setHeaderText("No se pudo cargar el portafolio");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
                System.err.println("Error al obtener portafolio: " + e.getMessage());
            }

        }).start();
    }

    private void generarReporteExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("reporte-transacciones.xlsx");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file == null)
            return;

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/transacciones/usuario?usuarioId=" + usuarioId;
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ObjectMapper mapper = new ObjectMapper();
                List<JsonNode> transacciones = mapper.readValue(response.body(), new TypeReference<List<JsonNode>>() {
                });

                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Transacciones");
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("Fecha");
                    header.createCell(1).setCellValue("Criptomoneda");
                    header.createCell(2).setCellValue("Cantidad");
                    header.createCell(3).setCellValue("Precio");
                    header.createCell(4).setCellValue("Valor Total");
                    header.createCell(5).setCellValue("Tipo");

                    int rowIdx = 1;
                    for (JsonNode t : transacciones) {
                        System.out.println("Transacción JSON: " + t.toPrettyString());
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(
                                t.hasNonNull("fechaTransaccion") ? t.get("fechaTransaccion").asText() : "");
                        row.createCell(1).setCellValue(t.hasNonNull("cryptoId") ? t.get("cryptoId").asText() : "");
                        row.createCell(2).setCellValue(
                                t.hasNonNull("cantidadCrypto") ? t.get("cantidadCrypto").asDouble() : 0.0);
                        row.createCell(3).setCellValue(
                                t.hasNonNull("precioTransaccion") ? t.get("precioTransaccion").asDouble() : 0.0);
                        row.createCell(4)
                                .setCellValue(t.hasNonNull("valorTotal") ? t.get("valorTotal").asDouble() : 0.0);
                        row.createCell(5)
                                .setCellValue(t.hasNonNull("tipoTransaccion") ? t.get("tipoTransaccion").asText() : "");

                    }

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        workbook.write(fos);
                    }
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText("Reporte generado correctamente");
                    alert.setContentText("Archivo guardado en:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("No se pudo generar el reporte");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                });
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Crea un punto de color para la leyenda del gráfico.
     *
     * @param color El color del punto.
     * @return Un nodo que representa el punto de color.
     */
    private Node createColorDot(Color color) {
        Circle circle = new Circle(6);
        circle.setFill(color);
        circle.setStrokeWidth(1);
        return circle;
    }

}
