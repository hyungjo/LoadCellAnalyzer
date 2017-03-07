package com.loadcell.view;

import com.loadcell.fileoutput.ExcelWriter;
import com.loadcell.model.BridgeConnectionStatus;
import com.loadcell.model.BridgeData;
import com.loadcell.util.sensor.Sensor;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;


public class RootLayoutController {
	private static final int MAX_DATA_POINTS = 1000;

	private VBox rootLayout;

	private Sensor sensor;

	private Map<Integer, LineChart<Number, Number>> bridgeLineChart;

	private Map<Integer, NumberAxis> bridgeChartXAxis;

	private int xSeriesData = 0;

	private Map<Integer, XYChart.Series<Number, Number>> bridgeSeriesData;

	private Map<Integer, Label> bridgeDataLabel;
	private Map<Integer, TextField> bridgeDataField;
	private Map<Integer, StringProperty> bridgeDataFieldProperty;

	private Map<Integer, Label> settingLabel;
	private Map<Integer, TextField> bridgeZeroingField;
	private Map<Integer, Button> bridgeZeroingBtn;
	private Map<Integer, Button> bridgeCalibrationBeforeBtn;
	private Map<Integer, TextField> bridgeCalibrationBeforeField;
	private Map<Integer, Button> bridgeCalibrationAfterBtn;
	private Map<Integer, TextField> bridgeCalibrationAfterField;

	private Map<Integer, Label> recordingLabel;
	private Map<Integer, Button> bridgeDataRecordingStartBtn;
	private Map<Integer, Button> bridgeDataRecordingStopBtn;

	private Map<Integer, ExcelWriter> excelWriter;

	private BridgeConnectionStatus bridgeConnectionStatus;

	private double tempZeroingMVValue;

	@FXML
	private void initialize() {

		bridgeLineChart = new HashMap<>();
		bridgeChartXAxis = new HashMap<>();
		bridgeSeriesData = new HashMap<>();

		bridgeDataLabel = new HashMap<>();
		bridgeDataField = new HashMap<>();
		bridgeDataFieldProperty = new HashMap<>();

		settingLabel = new HashMap<>();
		bridgeZeroingField = new HashMap<>();
		bridgeZeroingBtn = new HashMap<>();
		bridgeCalibrationBeforeBtn = new HashMap<>();
		bridgeCalibrationBeforeField = new HashMap<>();
		bridgeCalibrationAfterBtn = new HashMap<>();
		bridgeCalibrationAfterField = new HashMap<>();

		recordingLabel = new HashMap<>();
		bridgeDataRecordingStartBtn = new HashMap<>();
		bridgeDataRecordingStopBtn = new HashMap<>();

		excelWriter = new HashMap<>();
	}

