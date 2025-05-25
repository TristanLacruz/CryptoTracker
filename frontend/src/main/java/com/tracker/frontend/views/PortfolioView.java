package com.tracker.frontend.views;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.common.dto.CriptoPosesionDTO;
import com.tracker.frontend.session.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class PortfolioView {

    private final String usuarioId;

    public PortfolioView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar() {
        Label nombreUsuarioLabel = new Label("Cargando nombre del usuario...");
        nombreUsuarioLabel.getStyleClass().add("label");

        Stage stage = new Stage();
        stage.setTitle("Mi Portafolio");

        TableView<CriptoPosesionDTO> tableView = new TableView<>();
        ObservableList<CriptoPosesionDTO> data = FXCollections.observableArrayList();

        Label totalLabel = new Label("Total del portafolio: €0.00");
        Button btnGenerarReporte = new Button("Generar Reporte");
        btnGenerarReporte.setOnAction(e -> generarReporteExcel());

        TableColumn<CriptoPosesionDTO, String> simboloCol = new TableColumn<>("Criptomoneda");
        simboloCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("simbolo"));
        simboloCol.setMaxWidth(1f * Integer.MAX_VALUE * 33); // 33%

        TableColumn<CriptoPosesionDTO, Double> cantidadCol = new TableColumn<>("Cantidad");
        cantidadCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantidad"));
        cantidadCol.setMaxWidth(1f * Integer.MAX_VALUE * 33); // 33%
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

        // Gráfico de evolución del saldo
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Días");
        yAxis.setLabel("Saldo (€)");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Evolución del saldo");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Saldo en €");
        lineChart.getData().add(series);

        VBox vbox = new VBox(10,
                nombreUsuarioLabel,
                tableView,
                totalLabel,
                btnGenerarReporte,
                lineChart);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: transparent;");

        AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
        StackPane root = new StackPane(fondo, vbox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        stage.setScene(scene);
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

                Platform.runLater(() -> {
                    nombreUsuarioLabel.setText(textoFinal);
                });

                // 🔁 Obtener resumen de criptomonedas
                String url = "http://localhost:8080/api/transacciones/" + usuarioId + "/activos";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Respuesta de /activos: " + response.body());

                List<CriptoPosesionDTO> lista = mapper.readValue(response.body(),
                        new TypeReference<List<CriptoPosesionDTO>>() {
                        });

                // 🔁 Obtener cantidad invertida
                String urlInversion = "http://localhost:8080/api/transacciones/invertido/" + usuarioId;
                HttpRequest invRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlInversion))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> invResponse = client.send(invRequest, HttpResponse.BodyHandlers.ofString());

                double invertido = Double.parseDouble(invResponse.body());
                double total = lista.stream().mapToDouble(CriptoPosesionDTO::getValorTotal).sum();
                double diferencia = total - invertido;
                double rendimiento = invertido == 0 ? 0 : (diferencia / invertido) * 100;

                String resumen = String.format("Total del portafolio: €%,.2f\nRendimiento: %+.2f € (%.2f%%)", total,
                        diferencia, rendimiento);

                Platform.runLater(() -> {
                    data.addAll(lista);
                    totalLabel.setText(resumen);
                });

                // 🔁 Obtener evolución del saldo
                String urlEvolucion = "http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion";
                HttpRequest evRequest = HttpRequest.newBuilder()
                        .uri(URI.create(urlEvolucion))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> evResponse = client.send(evRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode evolucion = mapper.readTree(evResponse.body());

                Platform.runLater(() -> {
                    data.clear();
                    data.addAll(lista);
                    if (evolucion.size() > 0) {
                        // Busca la primera fecha válida
                        Optional<LocalDate> inicioOpt = StreamSupport.stream(evolucion.spliterator(), false)
                                .map(p -> p.get("fecha"))
                                .filter(f -> f != null && !f.isNull() && !"null".equals(f.asText()))
                                .map(f -> {
                                    try {
                                        return LocalDate.parse(f.asText());
                                    } catch (Exception ex) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .findFirst();

                        if (!inicioOpt.isPresent()) {
                            System.err.println("❌ No hay fechas válidas en evolución.");
                            return;
                        }

                        LocalDate inicio = inicioOpt.get();
                        for (JsonNode punto : evolucion) {
                            JsonNode fechaNode = punto.get("fecha");
                            JsonNode saldoNode = punto.get("saldoTotal");

                            if (fechaNode != null && !fechaNode.isNull() && !"null".equals(fechaNode.asText())
                                    && saldoNode != null && !saldoNode.isNull()) {
                                try {
                                    LocalDate fecha = LocalDate.parse(fechaNode.asText());
                                    double saldo = saldoNode.asDouble();
                                    System.out.println("✅ Fecha: " + fecha + ", Saldo: " + saldo);

                                    long dias = ChronoUnit.DAYS.between(inicio, fecha);
                                    series.getData().add(new XYChart.Data<>(dias, saldo));
                                } catch (Exception ex) {
                                    System.err.println("❌ Error al procesar punto de evolución: " + punto);
                                }
                            }
                        }
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
                        row.createCell(0).setCellValue(t.hasNonNull("fechaTransaccion") ? t.get("fechaTransaccion").asText() : "");
                        row.createCell(1).setCellValue(t.hasNonNull("cryptoId") ? t.get("cryptoId").asText() : "");
                        row.createCell(2).setCellValue(t.hasNonNull("cantidadCrypto") ? t.get("cantidadCrypto").asDouble() : 0.0);
                        row.createCell(3).setCellValue(t.hasNonNull("precioTransaccion") ? t.get("precioTransaccion").asDouble() : 0.0);
                        row.createCell(4).setCellValue(t.hasNonNull("valorTotal") ? t.get("valorTotal").asDouble() : 0.0);
                        row.createCell(5).setCellValue(t.hasNonNull("tipoTransaccion") ? t.get("tipoTransaccion").asText() : "");

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

}
