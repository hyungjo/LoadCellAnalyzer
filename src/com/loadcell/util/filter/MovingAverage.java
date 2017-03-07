package com.loadcell.util.filter;

import java.util.LinkedList;

public class MovingAverage {
	private int size;
	private double average;

	private LinkedList<Double> queue;

	public MovingAverage(int size) {
		setSize(size);
		queue = new LinkedList<Double>();
	}

	public double next(double value) {
		if(queue.size() < size - 1) {
			double sum = 0;

			queue.offer(value);
			for(double node : queue) {
				sum += node;
			}

			setAverage(sum / queue.size());

			return getAverage();
		} else {
			double sum = 0;

			queue.offer(value);
			for(double node : queue) {
				sum += node;
			}

			setAverage(sum / size);

			queue.poll();

			return getAverage();
		}
	}
	public void setSize(int size) {
		this.size = size;
	}

	public double getAverage() {
		return this.average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

}