	public void setRootLayout(VBox rootLayout) {
		this.rootLayout = rootLayout;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public void setBridgeConnectionStatus(BridgeConnectionStatus bridgeConnectionStatus) {
		this.bridgeConnectionStatus = bridgeConnectionStatus;
	}

	public void initRootRayout() {
		initChartLayout();
		updateRootLayout();
	}

	private void initChartLayout() {
		// Iterate Connected Bridge Number
		for (Map.Entry<Integer, Boolean> connectedBridge : this.bridgeConnectionStatus.getConnectedList().entrySet()) {
			// Bridge Number
			final int index = connectedBridge.getKey();

			// Bridge Data
			final BridgeData bridgeData = sensor.getBridgeDataQueueList().get(index);

			// Initialize Bridge Control Box
			final VBox bridgeBox = new VBox(10);
			final HBox bridgeController = new HBox(10);

			// Initialize Chart Series
			bridgeSeriesData.put(index, new XYChart.Series<>());
			bridgeSeriesData.get(index).setName("Bridge " + index);

			// Initialize Chart XAxis
			bridgeChartXAxis.put(index, new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10));
			bridgeChartXAxis.get(index).setAutoRanging(false);
//            bridgeChartXAxis.get(index).setForceZeroInRange(false);
//            bridgeChartXAxis.get(index).setTickLabelsVisible(false);
//            bridgeChartXAxis.get(index).setTickMarkVisible(false);
//            bridgeChartXAxis.get(index).setMinorTickVisible(false);

			// Initialize Line Chart
			bridgeLineChart.put(index, new LineChart<Number, Number>(bridgeChartXAxis.get(index), new NumberAxis()) {
				// Override to remove symbols on each data point
				@Override
				protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
				}
			});
			bridgeLineChart.get(index).setTitle("Bridge " + index + " Chart");
			bridgeLineChart.get(index).setAnimated(false);
			bridgeLineChart.get(index).setHorizontalGridLinesVisible(true);
			bridgeLineChart.get(index).getData().add(bridgeSeriesData.get(index));

			// Initialize Bridge Data Label
			bridgeDataLabel.put(index, new Label("Current Data: "));

			// Initialize Bridge Data Property for Binding Bridge Data Field
			bridgeDataFieldProperty.put(index, new SimpleStringProperty());

			// Initialize Bridge Data Field
			bridgeDataField.put(index, new TextField());
			bridgeDataField.get(index).setPrefWidth(100);
			bridgeDataField.get(index).textProperty().bind(bridgeDataFieldProperty.get(index));

			// Initialize Setting Label
			settingLabel.put(index, new Label("Setting: "));

			// Initialize Zeroing Field
			bridgeZeroingField.put(index, new TextField("1000"));
			bridgeZeroingField.get(index).setPrefWidth(60);

			// Initialize Zeroing Button
			bridgeZeroingBtn.put(index, new Button("Zeroing"));
			bridgeZeroingBtn.get(index).getStyleClass().add("primary");
			bridgeZeroingBtn.get(index).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					double sumZeroingValue = 0;

					// Set Zeroing Value
					bridgeData.setZeroingValue(0);

					// Set Zeroing MVValue
					for (int i = 0; i < Integer.parseInt(bridgeZeroingField.get(index).getText()); i++) {
						sumZeroingValue += bridgeData.peekBridgeDataQueue();
					}
					bridgeData.setZeroingMVValue(sumZeroingValue / Integer.parseInt(bridgeZeroingField.get(index).getText()));

					// Set Zeroing Flag
					bridgeData.setZeroingFlag(true);

					// Field & Button Disable / Able
					((Button) event.getSource()).setDisable(false);
					bridgeZeroingField.get(index).setDisable(false);
				}
			});

			// Initialize Calibration Before Field
			bridgeCalibrationBeforeField.put(index, new TextField());
			bridgeCalibrationBeforeField.get(index).setPrefWidth(60);

			// Initialize Calibration Before Button
			bridgeCalibrationBeforeBtn.put(index, new Button("Calibration [Before]"));
			bridgeCalibrationBeforeBtn.get(index).getStyleClass().add("success");
			bridgeCalibrationBeforeBtn.get(index).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					double sumCalibrationBeforeMVValue = 0;

					// Set CalibrationBefore Value
					bridgeData.setCalibrationBeforeValue(Integer.parseInt(bridgeCalibrationBeforeField.get(index).getText()));

					// Set CalibrationBefore MVValue
					for (int i = 0; i < Integer.parseInt(bridgeZeroingField.get(index).getText()); i++) {
						sumCalibrationBeforeMVValue += bridgeData.peekZeroingBridgeDataQueue();
					}
					bridgeData.setCalibrationBeforeMVValue(sumCalibrationBeforeMVValue / Integer.parseInt(bridgeZeroingField.get(index).getText()));

					// Set CalibrationBefore Flag
					bridgeData.setCalibrationBeforeFlag(true);

					// Field & Button Disable / Able
					((Button) event.getSource()).setDisable(true);
					bridgeCalibrationBeforeField.get(index).setDisable(true);
					bridgeCalibrationAfterField.get(index).setDisable(false);
					bridgeCalibrationAfterBtn.get(index).setDisable(false);
				}
			});

			// Initialize Calibration After Field
			bridgeCalibrationAfterField.put(index, new TextField());
			bridgeCalibrationAfterField.get(index).setPrefWidth(60);
			bridgeCalibrationAfterField.get(index).setDisable(true);

			// Initialize Calibration After Button
			bridgeCalibrationAfterBtn.put(index, new Button("Calibration [After]"));
			bridgeCalibrationAfterBtn.get(index).getStyleClass().add("warning");
			bridgeCalibrationAfterBtn.get(index).setDisable(true);
			bridgeCalibrationAfterBtn.get(index).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					double calibrationAfterMVValue = 0;

					// Set CalibrationAfter Value
					bridgeData.setCalibrationAfterValue(Integer.parseInt(bridgeCalibrationAfterField.get(index).getText()));

					// Set CalibrationAfter MVValue
					for (int i = 0; i < Integer.parseInt(bridgeZeroingField.get(index).getText()); i++) {
						calibrationAfterMVValue += bridgeData.peekZeroingBridgeDataQueue();
					}
					bridgeData.setCalibrationAfterMVValue(calibrationAfterMVValue / Integer.parseInt(bridgeZeroingField.get(index).getText()));

					// Set CalibrationAfter Flag
					bridgeData.setCalibrationAfterFlag(true);

					// Field & Button Disable / Able
					((Button) event.getSource()).setDisable(true);
					bridgeCalibrationAfterField.get(index).setDisable(false);
					bridgeCalibrationBeforeField.get(index).setDisable(false);
					bridgeCalibrationBeforeBtn.get(index).setDisable(false);
				}
			});

			// Initialize Recording Label
			recordingLabel.put(index, new Label("Recording: "));

			// Initialize Recording Start Button
			bridgeDataRecordingStartBtn.put(index, new Button("Start"));
			bridgeDataRecordingStartBtn.get(index).getStyleClass().add("success");
			bridgeDataRecordingStartBtn.get(index).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Crate ExcelWriter Thread
					if (!excelWriter.containsKey(index)) {
						excelWriter.put(index, new ExcelWriter(Integer.toString(index), bridgeData));
						excelWriter.get(index).start();
					} else {
						excelWriter.remove(index);
						excelWriter.put(index, new ExcelWriter(Integer.toString(index), bridgeData));
						excelWriter.get(index).start();
					}

					// Field & Button Disable / Able
					((Button) event.getSource()).setDisable(true);
					bridgeDataRecordingStopBtn.get(index).setDisable(false);
				}
			});

			// Initialize Recording Stop Button
			bridgeDataRecordingStopBtn.put(index, new Button("Stop"));
			bridgeDataRecordingStopBtn.get(index).getStyleClass().add("danger");
			bridgeDataRecordingStopBtn.get(index).setDisable(true);
			bridgeDataRecordingStopBtn.get(index).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Interrupt ExcelWriter Thread
					excelWriter.get(index).interrupt();

					// Field & Button Disable / Able
					((Button) event.getSource()).setDisable(true);
					bridgeDataRecordingStartBtn.get(index).setDisable(false);
				}
			});

			// Add Component to Box
			// Bridge Data Display Control
			bridgeController.setPadding(new Insets(5, 5, 5, 15));
			bridgeController.getChildren().add(bridgeDataLabel.get(index));
			bridgeController.getChildren().add(bridgeDataField.get(index));

			// Bridge Setting Control
			bridgeController.getChildren().add(settingLabel.get(index));
			bridgeController.getChildren().add(bridgeZeroingField.get(index));
			bridgeController.getChildren().add(bridgeZeroingBtn.get(index));
			bridgeController.getChildren().add(bridgeCalibrationBeforeField.get(index));
			bridgeController.getChildren().add(bridgeCalibrationBeforeBtn.get(index));
			bridgeController.getChildren().add(bridgeCalibrationAfterField.get(index));
			bridgeController.getChildren().add(bridgeCalibrationAfterBtn.get(index));

			// Bridge Data Recording Control
			bridgeController.getChildren().add(recordingLabel.get(index));
			bridgeController.getChildren().add(bridgeDataRecordingStartBtn.get(index));
			bridgeController.getChildren().add(bridgeDataRecordingStopBtn.get(index));

			// Make One Box
			bridgeBox.getChildren().add(bridgeController);
			bridgeBox.getChildren().add(bridgeLineChart.get(index));

			// Add Box
			rootLayout.getChildren().add(bridgeBox);
		}
	}

	private void updateRootLayout() {
		// Iterate Connected Bridge Number
		for (Map.Entry<Integer, Boolean> connectedBridge : this.bridgeConnectionStatus.getConnectedList().entrySet()) {
			// Bridge Number
			final int index = connectedBridge.getKey();

			// Bridge Data
			final BridgeData bridgeData = sensor.getBridgeDataQueueList().get(index);

			// Create Chart Update Thread
			new AnimationTimer() {
				@Override
				public void handle(long now) {
					// Bridge Value
					final double bridgeValue = bridgeData.takeFinalBridgeDataQueue();

					// Add New Chart Series Data
					bridgeSeriesData.get(index).getData().add(new XYChart.Data<>(xSeriesData++, bridgeValue));

					// Remove Old Chart Series Data
					if (bridgeSeriesData.get(index).getData().size() > MAX_DATA_POINTS) {
						bridgeSeriesData.get(index).getData().remove(0, bridgeSeriesData.get(index).getData().size() - MAX_DATA_POINTS);
					}

					// Update Chart Axis Bound
					bridgeChartXAxis.get(index).setLowerBound(xSeriesData - MAX_DATA_POINTS);
					bridgeChartXAxis.get(index).setUpperBound(xSeriesData - 1);

					// Delay Chart Update Thread per 1ms
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
			new AnimationTimer() {
				@Override
				public void handle(long now) {
					// Bridge Value
					final double bridgeValue = bridgeData.peekFinalBridgeDataQueue();

					// Update Bridge Data Field
					bridgeDataFieldProperty.get(index).set(String.format("%10.5f", bridgeValue));

					// Delay Bridge Data Field Update Thread per 1ms
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
