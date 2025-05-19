package com.tracker.frontend;

import com.tracker.frontend.views.MainMenuView;

import javafx.application.Application;
import javafx.stage.Stage;

public class DesktopLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainMenuView().mostrar(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
