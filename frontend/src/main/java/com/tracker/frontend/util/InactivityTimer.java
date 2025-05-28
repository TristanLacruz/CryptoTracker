package com.tracker.frontend.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

/**
 * Clase InactivityTimer que gestiona el tiempo de inactividad del usuario.
 * Si el usuario no interactúa con la aplicación durante 60 minutos, se ejecuta una acción.
 */
public class InactivityTimer {

    private final PauseTransition timeout;
    private final Stage stage;

    /**
     * Constructor de InactivityTimer.
     * @param stage El escenario de la aplicación.
     * @param onTimeout La acción a ejecutar cuando se alcanza el tiempo de inactividad.
     */
    public InactivityTimer(Stage stage, Runnable onTimeout) {
        this.stage = stage;
        this.timeout = new PauseTransition(Duration.minutes(60)); 

        timeout.setOnFinished(e -> {
            Platform.runLater(() -> {
                onTimeout.run();
            });
        });
    }

    /**
     * Método para adjuntar el temporizador de inactividad a una escena.
     * Registra un manejador de eventos para detectar el movimiento del ratón.
     * @param scene La escena a la que se adjunta el temporizador.
     */
    public void attachToScene(Scene scene) {
        EventHandler<MouseEvent> mouseMovedHandler = e -> resetTimer();
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        start();
    }

    /**
     * Método para adjuntar el temporizador de inactividad a un escenario.
     * Registra un manejador de eventos para detectar el movimiento del ratón.
     */
    public void start() {
        timeout.playFromStart();
    }

    /**
     * Método para detener el temporizador de inactividad.
     * Se puede llamar cuando se desea pausar el temporizador, por ejemplo, al cerrar la aplicación.
     */
    public void stop() {
        timeout.stop();
    }

    /**
     * Método para reiniciar el temporizador de inactividad.
     * Se puede llamar cuando se detecta una interacción del usuario, como un movimiento del ratón.
     */
    public void resetTimer() {
        timeout.playFromStart();
    }
}
