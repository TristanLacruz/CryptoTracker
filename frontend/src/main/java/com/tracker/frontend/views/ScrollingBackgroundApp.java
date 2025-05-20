package com.tracker.frontend.views;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScrollingBackgroundApp extends Application {

    private static final double SCROLL_SPEED = 10.0;

    @Override
    public void start(Stage stage) {
    	Image backgroundImage = new Image(getClass().getResource("/images/fondo.jpg").toExternalForm());

        ImageView bg1 = new ImageView(backgroundImage);
        ImageView bg2 = new ImageView(backgroundImage);

        // Coloca la segunda imagen justo a la derecha de la primera
        bg2.setX(backgroundImage.getWidth());

        Pane root = new Pane(bg1, bg2);

        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
            	System.out.println("bg1.x = " + bg1.getX());

                // Mueve ambas imágenes hacia la izquierda
                bg1.setX(bg1.getX() - SCROLL_SPEED);
                bg2.setX(bg2.getX() - SCROLL_SPEED);

                // Si una imagen se sale completamente de la izquierda, muévela al final
                if (bg1.getX() + backgroundImage.getWidth() <= 0) {
                    bg1.setX(bg2.getX() + backgroundImage.getWidth());
                }
                if (bg2.getX() + backgroundImage.getWidth() <= 0) {
                    bg2.setX(bg1.getX() + backgroundImage.getWidth());
                }
            }
        }.start();

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Scrolling Background");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
