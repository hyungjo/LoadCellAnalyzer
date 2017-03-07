package com.loadcell.util.sensor;

import com.loadcell.model.BridgeConnectionStatus;
import com.loadcell.model.BridgeData;
import com.phidgets.BridgePhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.BridgeDataEvent;
import com.phidgets.event.BridgeDataListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;

import java.util.HashMap;
import java.util.Map;

public class Sensor {
	public static final int INDEX_OF_BRIDGE_NUMBER = 45;
	public static final int INDEX_OF_BRIDGE_VALUE = 64;

	private BridgeConnectionStatus bridgeConnectionStatus;

	private Map<Integer, BridgeData> bridgeDataQueueList;

	private BridgePhidget bridge;
	private ErrorListener ErrorListener;
	private BridgeDataListener BridgeDataListener;

	public Sensor(BridgeConnectionStatus bridgeConnectionStatus) {
		this.bridgeDataQueueList = new HashMap<>();
		this.bridgeConnectionStatus = bridgeConnectionStatus;

		for (Map.Entry<Integer, Boolean> connectedBridge : this.bridgeConnectionStatus.getConnectedList().entrySet()) {
			final BridgeData bridgeData = new BridgeData(this.bridgeConnectionStatus);
			bridgeDataQueueList.put(connectedBridge.getKey(), bridgeData);
			System.out.println("Added BridgeData: " + connectedBridge.getKey());
		}

		initSensor();
	}

	public Map<Integer, BridgeData> getBridgeDataQueueList() {
		return this.bridgeDataQueueList;
	}

	public void initSensor() {
		try {
			bridge = new BridgePhidget();
			bridge.openAny();

			System.out.println("Waiting for the Phidget Bridge to be attached...");
			bridge.waitForAttachment();

			bridge.addErrorListener(new ErrorListener() {
				public void error(ErrorEvent ex) {
					System.out.println("\n--->Error: " + ex.getException());
				}
			});

//			System.out.println("Phidget Information");
//			System.out.println("====================================");
//			System.out.println("Version: " + bridge.getDeviceVersion());
//			System.out.println("Name: " + bridge.getDeviceName());
//			System.out.println("Serial #: " + bridge.getSerialNumber());
//			System.out.println("# Bridges: " + bridge.getInputCount());

			//setting all bridge enable states to true
			for (Map.Entry<Integer, Boolean> connectedBridge : this.bridgeConnectionStatus.getConnectedList().entrySet()) {
				final int bridgeNumber = connectedBridge.getKey();
				System.out.println("Setting the enable state of bridge " + bridgeNumber + " to true");
				bridge.setEnabled(bridgeNumber, true);

				System.out.println("Setting the gain of bridge " + bridgeNumber + " to " + bridgeConnectionStatus.getGain());
				bridge.setGain(bridgeNumber, bridgeConnectionStatus.getGain());

			}

			bridge.setDataRate(bridgeConnectionStatus.getDataRate());

			bridge.addBridgeDataListener(new BridgeDataListener() {
				public void bridgeData(BridgeDataEvent bde) {
					if (bde != null) {
						// System.out.println(bde.toString());
						final int bridgeNumber = Integer.parseInt(bde.toString().substring(INDEX_OF_BRIDGE_NUMBER, INDEX_OF_BRIDGE_NUMBER + 1));
						final double bridgeValue = Double.parseDouble(bde.toString().substring(INDEX_OF_BRIDGE_VALUE, bde.toString().length()));

						getBridgeDataQueueList().get(bridgeNumber).putBridgeDataQueue(bridgeValue);
					}
				}
			});
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
	}

	public void disconnection() throws PhidgetException {
		//closing
		bridge.removeBridgeDataListener(BridgeDataListener);
		bridge.removeErrorListener(ErrorListener);
		bridge.close();
		bridge = null;
		System.out.println("\nTurning off Phidget Bridge");
	}
}