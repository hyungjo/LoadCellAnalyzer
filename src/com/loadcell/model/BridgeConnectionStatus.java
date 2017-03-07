package com.loadcell.model;

import com.phidgets.BridgePhidget;

import java.util.HashMap;
import java.util.Map;

public class BridgeConnectionStatus {
	// Connected Bridge Number List
	private Map<Integer, Boolean> connectedList;

	// Sensor Gain Value
	private int gain;

	// Sensor Data Rate Value
	private int dataRate;

	// Filtering
	// Moving Average Size
	private int movingAverage;

	public BridgeConnectionStatus() {
		connectedList = new HashMap<>();
	}

	// Connected Bridge Number
	public Map<Integer, Boolean> getConnectedList() {
		return this.connectedList;
	}
	public void addConnectedList(int bridgeNumber, boolean status) {
		connectedList.put(bridgeNumber, status);
	}

	// Sensor Gain Value
	public int getGain() {
		switch (this.gain) {
			case 1:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_1;
			case 8:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_8;
			case 16:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_16;
			case 32:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_32;
			case 64:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_64;
			case 128:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_128;
			default:
				return BridgePhidget.PHIDGET_BRIDGE_GAIN_UNKNOWN;
		}
	}
	public void setGain(int gain) {
		this.gain = gain;
	}

	// Sensor Data Rate Value
	public int getDataRate() {
		return dataRate;
	}
	public void setDataRate(int dataRate) {
		this.dataRate = dataRate;
	}

	// Filter
	// Moving Average
	public int getMovingAverage() {
		return movingAverage;
	}
	public void setMovingAverage(int movingAverage) {
		this.movingAverage = movingAverage;
	}
}
