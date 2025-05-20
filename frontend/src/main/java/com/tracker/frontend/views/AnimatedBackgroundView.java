package com.tracker.frontend.views;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class AnimatedBackgroundView extends Pane {

	private final Canvas canvas;
	private final Image backgroundImage;
	private double angle = 0;

	public AnimatedBackgroundView(String imagePath) {
		canvas = new Canvas();
		this.getChildren().add(canvas);

		backgroundImage = new Image(getClass().getResource(imagePath).toExternalForm());

		// 🔄 Vincula el tamaño del canvas al tamaño del Pane
		canvas.widthProperty().bind(this.widthProperty());
		canvas.heightProperty().bind(this.heightProperty());

		startAnimation();
	}

	private void startAnimation() {
	    GraphicsContext gc = canvas.getGraphicsContext2D();

	    new AnimationTimer() {
	        private double angle = 0;

	        @Override
	        public void handle(long now) {
	            double canvasWidth = canvas.getWidth();
	            double canvasHeight = canvas.getHeight();

	            gc.clearRect(0, 0, canvasWidth, canvasHeight);

	            gc.save();

	            // Centro del canvas (centro de rotación)
	            gc.translate(canvasWidth / 2, canvasHeight / 2);
	            gc.rotate(angle);

	            // 🔁 Calcular escala para que la imagen siempre cubra la pantalla al rotar
	            double scaleFactor = Math.max(
	                canvasWidth / backgroundImage.getWidth(),
	                canvasHeight / backgroundImage.getHeight()
	            ) * Math.sqrt(2); // √2 garantiza cubrir incluso en diagonal

	            gc.scale(scaleFactor, scaleFactor);

	            // Dibujar centrado
	            gc.drawImage(backgroundImage,
	                -backgroundImage.getWidth() / 2,
	                -backgroundImage.getHeight() / 2
	            );

	            gc.restore();

	            angle += 0.02; // velocidad de rotación
	        }
	    }.start();
	}

}
