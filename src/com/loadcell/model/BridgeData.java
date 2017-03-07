package com.loadcell.model;

import com.loadcell.util.filter.MovingAverage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BridgeData {
	// Bridge Data Queue Size
	private final static int BLOCKING_QUEUE_SIZE = 1000;

	// Bridge Data Queue
	private BlockingQueue<Double> bridgeDataQueue;

	// Zeroing
	private double zeroingValue;
	private double zeroingMVValue;
	private boolean zeroingFlag;

	// Calibration
	private double calibrationBeforeValue;
	private double calibrationBeforeMVValue;
	private boolean calibrationBeforeFlag;
	private double calibrationAfterValue;
	private double calibrationAfterMVValue;
	private boolean calibrationAfterFlag;

	// Data Rate
	private long dataRate;

	// Filter
	// Moving Average
	private MovingAverage movingAverage;

	public BridgeData(BridgeConnectionStatus bridgeConnectionStatus) {
		// Initialize Bridge Data Queue
		bridgeDataQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);

		//Filter
		//Moving Average
		movingAverage = new MovingAverage(bridgeConnectionStatus.getMovingAverage());

		// Initialize Data Rate
		setDataRate(bridgeConnectionStatus.getDataRate());
	}

	// Bridge Data Queue
	public BlockingQueue<Double> getBridgeDataQueue() {
		return this.bridgeDataQueue;
	}

	// Zeroing
	public double getZeroingValue() {
		return zeroingValue;
	}
	public void setZeroingValue(double zeroingValue) {
		this.zeroingValue = zeroingValue;
	}
	public double getZeroingMVValue() {
		return zeroingMVValue;
	}
	public void setZeroingMVValue(double zeroingMVValue) {
		this.zeroingMVValue = zeroingMVValue;
	}
	public boolean isZeroingFlag() {
		return zeroingFlag;
	}
	public void setZeroingFlag(boolean zeroingFlag) {
		this.zeroingFlag = zeroingFlag;
	}

	// Calibration
	public double getCalibrationBeforeValue() {
		return calibrationBeforeValue;
	}
	public void setCalibrationBeforeValue(double calibrationBeforeValue) {
		this.calibrationBeforeValue = calibrationBeforeValue;
	}
	public double getCalibrationBeforeMVValue() {
		return calibrationBeforeMVValue;
	}
	public void setCalibrationBeforeMVValue(double calibrationBeforeMVValue) {
		this.calibrationBeforeMVValue = calibrationBeforeMVValue;
	}
	public boolean isCalibrationBeforeFlag() {
		return calibrationBeforeFlag;
	}
	public void setCalibrationBeforeFlag(boolean calibrationBeforeFlag) {
		this.calibrationBeforeFlag = calibrationBeforeFlag;
	}
	public double getCalibrationAfterValue() {
		return calibrationAfterValue;
	}
	public void setCalibrationAfterValue(double calibrationAfterValue) {
		this.calibrationAfterValue = calibrationAfterValue;
	}
	public double getCalibrationAfterMVValue() {
		return calibrationAfterMVValue;
	}
	public void setCalibrationAfterMVValue(double calibrationAfterMVValue) {
		this.calibrationAfterMVValue = calibrationAfterMVValue;
	}
	public boolean isCalibrationAfterFlag() {
		return calibrationAfterFlag;
	}
	public void setCalibrationAfterFlag(boolean calibrationAfterFlag) {
		this.calibrationAfterFlag = calibrationAfterFlag;
	}

	// Data Rate
	public long getDataRate() {
		return dataRate;
	}
	public void setDataRate(long dataRate) {
		this.dataRate = dataRate;
	}

	public double filteringZeroing(double bridgeValue) {
		double zeroingMVValue = getZeroingMVValue();

		if (isZeroingFlag()) {
			return bridgeValue - zeroingMVValue;
		} else {
			return bridgeValue;
		}
	}

	public double filteringCalibration(double bridgeValue) {
		// W = Weight (Known Real Weight Value)
		// MV = mv/V  (Real Bridge Value)
		// 1 = Before, 2 = After
		double w1 = getCalibrationBeforeValue();
		double w2 = getCalibrationAfterValue();
		double mv1 = getCalibrationBeforeMVValue();
		double mv2 = getCalibrationAfterMVValue();

		// If Before & After Calibration Set
		if (isCalibrationBeforeFlag() && isCalibrationAfterFlag()) {
			// Regression Equation
			return ((w2 - w1) / (mv2 - mv1)) * bridgeValue + ((w1 * mv2 - w2 * mv1) / (mv2 - mv1));
		} else {
			return bridgeValue;
		}
	}

	public void putBridgeDataQueue(double bridgeValue) {
		try {
			this.bridgeDataQueue.put(-1 * movingAverage.next(bridgeValue));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Double takeBridgeDataQueue() {
		try {
			return this.bridgeDataQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public double peekBridgeDataQueue() {
		while (true) {
			Double peeked = bridgeDataQueue.peek();
			if (peeked != null)
				return peeked;
		}
	}

	public double takeZeroingBridgeDataQueue() {
		if (isZeroingFlag()) {
			return filteringZeroing(takeBridgeDataQueue());
		} else {
			return takeBridgeDataQueue();
		}
	}

	public double peekZeroingBridgeDataQueue() {
		if (isZeroingFlag()) {
			return filteringZeroing(peekBridgeDataQueue());
		} else {
			return takeBridgeDataQueue();
		}
	}

	public double takeFinalBridgeDataQueue() {
		return filteringCalibration(filteringZeroing(takeBridgeDataQueue()));
	}

	public double peekFinalBridgeDataQueue() {
		return filteringCalibration(filteringZeroing(peekBridgeDataQueue()));
	}

}
