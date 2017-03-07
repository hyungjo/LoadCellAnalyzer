package com.loadcell.view;

import com.loadcell.model.BridgeConnectionStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingLayoutController {
	@FXML
	private CheckBox bridgeSelectChkBox0;
	@FXML
	private CheckBox bridgeSelectChkBox1;
	@FXML
	private CheckBox bridgeSelectChkBox2;
	@FXML
	private CheckBox bridgeSelectChkBox3;
	@FXML
	private TextField dataRateField;
	@FXML
	private ComboBox<Integer> gainField;
	@FXML
	private TextField movingAverageField;

	private Stage settingStage;
	private BridgeConnectionStatus bridgeConnectionStatus;

	@FXML
	private void initialize() {
		//Gain Field Initialize
		gainField.getItems().addAll(1, 8, 16, 32, 64, 128);
		gainField.setValue(128);
	}

	public void setSettingLayout(Stage settingStage) {
		this.settingStage = settingStage;
	}

	public void setBridgeConnectionStatus(BridgeConnectionStatus bridgeConnectionStatus) {
		this.bridgeConnectionStatus = bridgeConnectionStatus;
	}

	@FXML
	public void handleOk() {
		String errorMsg = "";
		Boolean bridgeSelectValidation = false;

		//Validation Input Value
		if (bridgeSelectChkBox0.isSelected()) {
			bridgeSelectValidation |= true;
		}
		if (bridgeSelectChkBox1.isSelected()) {
			bridgeSelectValidation |= true;
		}
		if (bridgeSelectChkBox2.isSelected()) {
			bridgeSelectValidation |= true;
		}
		if (bridgeSelectChkBox3.isSelected()) {
			bridgeSelectValidation |= true;
		}
		int dataRateValue = Integer.parseInt(dataRateField.getText());
		int movingAverageValue = Integer.parseInt(movingAverageField.getText());

		if (!bridgeSelectValidation) {
			errorMsg += "Select Bridge Number at least 1\n";
		}
		if (!(dataRateValue >= 1 && dataRateValue <= 128)) {
			errorMsg += "Data Rate Range should be (1 <= value <= 128)\n";
		}
		if (!(movingAverageValue >= 0 && movingAverageValue <= 100)) {
			errorMsg += "Moving Average Range should be (0 <= value <= 100)";
		}

		//Check Input Value Error
		if (errorMsg.equals("")) {
			if (bridgeSelectChkBox0.isSelected()) {
				bridgeConnectionStatus.addConnectedList(0, true);
			}
			if (bridgeSelectChkBox1.isSelected()) {
				bridgeConnectionStatus.addConnectedList(1, true);
			}
			if (bridgeSelectChkBox2.isSelected()) {
				bridgeConnectionStatus.addConnectedList(2, true);
			}
			if (bridgeSelectChkBox3.isSelected()) {
				bridgeConnectionStatus.addConnectedList(3, true);
			}
			bridgeConnectionStatus.setDataRate(1000 / Integer.parseInt(dataRateField.getText()));
			bridgeConnectionStatus.setGain(gainField.getValue());
			bridgeConnectionStatus.setMovingAverage(Integer.parseInt(movingAverageField.getText()));

			//Close Setting Window
			settingStage.close();
		} else {
			//Show Error Alert Window
			Alert alert = new Alert(Alert.AlertType.ERROR);

			alert.setTitle("Error Message");
			alert.setHeaderText("Invalid Input Value");
			alert.setContentText(errorMsg);
			alert.showAndWait();
		}
	}

	@FXML
	public void handleCancel() {
		System.exit(1);
	}
}