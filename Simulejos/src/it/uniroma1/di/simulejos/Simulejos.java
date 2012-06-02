package it.uniroma1.di.simulejos;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

public final class Simulejos extends JFrame {
	private static final long serialVersionUID = 1344391485057572344L;

	private final Simulation simulation = new Simulation();
	private volatile File file = null;

	private final JFileChooser fileChooser = new JFileChooser();

	private boolean reset() {
		if (simulation.isDirty()) {
			// TODO
			return false;
		}
		simulation.reset();
		return true;
	}

	private void save() {
		// TODO
	}

	private void saveAs() {
		// TODO
	}

	public final Action NEW_ACTION = new AbstractAction("New") {
		private static final long serialVersionUID = -4726361137806256305L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	};
	public final Action LOAD_ACTION = new AbstractAction("Load...") {
		private static final long serialVersionUID = 5108135153655697921L;

		@Override
		public void actionPerformed(ActionEvent e) {
			reset();
		}
	};
	public final Action SAVE_ACTION = new AbstractAction("Save") {
		private static final long serialVersionUID = -1829243020102401543L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (file != null) {
				saveAs();
			} else {
				save();
			}
		}
	};
	public final Action SAVE_AS_ACTION = new AbstractAction("Save as...") {
		private static final long serialVersionUID = -2297922078695549898L;

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
		}
	};
	public final Action EXIT_ACTION = new AbstractAction("Exit") {
		private static final long serialVersionUID = -1289143153929543605L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (reset()) {
				dispose();
			}
		}
	};

	private Simulejos() {
		super("Simulejos");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		final JMenu simulationMenu = new JMenu("Simulation");
		simulationMenu.add(NEW_ACTION);
		simulationMenu.add(LOAD_ACTION);
		simulationMenu.add(SAVE_ACTION);
		simulationMenu.add(SAVE_AS_ACTION);
		simulationMenu.addSeparator();
		simulationMenu.add(EXIT_ACTION);
		menuBar.add(simulationMenu);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	public static void main(String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Simulejos();
			}
		});
	}
}
