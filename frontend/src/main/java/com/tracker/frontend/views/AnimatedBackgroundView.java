package com.tracker.frontend.views;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Clase que representa una vista de fondo animado.
 * Esta clase extiende Pane y utiliza un Canvas para dibujar una imagen de fondo
 * que rota continuamente.
 */
public class AnimatedBackgroundView extends Pane {

	private final Canvas canvas;
	private final Image backgroundImage;
	private double angle = 0;

	/**
	 * Constructor de la clase AnimatedBackgroundView.
	 * Inicializa el canvas y la imagen de fondo.
	 *
	 * @param imagePath Ruta de la imagen de fondo.
	 */
	public AnimatedBackgroundView(String imagePath) {
		canvas = new Canvas();
		this.getChildren().add(canvas);

		backgroundImage = new Image(getClass().getResource(imagePath).toExternalForm());

		canvas.widthProperty().bind(this.widthProperty());
		canvas.heightProperty().bind(this.heightProperty());
		this.setMouseTransparent(true); 

		startAnimation();
	}

	/**
	 * Método que inicia la animación de rotación de la imagen de fondo.
	 * Utiliza un AnimationTimer para actualizar el canvas en cada fotograma.
	 */
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

	            gc.translate(canvasWidth / 2, canvasHeight / 2);
	            gc.rotate(angle);

	            double scaleFactor = Math.max(
	                canvasWidth / backgroundImage.getWidth(),
	                canvasHeight / backgroundImage.getHeight()
	            ) * Math.sqrt(2); 

	            gc.scale(scaleFactor, scaleFactor);

	            gc.drawImage(backgroundImage,
	                -backgroundImage.getWidth() / 2,
	                -backgroundImage.getHeight() / 2
	            );

	            gc.restore();

	            angle += 0.02;
	        }
	    }.start();
	}

}
