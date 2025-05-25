package com.tracker.frontend.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class InactivityTimer {

    private final PauseTransition timeout;
    private final Stage stage;

    public InactivityTimer(Stage stage, Runnable onTimeout) {
        this.stage = stage;
        this.timeout = new PauseTransition(Duration.minutes(5)); // ⏱ 1 minuto

        // Acción tras el tiempo de inactividad
        timeout.setOnFinished(e -> {
            Platform.runLater(() -> {
                onTimeout.run();
            });
        });
    }

    public void attachToScene(Scene scene) {
        EventHandler<MouseEvent> mouseMovedHandler = e -> resetTimer();
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        start();
    }

    public void start() {
        timeout.playFromStart();
    }

    public void stop() {
        timeout.stop();
    }

    public void resetTimer() {
        timeout.playFromStart();
    }
}
