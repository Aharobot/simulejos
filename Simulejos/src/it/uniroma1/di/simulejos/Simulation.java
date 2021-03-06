package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Program;
import it.uniroma1.di.simulejos.wavefront.ParseException;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.media.opengl.DebugGL2GL3;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.script.ScriptException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static javax.media.opengl.GL2GL3.*;

public final class Simulation {
	private static volatile boolean debugMode;

	public static void setDebugMode(boolean debug) {
		Simulation.debugMode = debug;
	}

	public final Camera camera;
	public final Floor floor = new Floor();
	private final Clock clock = new Clock();
	private final List<Robot> robotList = new LinkedList<>();
	public final Iterable<Robot> robots = robotList;
	public final Picker picker;
	private volatile boolean dirty;

	private final Frame parentWindow;
	private final GLJPanel canvas;
	private final Writer logWriter;

	private volatile Thread ticker;
	private volatile PrintWriter simulationLogWriter;

	private volatile Program robotProgram;

	private volatile boolean fastForward;

	private static GL2GL3 getGL(GLAutoDrawable drawable) {
		final GL2GL3 gl = drawable.getGL().getGL2GL3();
		if (debugMode) {
			return new DebugGL2GL3(gl);
		} else {
			return gl;
		}
	}

	public Simulation(Frame parentWindow, GLJPanel canvas, Writer logWriter) {
		this.parentWindow = parentWindow;
		this.logWriter = logWriter;
		this.simulationLogWriter = new PrintWriter(new PartialWriter(
				"Simulation", logWriter));
		this.canvas = canvas;
		this.camera = new Camera(canvas);
		this.picker = new Picker(camera, robots);
		canvas.addGLEventListener(new GLEventListener() {
			@Override
			public void init(GLAutoDrawable drawable) {
				final GL2GL3 gl = getGL(drawable);
				gl.glEnable(GL_DEPTH_TEST);
				gl.glClearDepth(0);
				gl.glDepthFunc(GL_GREATER);
				gl.glEnable(GL_CULL_FACE);
				robotProgram = new Program(gl, Robot.class, "robot",
						new String[] { "in_Vertex" });
				floor.init(gl);
				for (Robot robot : robots) {
					robot.init(gl);
				}
			}

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {
				final GL2GL3 gl = getGL(drawable);
				if (height > width) {
					gl.glViewport((width - height) / 2, 0, height, height);
				} else {
					gl.glViewport(0, (height - width) / 2, width, width);
				}
			}

			@Override
			public void display(GLAutoDrawable drawable) {
				final GL2GL3 gl = getGL(drawable);
				gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT
						| GL2GL3.GL_DEPTH_BUFFER_BIT);
				floor.draw(gl, camera);
				robotProgram.use();
				camera.uniform(robotProgram);
				for (Robot robot : robots) {
					robot.draw(gl, robotProgram);
				}
				gl.glFlush();
			}

			@Override
			public void dispose(GLAutoDrawable drawable) {
				robotProgram.delete();
			}
		});
		canvas.addGLEventListener(picker.new CanvasHandler());
	}

	public boolean isDirty() {
		return dirty;
	}

	public void clearDirty() {
		dirty = false;
	}

	public Robot addRobot(File classPath, String mainClassName, String script,
			File modelFile, boolean swapYAndZ) throws IOException,
			ParseException, ScriptException {
		dirty = true;
		final Robot robot = new Robot(parentWindow, canvas, logWriter, clock,
				classPath, mainClassName, script, ModelData.parseWavefront(
						modelFile, swapYAndZ), floor, robots);
		robotList.add(robot);
		canvas.repaint();
		return robot;
	}

	public void removeRobot(Robot robot) {
		dirty = true;
		robotList.remove(robot);
		canvas.repaint();
	}

	private interface State {
		State play() throws ScriptException;

		State fastForward() throws ScriptException;

		State suspend();

		State stop();
	};

	private final State runningState = new State() {
		@Override
		public State play() {
			return this;
		}

		@Override
		public State fastForward() {
			fastForward = true;
			simulationLogWriter.println("fast forward");
			return fastForwardState;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State suspend() {
			ticker.suspend();
			clock.suspend();
			for (Robot robot : robots) {
				robot.suspend();
			}
			simulationLogWriter.println("suspended");
			return suspendedState;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State stop() {
			ticker.stop();
			for (Robot robot : robots) {
				robot.stop();
			}
			simulationLogWriter.println("stopped");
			return stoppedState;
		}
	};

	private final State fastForwardState = new State() {
		@Override
		public State play() {
			fastForward = false;
			simulationLogWriter.println("normal speed");
			return runningState;
		}

		@Override
		public State fastForward() {
			return this;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State suspend() {
			ticker.suspend();
			clock.suspend();
			for (Robot robot : robots) {
				robot.suspend();
			}
			simulationLogWriter.println("suspended");
			canvas.repaint();
			return suspendedState;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State stop() {
			ticker.stop();
			for (Robot robot : robots) {
				robot.stop();
			}
			fastForward = false;
			canvas.repaint();
			simulationLogWriter.println("stopped");
			return stoppedState;
		}
	};

	private final State suspendedState = new State() {
		@SuppressWarnings("deprecation")
		@Override
		public State play() {
			fastForward = false;
			simulationLogWriter.println("resumed");
			for (Robot robot : robots) {
				robot.resume();
			}
			clock.resume();
			ticker.resume();
			return runningState;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State fastForward() {
			fastForward = true;
			simulationLogWriter.println("resumed in fast forward");
			for (Robot robot : robots) {
				robot.resume();
			}
			clock.resume();
			ticker.resume();
			return fastForwardState;
		}

		@Override
		public State suspend() {
			return this;
		}

		@SuppressWarnings("deprecation")
		@Override
		public State stop() {
			ticker.stop();
			for (Robot robot : robots) {
				robot.stop();
			}
			simulationLogWriter.println("stopped");
			return stoppedState;
		}
	};

	private final State stoppedState = new State() {
		final class Ticker extends Thread {
			private final Object blocker = new Object();
			private volatile long lastTimestamp;

			public Ticker() {
				super("ticker");
			}

			private void advanceTo(int period) throws InterruptedException {
				long elapsed;
				synchronized (blocker) {
					if ((elapsed = System.currentTimeMillis() - lastTimestamp) < period) {
						clock.advance(period - (int) elapsed);
					}
					lastTimestamp = System.currentTimeMillis();
				}
			}

			private void waitTo(int period) throws InterruptedException {
				long elapsed;
				synchronized (blocker) {
					while ((elapsed = System.currentTimeMillis()
							- lastTimestamp) < period) {
						blocker.wait(period - elapsed);
					}
					lastTimestamp = System.currentTimeMillis();
				}
			}

			private void step() throws Exception {
				final long timestamp = clock.getTimestamp();
				for (Robot robot : robots) {
					robot.tick(timestamp);
					for (Robot robot2 : robots) {
						if ((robot != robot2) && robot.collidesWith(robot2)) {
							final String message = "NXT" + robot.index
									+ " collided with NXT" + robot2.index;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									Simulation.this.stop();
									JOptionPane.showMessageDialog(parentWindow,
											message, "Simulejos",
											JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				}
			}

			@Override
			public void run() {
				final int rate = Preferences.userNodeForPackage(
						Simulation.class).getInt("rate", 60);
				final int period = (int) Math.round(1000.0 / rate);
				lastTimestamp = System.currentTimeMillis();
				while (true) {
					try {
						if (fastForward) {
							advanceTo(period);
						} else {
							waitTo(period);
						}
						step();
					} catch (Exception e) {
						e.printStackTrace();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Simulation.this.stop();
							}
						});
					}
					if (!fastForward) {
						canvas.repaint();
					}
				}
			}
		}

		@Override
		public State play() throws ScriptException {
			simulationLogWriter.println("started");
			ticker = new Ticker();
			for (Robot robot : robots) {
				robot.play();
			}
			fastForward = false;
			ticker.start();
			return runningState;
		}

		@Override
		public State fastForward() throws ScriptException {
			simulationLogWriter.println("fast forward");
			ticker = new Ticker();
			for (Robot robot : robots) {
				robot.play();
			}
			fastForward = true;
			ticker.start();
			return fastForwardState;
		}

		@Override
		public State suspend() {
			return this;
		}

		@Override
		public State stop() {
			return this;
		}
	};

	private volatile State state = stoppedState;

	public void play() throws ScriptException {
		state = state.play();
	}

	public void fastForward() throws ScriptException {
		state = state.fastForward();
	}

	public void suspend() {
		state = state.suspend();
	}

	public void stop() {
		state = state.stop();
	}

	public boolean isRunning() {
		return state != stoppedState;
	}

	public boolean isSuspended() {
		return state == suspendedState;
	}
}
