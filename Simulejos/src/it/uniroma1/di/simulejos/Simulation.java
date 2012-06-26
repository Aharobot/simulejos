package it.uniroma1.di.simulejos;

import java.awt.Frame;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public final class Simulation implements Serializable {
	private static final long serialVersionUID = -290517947218502549L;

	private final List<Robot> robots = new LinkedList<>();

	private transient volatile Frame parentWindow;
	private transient volatile PrintWriter logWriter;
	private transient volatile boolean dirty = false;

	public Simulation(Frame parentWindow, Writer logWriter) {
		this.parentWindow = parentWindow;
		this.logWriter = new PrintWriter(new PartialWriter("Simulation",
				logWriter));
	}

	public boolean isDirty() {
		return dirty;
	}

	public void clearDirty() {
		dirty = false;
	}

	public void addRobot(File classPath, String mainClassName, String script) {
		dirty = true;
		robots.add(new Robot(classPath, mainClassName, script, parentWindow,
				logWriter));
	}

	// TODO state machine

	public void play() {
		logWriter.println("started");
		for (Robot robot : robots) {
			robot.play();
		}
	}

	public void suspend() {
		for (Robot robot : robots) {
			robot.suspend();
		}
		logWriter.println("suspended");
	}

	public void resume() {
		logWriter.println("resumed");
		for (Robot robot : robots) {
			robot.resume();
		}
	}

	public void stop() {
		for (Robot robot : robots) {
			robot.stop();
		}
		logWriter.println("stopped");
	}
}
