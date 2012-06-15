package it.uniroma1.di.simulejos;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

final class Simulation implements Serializable {
	private static final long serialVersionUID = -290517947218502549L;

	private final List<Robot> robots = new LinkedList<>();

	private transient volatile boolean dirty = false;

	boolean isDirty() {
		return dirty;
	}

	void clearDirty() {
		dirty = false;
	}

	void addRobot(File classPath, String mainClassName) {
		dirty = true;
		robots.add(new Robot(classPath, mainClassName));
	}

	void play() {
		for (Robot robot : robots) {
			robot.play();
		}
	}

	void suspend() {
		for (Robot robot : robots) {
			robot.suspend();
		}
	}

	void resume() {
		for (Robot robot : robots) {
			robot.resume();
		}
	}

	void stop() {
		for (Robot robot : robots) {
			robot.stop();
		}
	}
}
