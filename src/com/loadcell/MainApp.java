package com.loadcell;

import com.loadcell.model.BridgeConnectionStatus;
import com.loadcell.util.sensor.Sensor;
import com.loadcell.view.RootLayoutController;
import com.loadcell.view.SettingLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
	private VBox rootLayout;
	private AnchorPane settingLayout;

	private RootLayoutController rlc;
	private SettingLayoutController sl;

	private Sensor sensor;
	private BridgeConnectionStatus bridgeConnectionStatus;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		bridgeConnectionStatus = new BridgeConnectionStatus();
	}

	@Override
	public void stop() throws Exception {
		sensor.disconnection();

		Platform.exit();
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) {
		initSettingLayout(primaryStage);

		sensor = new Sensor(bridgeConnectionStatus);

		initRootLayout(primaryStage);
	}

	public void initSettingLayout(Stage primaryStage) {
		try {
			Stage settingStage = new Stage();
			FXMLLoader settingLayoutLoader = new FXMLLoader();
			settingLayoutLoader.setLocation(MainApp.class.getResource("view/SettingLayout.fxml"));
			settingLayout = settingLayoutLoader.load();
			sl = settingLayoutLoader.getController();
			sl.setBridgeConnectionStatus(bridgeConnectionStatus);
			sl.setSettingLayout(settingStage);

			settingStage.initOwner(primaryStage);
			settingStage.setScene(new Scene(settingLayout));
			settingStage.setTitle("Load Cell Analyzer - Setting");
			settingStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout(Stage primaryStage) {
		try {
			FXMLLoader rootLayoutLoader = new FXMLLoader();
			rootLayoutLoader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = rootLayoutLoader.load();
			rlc = rootLayoutLoader.getController();
			rlc.setRootLayout(rootLayout);
			rlc.setSensor(sensor);
			rlc.setBridgeConnectionStatus(bridgeConnectionStatus);
			rlc.initRootRayout();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene chartScene = new Scene(rootLayout);
		chartScene.getStylesheets().add(MainApp.class.getResource("view/bootstrap3.css").toExternalForm());
		primaryStage.setScene(chartScene);
		primaryStage.setTitle("Load Cell Analyzer");
		primaryStage.show();
	}
}
