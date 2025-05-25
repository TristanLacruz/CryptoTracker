// package com.tracker.frontend.views.graficos;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.tracker.common.dto.EvolucionCompletaDTO;
// import com.tracker.frontend.session.Session;
// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.chart.LineChart;
// import javafx.scene.chart.NumberAxis;
// import javafx.scene.chart.XYChart;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.List;

// /**
//  * Clase que representa una vista de gráfico combinado para mostrar la evolución del portafolio.
//  */
// public class GraficoCombinadoView {

//     private final String usuarioId;

//     public GraficoCombinadoView(String usuarioId) {
//         this.usuarioId = usuarioId;
//     }

//     /**
//      * Muestra el gráfico combinado de la evolución del portafolio.
//      */
//     public void mostrar() {
//         Stage stage = new Stage();
//         stage.setTitle("Evolución del portafolio");

//         NumberAxis xAxis = new NumberAxis();
//         xAxis.setLabel("Día");

//         NumberAxis yAxis = new NumberAxis();
//         yAxis.setLabel("€");

//         LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
//         chart.setTitle("Valor total y ganancia neta");

//         XYChart.Series<Number, Number> valorSeries = new XYChart.Series<>();
//         valorSeries.setName("Valor diario (€)");

//         XYChart.Series<Number, Number> gananciaSeries = new XYChart.Series<>();
//         gananciaSeries.setName("Ganancia acumulada (€)");

//         VBox vbox = new VBox(10, chart);
//         vbox.setPadding(new Insets(10));
//         stage.setScene(new Scene(vbox, 700, 400));
//         stage.show();

//         new Thread(() -> {
//             try {
//                 String url = "http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion-completa";
//                 HttpRequest request = HttpRequest.newBuilder()
//                         .uri(URI.create(url))
//                         .header("Authorization", "Bearer " + Session.idToken)
//                         .GET()
//                         .build();

//                 HttpClient client = HttpClient.newHttpClient();
//                 HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//                 ObjectMapper mapper = new ObjectMapper();
//                 List<EvolucionCompletaDTO> datos = mapper.readValue(
//                     response.body(), 
//                     new TypeReference<List<EvolucionCompletaDTO>>() {}
//                 );

//                 for (EvolucionCompletaDTO punto : datos) {
//                     valorSeries.getData().add(new XYChart.Data<>(punto.getDia(), punto.getValorTotal()));
//                     gananciaSeries.getData().add(new XYChart.Data<>(punto.getDia(), punto.getGanancia()));
//                 }

//                 valorSeries.getNode().setStyle("-fx-stroke: #00FF00; -fx-stroke-width: 2px;");
//                 gananciaSeries.getNode().setStyle("-fx-stroke: #FF4081; -fx-stroke-width: 2px;");
//                 Platform.runLater(() -> chart.getData().addAll(valorSeries, gananciaSeries));

//             } catch (Exception e) {
//                 System.err.println("Error al cargar gráfico combinado: " + e.getMessage());
//                 e.printStackTrace();
//             }
//         }).start();
//     }
// }
