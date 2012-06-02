package it.uniroma1.di.simulejos;

import java.io.InputStream;
import java.io.OutputStream;

final class Simulation {
	private volatile boolean dirty = false;

	boolean isDirty() {
		return dirty;
	}

	void clearDirty() {
		dirty = false;
	}

	void reset() {
		dirty = false;
	}

	void load(InputStream is) {
		// TODO
	}

	void store(OutputStream os) {
		// TODO
	}
}
