package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;

final class Motor implements SimulatorInterface.Motor {
	private static final int RPM = 160;
	private static final long ONE_MINUTE_IN_NANOSECONDS = 60000000000l;

	public static final class Timer {
		private volatile long offset;
		private volatile boolean suspended;
		private volatile long suspendTimestamp;

		private Timer() {
		}

		public synchronized long getTimestamp() {
			if (suspended) {
				return suspendTimestamp - offset;
			} else {
				return System.nanoTime() - offset;
			}
		}

		public synchronized void suspend() {
			if (!suspended) {
				suspended = true;
				suspendTimestamp = System.nanoTime();
			}
		}

		public synchronized void resume() {
			if (suspended) {
				suspended = false;
				offset += System.nanoTime() - suspendTimestamp;
			}
		}
	}

	public final Timer timer = new Timer();

	private volatile Mode mode = Mode.FLOAT;
	private volatile int power;
	private volatile double count;
	private volatile double offset;
	private volatile long lastTimestamp = timer.getTimestamp();

	private volatile double lastSample;

	@Override
	public Mode getMode() {
		return mode;
	}

	@Override
	public void setMode(Mode mode) {
		control(this.power, mode);
	}

	@Override
	public int getPower() {
		return power;
	}

	@Override
	public void setPower(int power) {
		control(power, this.mode);
	}

	private double delta(long timestamp) {
		if (mode == Mode.FORWARD) {
			return (timestamp - lastTimestamp) * (power * RPM / 100.0)
					/ (double) ONE_MINUTE_IN_NANOSECONDS;
		} else if (mode == Mode.BACKWARD) {
			return (lastTimestamp - timestamp) * (power * RPM / 100.0)
					/ (double) ONE_MINUTE_IN_NANOSECONDS;
		} else {
			return 0;
		}
	}

	private double delta() {
		return delta(timer.getTimestamp());
	}

	@Override
	public synchronized void control(int power, Mode mode) {
		final long timestamp = timer.getTimestamp();
		count += delta(timestamp);
		lastTimestamp = timestamp;
		this.power = Math.max(Math.min(power, 100), 0);
		this.mode = mode;
	}

	private double getInternalCount() {
		return count + delta();
	}

	@Override
	public synchronized int getCount() {
		return (int) Math.round((getInternalCount() + offset) * 360);
	}

	@Override
	public synchronized void resetCount() {
		offset = -getInternalCount();
	}

	public synchronized double sample() {
		final double count = getInternalCount();
		final double result = count - lastSample;
		lastSample = count;
		return result;
	}
}
