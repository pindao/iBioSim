package reb2sac.core.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.sbml.libsbml.*;
import sbmleditor.core.gui.*;
import graph.core.gui.*;
import biomodelsim.core.gui.*;
import buttons.core.gui.*;

/**
 * This class creates a GUI for the reb2sac program. It implements the
 * ActionListener class, the KeyListener class, the MouseListener class, and the
 * Runnable class. This allows the GUI to perform actions when buttons are
 * pressed, text is entered into a field, or the mouse is clicked. It also
 * allows this class to execute many reb2sac programs at the same time on
 * different threads.
 * 
 * @author Curtis Madsen
 */
public class Reb2Sac extends JPanel implements ActionListener, Runnable, MouseListener {

	private static final long serialVersionUID = 3181014495993143825L;

	private JTextField amountTerm; // Amount for termination condition

	/*
	 * Buttons for adding and removing conditions and species
	 */
	private JButton addIntSpecies, removeIntSpecies, addTermCond, removeTermCond, clearIntSpecies,
			clearTermCond;

	/*
	 * Radio Buttons that represent the different abstractions
	 */
	private JRadioButton none, abstraction, nary, ODE, monteCarlo, markov;

	private JRadioButton sbml, dot, xhtml; // Radio Buttons output option

	/*
	 * Radio Buttons for termination conditions
	 */
	private JRadioButton ge, gt, eq, le, lt;

	private JButton run, save, saveAs; // The save and run button

	/*
	 * Added interesting species and termination conditions
	 */
	private JList species, terminations;

	private JList intSpecies, termCond; // List of species in sbml file

	private JLabel spLabel, speciesLabel; // Labels for interesting species

	/*
	 * Text fields for changes in the abstraction
	 */
	private JTextField limit, interval, step, seed, runs;

	/*
	 * Labels for the changes in the abstraction
	 */
	private JLabel limitLabel, intervalLabel, stepLabel, seedLabel, runsLabel;

	/*
	 * Description of selected simulator
	 */
	private JLabel description, explanation;

	/*
	 * List of interesting species
	 */
	private Object[] interestingSpecies = new Object[0];

	/*
	 * List of species with termination conditions
	 */
	private Object[] termConditions = new Object[0];

	private JComboBox simulators; // Combo Box for possible simulators

	private JLabel simulatorsLabel; // Label for possible simulators

	private JTextField rapid1, rapid2, qssa, maxCon; // advanced options

	/*
	 * advanced labels
	 */
	private JLabel rapidLabel1, rapidLabel2, qssaLabel, maxConLabel;

	private JCheckBox usingSSA; // check box for using ssa

	private JComboBox availSpecies; // species for SSA

	private JList ssa; // list of ssa

	private JTextField time; // time for ssa

	private JTextField ssaModNum; // number that the ssa is changed by

	private JComboBox ssaMod; // amount to mod the ssa species by

	private JButton addSSA, editSSA, removeSSA; // Buttons for editing SSA

	private JButton newSSA; // Buttons for SSA file

	private JLabel timeLabel; // Label for SSA

	private Object[] ssaList; // array for ssa JList

	private JList properties; // JList for properties

	private JTextField prop, value; // text areas for properties

	private JButton addProp, removeProp; // buttons for properties

	private Object[] props; // array for properties JList

	private String sbmlFile, root; // sbml file and root directory

	private BioSim biomodelsim; // reference to the tstubd class

	private String simName; // simulation id

	private Log log; // the log

	private JTabbedPane simTab; // the simulation tab

	private SBML_Editor sbmlEditor; // sbml editor

	private JTextArea sadFile;

	private JRadioButton overwrite, append;

	private JLabel choose3;

	private boolean runFiles;

	private String separator;

	/**
	 * This is the constructor for the GUI. It initializes all the input fields,
	 * puts them on panels, adds the panels to the frame, and then displays the
	 * GUI.
	 */
	public Reb2Sac(String sbmlFile, String root, BioSim biomodelsim, String simName, Log log,
			JTabbedPane simTab, String open) {
		if (File.separator.equals("\\")) {
			separator = "\\\\";
		} else {
			separator = File.separator;
		}

		this.biomodelsim = biomodelsim;
		this.sbmlFile = sbmlFile;
		this.root = root;
		this.simName = simName;
		this.log = log;
		this.simTab = simTab;

		// Creates the input fields for the changes in abstraction
		String[] odeSimulators = new String[6];
		odeSimulators[0] = "euler";
		odeSimulators[1] = "gear1";
		odeSimulators[2] = "gear2";
		odeSimulators[3] = "rk4imp";
		odeSimulators[4] = "rk8pd";
		odeSimulators[5] = "rkf45";
		explanation = new JLabel("Description Of Selected Simulator:     ");
		description = new JLabel("Embedded Runge-Kutta-Fehlberg (4, 5) method");
		simulators = new JComboBox(odeSimulators);
		simulators.setSelectedItem("rkf45");
		simulators.addActionListener(this);
		limit = new JTextField("100.0", 39);
		interval = new JTextField("1.0", 15);
		step = new JTextField("1.0", 15);
		step.setEnabled(false);
		int next = 1;
		String filename = "sim" + next;
		while (new File(root + separator + filename).exists()) {
			next++;
			filename = "sim" + next;
		}
		// dir = new JTextField(filename, 15);
		seed = new JTextField("314159", 15);
		runs = new JTextField("1", 15);
		simulatorsLabel = new JLabel("Possible Simulators/Analyzers:");
		limitLabel = new JLabel("Time Limit:");
		intervalLabel = new JLabel("Print Interval:");
		stepLabel = new JLabel("Time Step:");
		seedLabel = new JLabel("Random Seed:");
		runsLabel = new JLabel("Runs:");
		JPanel inputHolder = new JPanel(new BorderLayout());
		JPanel inputHolderLeft = new JPanel(new GridLayout(7, 1));
		JPanel inputHolderRight = new JPanel(new GridLayout(7, 1));
		inputHolderLeft.add(simulatorsLabel);
		inputHolderRight.add(simulators);
		inputHolderLeft.add(explanation);
		inputHolderRight.add(description);
		inputHolderLeft.add(limitLabel);
		inputHolderRight.add(limit);
		inputHolderLeft.add(intervalLabel);
		inputHolderRight.add(interval);
		inputHolderLeft.add(stepLabel);
		inputHolderRight.add(step);
		inputHolderLeft.add(seedLabel);
		inputHolderRight.add(seed);
		inputHolderLeft.add(runsLabel);
		inputHolderRight.add(runs);
		inputHolder.add(inputHolderLeft, "West");
		inputHolder.add(inputHolderRight, "Center");
		JPanel topInputHolder = new JPanel();
		topInputHolder.add(inputHolder);

		// Creates the interesting species JList
		intSpecies = new JList();
		species = new JList();
		spLabel = new JLabel("Available Species:");
		speciesLabel = new JLabel("Interesting Species:");
		JPanel speciesHolder = new JPanel(new BorderLayout());
		JPanel listOfSpeciesLabelHolder = new JPanel(new GridLayout(1, 2));
		JPanel listOfSpeciesHolder = new JPanel(new GridLayout(1, 2));
		JScrollPane scroll = new JScrollPane();
		JScrollPane scroll1 = new JScrollPane();
		scroll.setMinimumSize(new Dimension(260, 200));
		scroll.setPreferredSize(new Dimension(276, 132));
		scroll.setViewportView(species);
		scroll1.setMinimumSize(new Dimension(260, 200));
		scroll1.setPreferredSize(new Dimension(276, 132));
		scroll1.setViewportView(intSpecies);
		addIntSpecies = new JButton("Add Species");
		removeIntSpecies = new JButton("Remove Species");
		clearIntSpecies = new JButton("Clear Species");
		listOfSpeciesLabelHolder.add(spLabel);
		listOfSpeciesHolder.add(scroll1);
		listOfSpeciesLabelHolder.add(speciesLabel);
		listOfSpeciesHolder.add(scroll);
		speciesHolder.add(listOfSpeciesLabelHolder, "North");
		speciesHolder.add(listOfSpeciesHolder, "Center");
		JPanel buttonHolder = new JPanel();
		buttonHolder.add(addIntSpecies);
		buttonHolder.add(removeIntSpecies);
		buttonHolder.add(clearIntSpecies);
		speciesHolder.add(buttonHolder, "South");
		intSpecies.setEnabled(false);
		species.setEnabled(false);
		intSpecies.addMouseListener(this);
		species.addMouseListener(this);
		spLabel.setEnabled(false);
		speciesLabel.setEnabled(false);
		addIntSpecies.setEnabled(false);
		removeIntSpecies.setEnabled(false);
		addIntSpecies.addActionListener(this);
		removeIntSpecies.addActionListener(this);
		clearIntSpecies.setEnabled(false);
		clearIntSpecies.addActionListener(this);

		// Sets up the radio buttons for Abstraction and Nary
		JLabel choose = new JLabel("Choose One:");
		none = new JRadioButton("None");
		abstraction = new JRadioButton("Abstraction");
		nary = new JRadioButton("Abstraction & Nary");
		ButtonGroup abs = new ButtonGroup();
		abs.add(none);
		abs.add(abstraction);
		abs.add(nary);
		none.setSelected(true);
		JPanel absAndNaryPanel = new JPanel();
		absAndNaryPanel.add(choose);
		absAndNaryPanel.add(none);
		absAndNaryPanel.add(abstraction);
		absAndNaryPanel.add(nary);
		none.addActionListener(this);
		abstraction.addActionListener(this);
		nary.addActionListener(this);

		// Sets up the radio buttons for ODE, Monte Carlo, and Markov
		JLabel choose2 = new JLabel("Choose One:");
		ODE = new JRadioButton("ODE");
		monteCarlo = new JRadioButton("Monte Carlo");
		markov = new JRadioButton("Markov");
		ODE.setSelected(true);
		markov.setEnabled(false);
		seed.setEnabled(false);
		seedLabel.setEnabled(false);
		runs.setEnabled(false);
		runsLabel.setEnabled(false);
		JPanel odeMonteAndMarkovPanel = new JPanel();
		odeMonteAndMarkovPanel.add(choose2);
		odeMonteAndMarkovPanel.add(ODE);
		odeMonteAndMarkovPanel.add(monteCarlo);
		odeMonteAndMarkovPanel.add(markov);
		ODE.addActionListener(this);
		monteCarlo.addActionListener(this);
		markov.addActionListener(this);

		// Sets up the radio buttons for output option
		sbml = new JRadioButton("sbml");
		dot = new JRadioButton("dot");
		xhtml = new JRadioButton("xhtml");
		sbml.setSelected(true);
		odeMonteAndMarkovPanel.add(sbml);
		odeMonteAndMarkovPanel.add(dot);
		odeMonteAndMarkovPanel.add(xhtml);
		sbml.addActionListener(this);
		dot.addActionListener(this);
		xhtml.addActionListener(this);
		ButtonGroup sim = new ButtonGroup();
		sim.add(ODE);
		sim.add(monteCarlo);
		sim.add(markov);
		sim.add(sbml);
		sim.add(dot);
		sim.add(xhtml);
		choose3 = new JLabel("Choose One:");
		overwrite = new JRadioButton("Overwrite");
		append = new JRadioButton("Append");
		overwrite.setSelected(true);
		overwrite.setEnabled(false);
		append.setEnabled(false);
		choose3.setEnabled(false);
		JPanel overwritePanel = new JPanel();
		overwritePanel.add(choose3);
		overwritePanel.add(overwrite);
		overwritePanel.add(append);
		overwrite.addActionListener(this);
		append.addActionListener(this);
		ButtonGroup over = new ButtonGroup();
		over.add(overwrite);
		over.add(append);

		// Puts all the radio buttons in a panel
		JPanel radioButtonPanel = new JPanel(new BorderLayout());
		radioButtonPanel.add(absAndNaryPanel, "North");
		radioButtonPanel.add(odeMonteAndMarkovPanel, "Center");
		radioButtonPanel.add(overwritePanel, "South");

		// Creates the main tabbed panel
		JPanel mainTabbedPanel = new JPanel(new BorderLayout());
		mainTabbedPanel.add(topInputHolder, "Center");
		mainTabbedPanel.add(radioButtonPanel, "North");

		// Creates the run button
		run = new JButton("Save And Run");
		save = new JButton("Save");
		saveAs = new JButton("Save As");
		JPanel runHolder = new JPanel();
		runHolder.add(save);
		save.addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		runHolder.add(run);
		run.addActionListener(this);
		run.setMnemonic(KeyEvent.VK_R);
		runHolder.add(saveAs);
		saveAs.addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_A);

		// Creates the termination conditions tab
		termCond = new JList();
		terminations = new JList();
		addTermCond = new JButton("Add Condition");
		removeTermCond = new JButton("Remove Condition");
		clearTermCond = new JButton("Clear Conditions");
		addTermCond.addActionListener(this);
		removeTermCond.addActionListener(this);
		clearTermCond.addActionListener(this);
		amountTerm = new JTextField("0.0", 15);
		JLabel spLabel2 = new JLabel("Available Species:");
		JLabel specLabel = new JLabel("Termination Conditions:");
		JScrollPane scroll2 = new JScrollPane();
		scroll2.setMinimumSize(new Dimension(260, 200));
		scroll2.setPreferredSize(new Dimension(276, 132));
		scroll2.setViewportView(termCond);
		JScrollPane scroll3 = new JScrollPane();
		scroll3.setMinimumSize(new Dimension(260, 200));
		scroll3.setPreferredSize(new Dimension(276, 132));
		scroll3.setViewportView(terminations);
		JPanel mainTermCond = new JPanel(new BorderLayout());
		JPanel termCondPanel = new JPanel(new GridLayout(1, 2));
		JPanel speciesPanel = new JPanel(new GridLayout(1, 2));
		ge = new JRadioButton(">=");
		gt = new JRadioButton(">");
		eq = new JRadioButton("=");
		le = new JRadioButton("<=");
		lt = new JRadioButton("<");
		ButtonGroup condition = new ButtonGroup();
		condition.add(gt);
		condition.add(ge);
		condition.add(eq);
		condition.add(le);
		condition.add(lt);
		gt.setSelected(true);
		JPanel termOptionsPanel = new JPanel(new BorderLayout());
		JPanel termSelectionPanel = new JPanel();
		JPanel termButtonPanel = new JPanel();
		termSelectionPanel.add(gt);
		termSelectionPanel.add(ge);
		termSelectionPanel.add(eq);
		termSelectionPanel.add(le);
		termSelectionPanel.add(lt);
		termSelectionPanel.add(amountTerm);
		termButtonPanel.add(addTermCond);
		termButtonPanel.add(removeTermCond);
		termButtonPanel.add(clearTermCond);
		termOptionsPanel.add(termSelectionPanel, "North");
		termOptionsPanel.add(termButtonPanel, "South");
		speciesPanel.add(spLabel2);
		speciesPanel.add(specLabel);
		termCondPanel.add(scroll2);
		termCondPanel.add(scroll3);
		termCond.addMouseListener(this);
		terminations.addMouseListener(this);
		mainTermCond.add(speciesPanel, "North");
		mainTermCond.add(termCondPanel, "Center");
		mainTermCond.add(termOptionsPanel, "South");

		JPanel sadTermCondPanel = new JPanel(new BorderLayout());
		sadFile = new JTextArea();
		JScrollPane scroll9 = new JScrollPane();
		scroll9.setViewportView(sadFile);
		sadTermCondPanel.add(scroll9, "Center");

		// Creates some advanced options
		JPanel advancedGrid = new JPanel(new GridLayout(8, 2));
		JPanel advanced = new JPanel(new GridLayout(2, 1));
		JPanel rapidSpace1 = new JPanel();
		JPanel rapidSpace2 = new JPanel();
		JPanel rapidSpace3 = new JPanel();
		JPanel rapidSpace4 = new JPanel();
		JPanel qssaSpace1 = new JPanel();
		JPanel qssaSpace2 = new JPanel();
		JPanel maxConSpace1 = new JPanel();
		JPanel maxConSpace2 = new JPanel();
		rapidLabel1 = new JLabel("Rapid Equilibrium Condition 1:");
		rapid1 = new JTextField("0.1", 15);
		rapidLabel2 = new JLabel("Rapid Equilibrium Condition 2:");
		rapid2 = new JTextField("0.1", 15);
		qssaLabel = new JLabel("QSSA Condition:");
		qssa = new JTextField("0.1", 15);
		maxConLabel = new JLabel("Max Concentration Threshold:");
		maxCon = new JTextField("15", 15);
		maxConLabel.setEnabled(false);
		maxCon.setEnabled(false);
		qssaLabel.setEnabled(false);
		qssa.setEnabled(false);
		rapidLabel1.setEnabled(false);
		rapid1.setEnabled(false);
		rapidLabel2.setEnabled(false);
		rapid2.setEnabled(false);
		advancedGrid.add(rapidLabel1);
		advancedGrid.add(rapid1);
		advancedGrid.add(rapidSpace1);
		advancedGrid.add(rapidSpace2);
		advancedGrid.add(rapidLabel2);
		advancedGrid.add(rapid2);
		advancedGrid.add(rapidSpace3);
		advancedGrid.add(rapidSpace4);
		advancedGrid.add(qssaLabel);
		advancedGrid.add(qssa);
		advancedGrid.add(qssaSpace1);
		advancedGrid.add(qssaSpace2);
		advancedGrid.add(maxConLabel);
		advancedGrid.add(maxCon);
		advancedGrid.add(maxConSpace1);
		advancedGrid.add(maxConSpace2);
		advanced.add(advancedGrid);
		JPanel space = new JPanel();
		advanced.add(space);

		// Creates the ssa with user defined species level update feature tab
		JPanel ssaPanel = new JPanel(new BorderLayout());
		newSSA = new JButton("Clear");
		newSSA.setEnabled(false);
		newSSA.addActionListener(this);
		usingSSA = new JCheckBox("Use User Defined Data File");
		usingSSA.setSelected(false);
		usingSSA.addActionListener(this);
		ssa = new JList();
		ssa.setEnabled(false);
		ssa.addMouseListener(this);
		ssaList = new Object[0];
		JScrollPane scroll5 = new JScrollPane();
		scroll5.setMinimumSize(new Dimension(260, 200));
		scroll5.setPreferredSize(new Dimension(276, 132));
		scroll5.setViewportView(ssa);
		JPanel ssaAddPanel = new JPanel(new BorderLayout());
		JPanel ssaAddPanel1 = new JPanel();
		JPanel ssaAddPanel2 = new JPanel();
		JPanel ssaAddPanel3 = new JPanel();
		timeLabel = new JLabel("At time step: ");
		timeLabel.setEnabled(false);
		time = new JTextField(15);
		time.setEnabled(false);
		availSpecies = new JComboBox();
		availSpecies.setEnabled(false);
		String[] mod = new String[5];
		mod[0] = "goes to";
		mod[1] = "is added by";
		mod[2] = "is subtracted by";
		mod[3] = "is multiplied by";
		mod[4] = "is divided by";
		ssaMod = new JComboBox(mod);
		ssaMod.setEnabled(false);
		ssaModNum = new JTextField(15);
		ssaModNum.setEnabled(false);
		ssaAddPanel1.add(timeLabel);
		ssaAddPanel1.add(time);
		ssaAddPanel1.add(availSpecies);
		ssaAddPanel2.add(ssaMod);
		ssaAddPanel2.add(ssaModNum);
		addSSA = new JButton("Add");
		addSSA.setEnabled(false);
		addSSA.addActionListener(this);
		editSSA = new JButton("Edit");
		editSSA.setEnabled(false);
		editSSA.addActionListener(this);
		removeSSA = new JButton("Remove");
		removeSSA.setEnabled(false);
		removeSSA.addActionListener(this);
		ssaAddPanel3.add(addSSA);
		ssaAddPanel3.add(editSSA);
		ssaAddPanel3.add(removeSSA);
		ssaAddPanel3.add(newSSA);
		ssaAddPanel.add(ssaAddPanel1, "North");
		ssaAddPanel.add(ssaAddPanel2, "Center");
		ssaAddPanel.add(ssaAddPanel3, "South");
		ssaPanel.add(usingSSA, "North");
		ssaPanel.add(scroll5, "Center");
		ssaPanel.add(ssaAddPanel, "South");
		if (new File(root + separator + simName + separator + "user-defined.dat").exists()) {
			String getData = "";
			try {
				Scanner scan = new Scanner(new File(root + separator + simName + separator
						+ "user-defined.dat"));
				while (scan.hasNextLine()) {
					String get = scan.nextLine();
					if (get.split(" ").length == 3) {
						if (scan.hasNextLine()) {
							getData += get + "\n";
						} else {
							getData += get;
						}
					} else {
						JOptionPane.showMessageDialog(biomodelsim.frame(),
								"Unable to load user defined file!", "Error Loading File",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(biomodelsim.frame(),
						"Unable to load user defined file!", "Error Loading File",
						JOptionPane.ERROR_MESSAGE);
			}
			if (!getData.equals("")) {
				ssaList = getData.split("\n");
			} else {
				ssaList = new Object[0];
			}
			ssa.setListData(ssaList);
			ssa.setEnabled(true);
			timeLabel.setEnabled(true);
			time.setEnabled(true);
			availSpecies.setEnabled(true);
			ssaMod.setEnabled(true);
			ssaModNum.setEnabled(true);
			addSSA.setEnabled(true);
			editSSA.setEnabled(true);
			removeSSA.setEnabled(true);
		}

		// Creates tab for adding properties
		JPanel propertiesPanel = new JPanel(new BorderLayout());
		JPanel propertiesPanel1 = new JPanel(new BorderLayout());
		JPanel propertiesPanel2 = new JPanel(new BorderLayout());
		JPanel propertiesInput = new JPanel();
		JPanel propertiesButtons = new JPanel();
		JLabel propertiesLabel = new JLabel("Properties To Add:");
		JLabel propLabel = new JLabel("Property");
		JLabel valueLabel = new JLabel("Value");
		properties = new JList();
		properties.addMouseListener(this);
		JScrollPane scroll6 = new JScrollPane();
		scroll6.setMinimumSize(new Dimension(260, 200));
		scroll6.setPreferredSize(new Dimension(276, 132));
		scroll6.setViewportView(properties);
		props = new Object[0];
		prop = new JTextField(20);
		value = new JTextField(20);
		addProp = new JButton("Add");
		addProp.addActionListener(this);
		removeProp = new JButton("Remove");
		removeProp.addActionListener(this);
		propertiesInput.add(propLabel);
		propertiesInput.add(prop);
		propertiesInput.add(valueLabel);
		propertiesInput.add(value);
		propertiesButtons.add(addProp);
		propertiesButtons.add(removeProp);
		propertiesPanel2.add(propertiesInput, "Center");
		propertiesPanel2.add(propertiesButtons, "South");
		propertiesPanel1.add(propertiesLabel, "North");
		propertiesPanel1.add(scroll6, "Center");
		propertiesPanel.add(propertiesPanel1, "Center");
		propertiesPanel.add(propertiesPanel2, "South");

		// Creates the tabs and adds them to the main panel
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Options", mainTabbedPanel);
		tab.addTab("Termination Conditions", mainTermCond);
		tab.addTab("Advanced Termination Conditions", sadTermCondPanel);
		tab.addTab("Interesting Species", speciesHolder);
		tab.addTab("User Defined Data File", ssaPanel);
		tab.addTab("Properties", propertiesPanel);
		tab.addTab("Advanced Options", advanced);
		this.setLayout(new BorderLayout());
		this.add(tab, "Center");
		this.add(runHolder, "South");
		SBMLReader reader = new SBMLReader();
		SBMLDocument document = reader.readSBML(sbmlFile);
		Model model = document.getModel();
		ArrayList<String> listOfSpecs = new ArrayList<String>();
		if (model != null) {
			ListOf listOfSpecies = model.getListOfSpecies();
			for (int i = 0; i < model.getNumSpecies(); i++) {
				Species species = (Species) listOfSpecies.get(i);
				listOfSpecs.add(species.getId());
			}
		}
		Object[] list = listOfSpecs.toArray();
		for (int i = 1; i < list.length; i++) {
			String index = (String) list[i];
			int j = i;
			while ((j > 0) && ((String) list[j - 1]).compareToIgnoreCase(index) > 0) {
				list[j] = list[j - 1];
				j = j - 1;
			}
			list[j] = index;
		}
		intSpecies.setListData(list);
		termCond.setListData(list);
		int rem = availSpecies.getItemCount();
		for (int i = 0; i < rem; i++) {
			availSpecies.removeItemAt(0);
		}
		for (int i = 0; i < list.length; i++) {
			availSpecies.addItem(((String) list[i]).replace(" ", "_"));
		}
		runFiles = false;
		String[] searchForRunFiles = new File(root + separator + simName).list();
		for (String s : searchForRunFiles) {
			if (s.length() > 3 && s.substring(0, 4).equals("run-")) {
				runFiles = true;
			}
		}
		if (open != null) {
			open(open);
		}
	}

	/**
	 * This method performs different functions depending on what buttons are
	 * pushed and what input fields contain data.
	 */
	public void actionPerformed(ActionEvent e) {
		// if the none Radio Button is selected
		if (e.getSource() == none) {
			Button_Enabling.enableNoneOrAbs(ODE, monteCarlo, markov, seed, seedLabel, runs,
					runsLabel, stepLabel, step, limitLabel, limit, intervalLabel, interval,
					simulators, simulatorsLabel, explanation, description, none, intSpecies,
					species, spLabel, speciesLabel, addIntSpecies, removeIntSpecies, rapid1,
					rapid2, qssa, maxCon, rapidLabel1, rapidLabel2, qssaLabel, maxConLabel,
					usingSSA, clearIntSpecies);
			if (!sbml.isSelected() && !xhtml.isSelected() && !dot.isSelected() && runFiles) {
				overwrite.setEnabled(true);
				append.setEnabled(true);
				choose3.setEnabled(true);
				if (overwrite.isSelected()) {
					limit.setEnabled(true);
					interval.setEnabled(true);
					limitLabel.setEnabled(true);
					intervalLabel.setEnabled(true);
				} else {
					limit.setEnabled(false);
					interval.setEnabled(false);
					limitLabel.setEnabled(false);
					intervalLabel.setEnabled(false);
				}
			}
		}
		// if the abstraction Radio Button is selected
		else if (e.getSource() == abstraction) {
			Button_Enabling.enableNoneOrAbs(ODE, monteCarlo, markov, seed, seedLabel, runs,
					runsLabel, stepLabel, step, limitLabel, limit, intervalLabel, interval,
					simulators, simulatorsLabel, explanation, description, none, intSpecies,
					species, spLabel, speciesLabel, addIntSpecies, removeIntSpecies, rapid1,
					rapid2, qssa, maxCon, rapidLabel1, rapidLabel2, qssaLabel, maxConLabel,
					usingSSA, clearIntSpecies);
			if (!sbml.isSelected() && !xhtml.isSelected() && !dot.isSelected() && runFiles) {
				overwrite.setEnabled(true);
				append.setEnabled(true);
				choose3.setEnabled(true);
				if (overwrite.isSelected()) {
					limit.setEnabled(true);
					interval.setEnabled(true);
					limitLabel.setEnabled(true);
					intervalLabel.setEnabled(true);
				} else {
					limit.setEnabled(false);
					interval.setEnabled(false);
					limitLabel.setEnabled(false);
					intervalLabel.setEnabled(false);
				}
			}
		}
		// if the nary Radio Button is selected
		else if (e.getSource() == nary) {
			Button_Enabling.enableNary(ODE, monteCarlo, markov, seed, seedLabel, runs, runsLabel,
					stepLabel, step, limitLabel, limit, intervalLabel, interval, simulators,
					simulatorsLabel, explanation, description, intSpecies, species, spLabel,
					speciesLabel, addIntSpecies, removeIntSpecies, rapid1, rapid2, qssa, maxCon,
					rapidLabel1, rapidLabel2, qssaLabel, maxConLabel, usingSSA, clearIntSpecies);
			if (!sbml.isSelected() && !xhtml.isSelected() && !dot.isSelected() && runFiles) {
				overwrite.setEnabled(true);
				append.setEnabled(true);
				choose3.setEnabled(true);
				if (overwrite.isSelected()) {
					limit.setEnabled(true);
					interval.setEnabled(true);
					limitLabel.setEnabled(true);
					intervalLabel.setEnabled(true);
				} else {
					limit.setEnabled(false);
					interval.setEnabled(false);
					limitLabel.setEnabled(false);
					intervalLabel.setEnabled(false);
				}
			}
		}
		// if the ODE Radio Button is selected
		else if (e.getSource() == ODE) {
			Button_Enabling.enableODE(seed, seedLabel, runs, runsLabel, stepLabel, step,
					limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description, usingSSA);
			overwrite.setEnabled(false);
			append.setEnabled(false);
			choose3.setEnabled(false);
		}
		// if the monteCarlo Radio Button is selected
		else if (e.getSource() == monteCarlo) {
			Button_Enabling.enableMonteCarlo(seed, seedLabel, runs, runsLabel, stepLabel, step,
					limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description, usingSSA);
			if (runFiles) {
				overwrite.setEnabled(true);
				append.setEnabled(true);
				choose3.setEnabled(true);
				if (overwrite.isSelected()) {
					limit.setEnabled(true);
					interval.setEnabled(true);
					limitLabel.setEnabled(true);
					intervalLabel.setEnabled(true);
				} else {
					limit.setEnabled(false);
					interval.setEnabled(false);
					limitLabel.setEnabled(false);
					intervalLabel.setEnabled(false);
				}
			}
		}
		// if the markov Radio Button is selected
		else if (e.getSource() == markov) {
			Button_Enabling.enableMarkov(seed, seedLabel, runs, runsLabel, stepLabel, step,
					limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description, usingSSA);
			overwrite.setEnabled(false);
			append.setEnabled(false);
			choose3.setEnabled(false);
		}
		// if the sbml Radio Button is selected
		else if (e.getSource() == sbml) {
			Button_Enabling.enableSbmlDotAndXhtml(seed, seedLabel, runs, runsLabel, stepLabel,
					step, limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description);
			overwrite.setEnabled(false);
			append.setEnabled(false);
			choose3.setEnabled(false);
		}
		// if the dot Radio Button is selected
		else if (e.getSource() == dot) {
			Button_Enabling.enableSbmlDotAndXhtml(seed, seedLabel, runs, runsLabel, stepLabel,
					step, limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description);
			overwrite.setEnabled(false);
			append.setEnabled(false);
			choose3.setEnabled(false);
		}
		// if the xhtml Radio Button is selected
		else if (e.getSource() == xhtml) {
			Button_Enabling.enableSbmlDotAndXhtml(seed, seedLabel, runs, runsLabel, stepLabel,
					step, limitLabel, limit, intervalLabel, interval, simulators, simulatorsLabel,
					explanation, description);
			overwrite.setEnabled(false);
			append.setEnabled(false);
			choose3.setEnabled(false);
		}
		// if the add interesting species button is clicked
		else if (e.getSource() == addIntSpecies) {
			interestingSpecies = Buttons.add(interestingSpecies, species, intSpecies, false,
					amountTerm, ge, gt, eq, lt, le, this);
		}
		// if the remove interesting species button is clicked
		else if (e.getSource() == removeIntSpecies) {
			Buttons.remove(species, interestingSpecies);
		}
		// if the clear interesting species button is clicked
		else if (e.getSource() == clearIntSpecies) {
			interestingSpecies = new Object[0];
			species.setListData(interestingSpecies);
		}
		// if the add termination conditions button is clicked
		else if (e.getSource() == addTermCond) {
			termConditions = Buttons.add(termConditions, terminations, termCond, true, amountTerm,
					ge, gt, eq, lt, le, this);
		}
		// if the remove termination conditions button is clicked
		else if (e.getSource() == removeTermCond) {
			Buttons.remove(terminations, termConditions);
		}
		// if the clear termination conditions button is clicked
		else if (e.getSource() == clearTermCond) {
			termConditions = new Object[0];
			terminations.setListData(termConditions);
		}
		// if the simulators combo box is selected
		else if (e.getSource() == simulators) {
			if (simulators.getItemCount() == 0) {
				description.setText("");
			} else if (simulators.getSelectedItem().equals("euler")) {
				step.setEnabled(true);
				stepLabel.setEnabled(true);
				description.setText("Euler method");
			} else if (simulators.getSelectedItem().equals("gear1")) {
				step.setEnabled(false);
				stepLabel.setEnabled(false);
				description.setText("Gear method, M=1");
			} else if (simulators.getSelectedItem().equals("gear2")) {
				step.setEnabled(false);
				stepLabel.setEnabled(false);
				description.setText("Gear method, M=2");
			} else if (simulators.getSelectedItem().equals("rk4imp")) {
				step.setEnabled(false);
				stepLabel.setEnabled(false);
				description.setText("Implicit 4th order Runge-Kutta at Gaussian points");
			} else if (simulators.getSelectedItem().equals("rk8pd")) {
				step.setEnabled(false);
				stepLabel.setEnabled(false);
				description.setText("Embedded Runge-Kutta Prince-Dormand (8,9) method");
			} else if (simulators.getSelectedItem().equals("rkf45")) {
				step.setEnabled(false);
				stepLabel.setEnabled(false);
				description.setText("Embedded Runge-Kutta-Fehlberg (4, 5) method");
			} else if (simulators.getSelectedItem().equals("gillespie")) {
				description.setText("Gillespie's direct method");
			} else if (simulators.getSelectedItem().equals("emc-sim")) {
				description.setText("Monte Carlo sim with jump count as" + " independent variable");
			} else if (simulators.getSelectedItem().equals("bunker")) {
				description.setText("Bunker's method");
			} else if (simulators.getSelectedItem().equals("nmc")) {
				description.setText("Monte Carlo simulation with normally"
						+ " distributed waiting time");
			} else if (simulators.getSelectedItem().equals("ctmc-transient")) {
				description.setText("Transient Distribution Analysis");
			}
		}
		// if the Run button is clicked
		else if (e.getSource() == run) {
			sbmlEditor.save();
			new Thread(this).start();
		} else if (e.getSource() == save) {
			sbmlEditor.save();
			save();
		} else if (e.getSource() == saveAs) {
			String simName = JOptionPane.showInputDialog(biomodelsim.frame(), "Enter Analysis ID:",
					"Analysis ID", JOptionPane.PLAIN_MESSAGE);
			if (simName != null && !simName.trim().equals("")) {
				biomodelsim.copySim(simName.trim());
			}
		}
		// if the using ssa check box is clicked
		else if (e.getSource() == usingSSA) {
			if (usingSSA.isSelected()) {
				newSSA.setEnabled(true);
				usingSSA.setSelected(true);
				description.setEnabled(false);
				explanation.setEnabled(false);
				simulators.setEnabled(false);
				simulatorsLabel.setEnabled(false);
				ODE.setEnabled(false);
				if (ODE.isSelected()) {
					ODE.setSelected(false);
					monteCarlo.setSelected(true);
					Button_Enabling.enableMonteCarlo(seed, seedLabel, runs, runsLabel, stepLabel,
							step, limitLabel, limit, intervalLabel, interval, simulators,
							simulatorsLabel, explanation, description, usingSSA);
					if (runFiles) {
						overwrite.setEnabled(true);
						append.setEnabled(true);
						choose3.setEnabled(true);
						if (overwrite.isSelected()) {
							limit.setEnabled(true);
							interval.setEnabled(true);
							limitLabel.setEnabled(true);
							intervalLabel.setEnabled(true);
						} else {
							limit.setEnabled(false);
							interval.setEnabled(false);
							limitLabel.setEnabled(false);
							intervalLabel.setEnabled(false);
						}
					}
				}
				markov.setEnabled(false);
				if (markov.isSelected()) {
					markov.setSelected(false);
					monteCarlo.setSelected(true);
					Button_Enabling.enableMonteCarlo(seed, seedLabel, runs, runsLabel, stepLabel,
							step, limitLabel, limit, intervalLabel, interval, simulators,
							simulatorsLabel, explanation, description, usingSSA);
					if (runFiles) {
						overwrite.setEnabled(true);
						append.setEnabled(true);
						choose3.setEnabled(true);
						if (overwrite.isSelected()) {
							limit.setEnabled(true);
							interval.setEnabled(true);
							limitLabel.setEnabled(true);
							intervalLabel.setEnabled(true);
						} else {
							limit.setEnabled(false);
							interval.setEnabled(false);
							limitLabel.setEnabled(false);
							intervalLabel.setEnabled(false);
						}
					}
				}
				ssa.setEnabled(true);
				timeLabel.setEnabled(true);
				time.setEnabled(true);
				availSpecies.setEnabled(true);
				ssaMod.setEnabled(true);
				ssaModNum.setEnabled(true);
				addSSA.setEnabled(true);
				editSSA.setEnabled(true);
				removeSSA.setEnabled(true);
			} else {
				description.setEnabled(true);
				explanation.setEnabled(true);
				simulators.setEnabled(true);
				simulatorsLabel.setEnabled(true);
				newSSA.setEnabled(false);
				usingSSA.setSelected(false);
				ssa.setEnabled(false);
				timeLabel.setEnabled(false);
				time.setEnabled(false);
				availSpecies.setEnabled(false);
				ssaMod.setEnabled(false);
				ssaModNum.setEnabled(false);
				addSSA.setEnabled(false);
				editSSA.setEnabled(false);
				removeSSA.setEnabled(false);
				if (!nary.isSelected()) {
					ODE.setEnabled(true);
				} else {
					markov.setEnabled(true);
				}
			}
		}
		// if the add ssa button is clicked
		else if (e.getSource() == addSSA) {
			double time = 0;
			int mod = 0;
			try {
				time = Double.parseDouble(this.time.getText().trim());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(biomodelsim.frame(), "You must enter a double "
						+ "into the time text field!", "Time Must Be A Double",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				mod = Integer.parseInt(ssaModNum.getText().trim());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(biomodelsim.frame(), "You must enter an integer "
						+ "into the amount change text field!", "Amount Change Must Be An Integer",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String modify;
			if (ssaMod.getSelectedItem().equals("goes to")) {
				modify = "=";
			} else if (ssaMod.getSelectedItem().equals("is added by")) {
				modify = "+";
			} else if (ssaMod.getSelectedItem().equals("is subtracted by")) {
				modify = "-";
			} else if (ssaMod.getSelectedItem().equals("is multiplied by")) {
				modify = "*";
			} else {
				modify = "/";
			}
			if (availSpecies.getSelectedItem() == null) {
				JOptionPane.showMessageDialog(biomodelsim.frame(),
						"You must select a model for simulation "
								+ "in order to add a user defined condition.",
						"Select A Model For Simulation", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String add = time + " " + availSpecies.getSelectedItem() + " " + modify + mod;
			JList addSSA = new JList();
			Object[] adding = { add };
			addSSA.setListData(adding);
			addSSA.setSelectedIndex(0);
			ssaList = Buttons.add(ssaList, ssa, addSSA, false, null, null, null, null, null, null,
					this);
			int[] index = ssa.getSelectedIndices();
			ssaList = Buttons.getList(ssaList, ssa);
			ssa.setSelectedIndices(index);
			ArrayList<Double> sort = new ArrayList<Double>();
			for (int i = 0; i < ssaList.length; i++) {
				sort.add(Double.parseDouble(((String) ssaList[i]).split(" ")[0]));
			}
			int in, out;
			for (out = 1; out < sort.size(); out++) {
				double temp = sort.get(out);
				String temp2 = (String) ssaList[out];
				in = out;
				while (in > 0 && sort.get(in - 1) > temp) {
					sort.set(in, sort.get(in - 1));
					ssaList[in] = ssaList[in - 1];
					--in;
				}
				sort.set(in, temp);
				ssaList[in] = temp2;
			}
			ssa.setListData(ssaList);
		}
		// if the edit ssa button is clicked
		else if (e.getSource() == editSSA) {
			if (ssa.getSelectedIndex() != -1) {
				String[] get = ((String) ssaList[ssa.getSelectedIndex()]).split(" ");
				JPanel ssaAddPanel = new JPanel(new BorderLayout());
				JPanel ssaAddPanel1 = new JPanel();
				JPanel ssaAddPanel2 = new JPanel();
				JLabel timeLabel = new JLabel("At time step: ");
				JTextField time = new JTextField(15);
				time.setText(get[0]);
				String filename = sbmlFile;
				SBMLReader reader = new SBMLReader();
				SBMLDocument document = reader.readSBML(filename);
				Model model = document.getModel();
				ArrayList<String> listOfSpecs = new ArrayList<String>();
				if (model != null) {
					ListOf listOfSpecies = model.getListOfSpecies();
					for (int i = 0; i < model.getNumSpecies(); i++) {
						Species species = (Species) listOfSpecies.get(i);
						listOfSpecs.add(species.getId());
					}
				}
				Object[] list = listOfSpecs.toArray();
				for (int i = 1; i < list.length; i++) {
					String index = (String) list[i];
					int j = i;
					while ((j > 0) && ((String) list[j - 1]).compareToIgnoreCase(index) > 0) {
						list[j] = list[j - 1];
						j = j - 1;
					}
					list[j] = index;
				}
				JComboBox availSpecies = new JComboBox();
				for (int i = 0; i < list.length; i++) {
					availSpecies.addItem(((String) list[i]).replace(" ", "_"));
				}
				availSpecies.setSelectedItem(get[1]);
				String[] mod = new String[5];
				mod[0] = "goes to";
				mod[1] = "is added by";
				mod[2] = "is subtracted by";
				mod[3] = "is multiplied by";
				mod[4] = "is divided by";
				JComboBox ssaMod = new JComboBox(mod);
				if (get[2].substring(0, 1).equals("=")) {
					ssaMod.setSelectedItem("goes to");
				} else if (get[2].substring(0, 1).equals("+")) {
					ssaMod.setSelectedItem("is added by");
				} else if (get[2].substring(0, 1).equals("-")) {
					ssaMod.setSelectedItem("is subtracted by");
				} else if (get[2].substring(0, 1).equals("*")) {
					ssaMod.setSelectedItem("is multiplied by");
				} else if (get[2].substring(0, 1).equals("/")) {
					ssaMod.setSelectedItem("is divided by");
				}
				JTextField ssaModNum = new JTextField(15);
				ssaModNum.setText(get[2].substring(1));
				ssaAddPanel1.add(timeLabel);
				ssaAddPanel1.add(time);
				ssaAddPanel1.add(availSpecies);
				ssaAddPanel2.add(ssaMod);
				ssaAddPanel2.add(ssaModNum);
				ssaAddPanel.add(ssaAddPanel1, "North");
				ssaAddPanel.add(ssaAddPanel2, "Center");
				String[] options = { "Save", "Cancel" };
				int value = JOptionPane.showOptionDialog(biomodelsim.frame(), ssaAddPanel,
						"Edit User Defined Data", JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (value == JOptionPane.YES_OPTION) {
					double time1 = 0;
					int mod1 = 0;
					try {
						time1 = Double.parseDouble(time.getText().trim());
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(biomodelsim.frame(),
								"You must enter a double " + "into the time text field!",
								"Time Must Be A Double", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						mod1 = Integer.parseInt(ssaModNum.getText().trim());
					} catch (Exception e1) {
						JOptionPane
								.showMessageDialog(biomodelsim.frame(),
										"You must enter an integer "
												+ "into the amount change text field!",
										"Amount Change Must Be An Integer",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					String modify;
					if (ssaMod.getSelectedItem().equals("goes to")) {
						modify = "=";
					} else if (ssaMod.getSelectedItem().equals("is added by")) {
						modify = "+";
					} else if (ssaMod.getSelectedItem().equals("is subtracted by")) {
						modify = "-";
					} else if (ssaMod.getSelectedItem().equals("is multiplied by")) {
						modify = "*";
					} else {
						modify = "/";
					}
					if (availSpecies.getSelectedItem() == null) {
						JOptionPane.showMessageDialog(biomodelsim.frame(),
								"You must select a model for simulation "
										+ "in order to add a user defined condition.",
								"Select A Model For Simulation", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ssaList[ssa.getSelectedIndex()] = time1 + " " + availSpecies.getSelectedItem()
							+ " " + modify + mod1;
					ssa.setListData(ssaList);
					int[] index = ssa.getSelectedIndices();
					ssaList = Buttons.getList(ssaList, ssa);
					ssa.setSelectedIndices(index);
					ArrayList<Double> sort = new ArrayList<Double>();
					for (int i = 0; i < ssaList.length; i++) {
						sort.add(Double.parseDouble(((String) ssaList[i]).split(" ")[0]));
					}
					int in, out;
					for (out = 1; out < sort.size(); out++) {
						double temp = sort.get(out);
						String temp2 = (String) ssaList[out];
						in = out;
						while (in > 0 && sort.get(in - 1) > temp) {
							sort.set(in, sort.get(in - 1));
							ssaList[in] = ssaList[in - 1];
							--in;
						}
						sort.set(in, temp);
						ssaList[in] = temp2;
					}
					ssa.setListData(ssaList);
				}
			}
		}
		// if the remove ssa button is clicked
		else if (e.getSource() == removeSSA) {
			Buttons.remove(ssa, ssaList);
		}
		// if the new ssa button is clicked
		else if (e.getSource() == newSSA) {
			ssaList = new Object[0];
			ssa.setListData(ssaList);
			ssa.setEnabled(true);
			timeLabel.setEnabled(true);
			time.setEnabled(true);
			availSpecies.setEnabled(true);
			ssaMod.setEnabled(true);
			ssaModNum.setEnabled(true);
			addSSA.setEnabled(true);
			editSSA.setEnabled(true);
			removeSSA.setEnabled(true);
		}
		// if the remove properties button is clicked
		else if (e.getSource() == removeProp) {
			Buttons.remove(properties, props);
		}
		// if the add properties button is clicked
		else if (e.getSource() == addProp) {
			if (prop.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(biomodelsim.frame(),
						"Enter a property into the property field!", "Must Enter A Property",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (value.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(biomodelsim.frame(),
						"Enter a value into the value field!", "Must Enter A Value",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String add = prop.getText().trim() + "=" + value.getText().trim();
			JList addPropery = new JList();
			Object[] adding = { add };
			addPropery.setListData(adding);
			addPropery.setSelectedIndex(0);
			props = Buttons.add(props, properties, addPropery, false, null, null, null, null, null,
					null, this);
		} else if (e.getSource() == overwrite) {
			limit.setEnabled(true);
			interval.setEnabled(true);
			limitLabel.setEnabled(true);
			intervalLabel.setEnabled(true);
		} else if (e.getSource() == append) {
			limit.setEnabled(false);
			interval.setEnabled(false);
			limitLabel.setEnabled(false);
			intervalLabel.setEnabled(false);
			Random rnd = new Random();
			seed.setText("" + rnd.nextInt());
			int cut = 0;
			String[] getFilename = sbmlFile.split(separator);
			for (int i = 0; i < getFilename[getFilename.length - 1].length(); i++) {
				if (getFilename[getFilename.length - 1].charAt(i) == '.') {
					cut = i;
				}
			}
			String propName = sbmlFile.substring(0, sbmlFile.length()
					- getFilename[getFilename.length - 1].length())
					+ getFilename[getFilename.length - 1].substring(0, cut) + ".properties";
			try {
				if (new File(propName).exists()) {
					Properties getProps = new Properties();
					getProps.load(new FileInputStream(new File(propName)));
					if (getProps.containsKey("monte.carlo.simulation.time.limit")) {
						limit.setText(getProps.getProperty("monte.carlo.simulation.time.limit"));
						interval.setText(getProps
								.getProperty("monte.carlo.simulation.print.interval"));
					}
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(biomodelsim.frame(),
						"Unable to restore time limit and print interval.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * If the run button is pressed, this method starts a new thread for the
	 * simulation.
	 */
	public void run() {
		double timeLimit = 100.0;
		double printInterval = 1.0;
		double timeStep = 1.0;
		String outDir = "";
		long rndSeed = 314159;
		int run = 1;
		try {
			timeLimit = Double.parseDouble(limit.getText().trim());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Time Limit Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			printInterval = Double.parseDouble(interval.getText().trim());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Print Interval Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (step.isEnabled()) {
				timeStep = Double.parseDouble(step.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Time Step Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		outDir = simName;
		try {
			if (seed.isEnabled()) {
				rndSeed = Long.parseLong(seed.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter An Integer In The Random Seed Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (runs.isEnabled()) {
				run = Integer.parseInt(runs.getText().trim());
				if (run < 0) {
					JOptionPane.showMessageDialog(biomodelsim.frame(),
							"Must Enter A Positive Integer In The Runs Field."
									+ "\nProceding With Default:  1", "Error",
							JOptionPane.ERROR_MESSAGE);
					run = 1;
				}
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Positive Integer In The Runs Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!runs.isEnabled()) {
			for (String runs : new File(root + separator + simName).list()) {
				String end = "";
				for (int j = 1; j < 5; j++) {
					end = runs.charAt(runs.length() - j) + end;
				}
				if (end.equals(".tsd") || end.equals(".dat") || end.equals(".csv")) {
					if (runs.contains("run-")) {
						run = Math.max(run, Integer.parseInt(runs.substring(4, runs.length()
								- end.length())));
					}
				}
			}
		}
		String printer_id = "tsd.printer";
		String printer_track_quantity = "amount";
		String sim = (String) simulators.getSelectedItem();
		int[] index = terminations.getSelectedIndices();
		String[] termCond = Buttons.getList(termConditions, terminations);
		terminations.setSelectedIndices(index);
		index = species.getSelectedIndices();
		String[] intSpecies = Buttons.getList(interestingSpecies, species);
		species.setSelectedIndices(index);
		String selectedButtons = "";
		double rap1 = 0.1;
		double rap2 = 0.1;
		double qss = 0.1;
		int con = 15;
		try {
			if (rapid1.isEnabled()) {
				rap1 = Double.parseDouble(rapid1.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter A Double In The Rapid"
					+ " Equilibrium Condition 1 Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (rapid2.isEnabled()) {
				rap2 = Double.parseDouble(rapid2.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter A Double In The Rapid"
					+ " Equilibrium Condition 2 Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (qssa.isEnabled()) {
				qss = Double.parseDouble(qssa.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The QSSA Condition Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (maxCon.isEnabled()) {
				con = Integer.parseInt(maxCon.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter An Integer In The Max"
					+ " Concentration Threshold Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (none.isSelected() && ODE.isSelected()) {
			selectedButtons = "none_ODE";
		} else if (none.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "none_monteCarlo";
		} else if (abstraction.isSelected() && ODE.isSelected()) {
			selectedButtons = "abs_ODE";
		} else if (abstraction.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "abs_monteCarlo";
		} else if (nary.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "nary_monteCarlo";
		} else if (nary.isSelected() && markov.isSelected()) {
			selectedButtons = "nary_markov";
		} else if (none.isSelected()) {
			selectedButtons = "none";
		} else if (abstraction.isSelected()) {
			selectedButtons = "abs";
		} else if (nary.isSelected()) {
			selectedButtons = "nary";
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(root + separator + simName
					+ separator + "user-defined.dat"));
			int[] indecies = ssa.getSelectedIndices();
			ssaList = Buttons.getList(ssaList, ssa);
			ssa.setSelectedIndices(indecies);
			String save = "";
			for (int i = 0; i < ssaList.length; i++) {
				if (i == ssaList.length - 1) {
					save += ssaList[i];
				} else {
					save += ssaList[i] + "\n";
				}
			}
			byte[] output = save.getBytes();
			out.write(output);
			out.close();
			if (!usingSSA.isSelected() && save.trim().equals("")) {
				new File(root + separator + simName + separator + "user-defined.dat").delete();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Unable to save user defined file!",
					"Error Saving File", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int cut = 0;
		String[] getFilename = sbmlFile.split(separator);
		for (int i = 0; i < getFilename[getFilename.length - 1].length(); i++) {
			if (getFilename[getFilename.length - 1].charAt(i) == '.') {
				cut = i;
			}
		}
		String propName = sbmlFile.substring(0, sbmlFile.length()
				- getFilename[getFilename.length - 1].length())
				+ getFilename[getFilename.length - 1].substring(0, cut) + ".properties";
		String monteLimit = "";
		String monteInterval = "";
		try {
			if (new File(propName).exists()) {
				Properties getProps = new Properties();
				getProps.load(new FileInputStream(new File(propName)));
				if (getProps.containsKey("monte.carlo.simulation.time.limit")) {
					monteLimit = getProps.getProperty("monte.carlo.simulation.time.limit");
					monteInterval = getProps.getProperty("monte.carlo.simulation.print.interval");
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Unable to add properties to property file.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		log.addText("Creating properties file:\n" + propName + "\n");
		final JButton cancel = new JButton("Cancel");
		final JFrame running = new JFrame("Running...");
		WindowListener w = new WindowListener() {
			public void windowClosing(WindowEvent arg0) {
				cancel.doClick();
				running.dispose();
			}

			public void windowOpened(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowDeactivated(WindowEvent arg0) {
			}
		};
		running.addWindowListener(w);
		JPanel text = new JPanel();
		JPanel progBar = new JPanel();
		JPanel button = new JPanel();
		JPanel all = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Progress");
		int steps;
		if (ODE.isSelected()) {
			steps = (int) (timeLimit / printInterval);
		} else {
			steps = run;
		}
		JProgressBar progress = new JProgressBar(0, steps);
		progress.setStringPainted(true);
		// progress.setString("");
		// progress.setIndeterminate(true);
		progress.setValue(0);
		text.add(label);
		progBar.add(progress);
		button.add(cancel);
		all.add(text, "North");
		all.add(progBar, "Center");
		all.add(button, "South");
		running.setContentPane(all);
		running.pack();
		Dimension screenSize;
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			screenSize = tk.getScreenSize();
		} catch (AWTError awe) {
			screenSize = new Dimension(640, 480);
		}
		Dimension frameSize = running.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		int x = screenSize.width / 2 - frameSize.width / 2;
		int y = screenSize.height / 2 - frameSize.height / 2;
		running.setLocation(x, y);
		running.setVisible(true);
		running.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Run runProgram = new Run();
		cancel.addActionListener(runProgram);
		biomodelsim.getExitButton().addActionListener(runProgram);
		if (sadFile.getText().trim().length() != 0) {
			try {
				FileOutputStream out = new FileOutputStream(new File(root + separator + outDir
						+ separator + outDir + ".sad"));
				byte[] output = sadFile.getText().trim().getBytes();
				out.write(output);
				out.close();
			} catch (Exception e) {
			}
		}
		runProgram.createProperties(timeLimit, printInterval, timeStep,".", 
					    //root + separator + outDir,
				rndSeed, run, termCond, intSpecies, printer_id, printer_track_quantity, sbmlFile
						.split(separator), selectedButtons, this, sbmlFile, rap1, rap2, qss, con,
				usingSSA, 
					    //root + separator + simName + separator + 
					    "user-defined.dat", sadFile
						.getText().trim(), new File(root + separator + outDir + separator + outDir
						+ ".sad"));
		int[] indecies = properties.getSelectedIndices();
		props = Buttons.getList(props, properties);
		properties.setSelectedIndices(indecies);
		try {
			Properties getProps = new Properties();
			getProps.load(new FileInputStream(new File(propName)));
			for (int i = 0; i < props.length; i++) {
				String[] split = ((String) props[i]).split("=");
				getProps.setProperty(split[0], split[1]);
			}
			getProps.setProperty("selected.simulator", sim);
			if (monteCarlo.isSelected()) {
				if (append.isSelected()) {
					String[] searchForRunFiles = new File(root + separator + simName).list();
					int start = 1;
					for (String s : searchForRunFiles) {
						if (s.length() > 3 && s.substring(0, 4).equals("run-")) {
							start = Math.max(Integer.parseInt(s.substring(4, s.length() - 4)),
									start);
						}
					}
					getProps.setProperty("monte.carlo.simulation.start.index", (start + 1) + "");
				} else {
					getProps.setProperty("monte.carlo.simulation.start.index", "1");
				}
			}
			if (getProps.containsKey("ode.simulation.time.limit")) {
				if (!monteLimit.equals("")) {
					getProps.setProperty("monte.carlo.simulation.time.limit", monteLimit);
					getProps.setProperty("monte.carlo.simulation.print.interval", monteInterval);
				}
			}
			getProps.store(new FileOutputStream(new File(propName)),
					getFilename[getFilename.length - 1].substring(0, cut) + " Properties");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Unable to add properties to property file.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		if (monteCarlo.isSelected() && overwrite.isSelected()) {
			File[] files = new File(root + separator + outDir).listFiles();
			for (File f : files) {
				if (f.getName().contains("run-")) {
					f.delete();
				}
			}
		}
		int exit = runProgram.execute(sbmlFile, sbml, dot, xhtml, biomodelsim.frame(), ODE,
				monteCarlo, sim, printer_id, printer_track_quantity, root + separator + outDir,
				nary, 1, intSpecies, log, usingSSA, root + separator + simName + separator
						+ "user-defined.dat", biomodelsim, simTab, root, progress, steps);
		if (nary.isSelected() && exit == 0) {
			new Nary_Run(this, amountTerm, ge, gt, eq, lt, le, simulators, sbmlFile
					.split(separator), sbmlFile, sbml, dot, xhtml, nary, ODE, monteCarlo,
					timeLimit, printInterval, root + separator + outDir, rndSeed, run, printer_id,
					printer_track_quantity, termCond, intSpecies, rap1, rap2, qss, con, log,
					usingSSA, root + separator + simName + separator + "user-defined.dat",
					biomodelsim, simTab, root);
		}
		running.setCursor(null);
		running.dispose();
		biomodelsim.getExitButton().removeActionListener(runProgram);
		String[] searchForRunFiles = new File(root + separator + simName).list();
		for (String s : searchForRunFiles) {
			if (s.length() > 3 && s.substring(0, 4).equals("run-")) {
				runFiles = true;
			}
		}
		if (monteCarlo.isSelected()) {
			overwrite.setEnabled(true);
			append.setEnabled(true);
			choose3.setEnabled(true);
			if (overwrite.isSelected()) {
				limit.setEnabled(true);
				interval.setEnabled(true);
				limitLabel.setEnabled(true);
				intervalLabel.setEnabled(true);
			} else {
				limit.setEnabled(false);
				interval.setEnabled(false);
				limitLabel.setEnabled(false);
				intervalLabel.setEnabled(false);
			}
		}
		if (append.isSelected()) {
			Random rnd = new Random();
			seed.setText("" + rnd.nextInt());
		}
		for (int i = 0; i < biomodelsim.getTab().getTabCount(); i++) {
			if (biomodelsim.getTab().getComponentAt(i) instanceof Graph) {
				((Graph) biomodelsim.getTab().getComponentAt(i)).refresh();
			}
		}
	}

	/**
	 * Invoked when the mouse is double clicked in the interesting species
	 * JLists or termination conditions JLists. Adds or removes the selected
	 * interesting species or termination conditions.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (e.getSource() == intSpecies) {
				interestingSpecies = Buttons.add(interestingSpecies, species, intSpecies, false,
						amountTerm, ge, gt, eq, lt, le, this);
			} else if (e.getSource() == species) {
				Buttons.remove(species, interestingSpecies);
			} else if (e.getSource() == termCond) {
				termConditions = Buttons.add(termConditions, terminations, termCond, true,
						amountTerm, ge, gt, eq, lt, le, this);
			} else if (e.getSource() == terminations) {
				Buttons.remove(terminations, termConditions);
			} else if (e.getSource() == ssa) {
				if (ssa.getSelectedIndex() != -1) {
					String[] get = ((String) ssaList[ssa.getSelectedIndex()]).split(" ");
					JPanel ssaAddPanel = new JPanel(new BorderLayout());
					JPanel ssaAddPanel1 = new JPanel();
					JPanel ssaAddPanel2 = new JPanel();
					JLabel timeLabel = new JLabel("At time step: ");
					JTextField time = new JTextField(15);
					time.setText(get[0]);
					String filename = sbmlFile;
					SBMLReader reader = new SBMLReader();
					SBMLDocument document = reader.readSBML(filename);
					Model model = document.getModel();
					ArrayList<String> listOfSpecs = new ArrayList<String>();
					if (model != null) {
						ListOf listOfSpecies = model.getListOfSpecies();
						for (int i = 0; i < model.getNumSpecies(); i++) {
							Species species = (Species) listOfSpecies.get(i);
							listOfSpecs.add(species.getId());
						}
					}
					Object[] list = listOfSpecs.toArray();
					for (int i = 1; i < list.length; i++) {
						String index = (String) list[i];
						int j = i;
						while ((j > 0) && ((String) list[j - 1]).compareToIgnoreCase(index) > 0) {
							list[j] = list[j - 1];
							j = j - 1;
						}
						list[j] = index;
					}
					JComboBox availSpecies = new JComboBox();
					for (int i = 0; i < list.length; i++) {
						availSpecies.addItem(((String) list[i]).replace(" ", "_"));
					}
					availSpecies.setSelectedItem(get[1]);
					String[] mod = new String[5];
					mod[0] = "goes to";
					mod[1] = "is added by";
					mod[2] = "is subtracted by";
					mod[3] = "is multiplied by";
					mod[4] = "is divided by";
					JComboBox ssaMod = new JComboBox(mod);
					if (get[2].substring(0, 1).equals("=")) {
						ssaMod.setSelectedItem("goes to");
					} else if (get[2].substring(0, 1).equals("+")) {
						ssaMod.setSelectedItem("is added by");
					} else if (get[2].substring(0, 1).equals("-")) {
						ssaMod.setSelectedItem("is subtracted by");
					} else if (get[2].substring(0, 1).equals("*")) {
						ssaMod.setSelectedItem("is multiplied by");
					} else if (get[2].substring(0, 1).equals("/")) {
						ssaMod.setSelectedItem("is divided by");
					}
					JTextField ssaModNum = new JTextField(15);
					ssaModNum.setText(get[2].substring(1));
					ssaAddPanel1.add(timeLabel);
					ssaAddPanel1.add(time);
					ssaAddPanel1.add(availSpecies);
					ssaAddPanel2.add(ssaMod);
					ssaAddPanel2.add(ssaModNum);
					ssaAddPanel.add(ssaAddPanel1, "North");
					ssaAddPanel.add(ssaAddPanel2, "Center");
					String[] options = { "Save", "Cancel" };
					int value = JOptionPane.showOptionDialog(biomodelsim.frame(), ssaAddPanel,
							"Edit User Defined Data", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if (value == JOptionPane.YES_OPTION) {
						double time1 = 0;
						int mod1 = 0;
						try {
							time1 = Double.parseDouble(time.getText().trim());
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(biomodelsim.frame(),
									"You must enter a double " + "into the time text field!",
									"Time Must Be A Double", JOptionPane.ERROR_MESSAGE);
							return;
						}
						try {
							mod1 = Integer.parseInt(ssaModNum.getText().trim());
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(biomodelsim.frame(),
									"You must enter an integer "
											+ "into the amount change text field!",
									"Amount Change Must Be An Integer", JOptionPane.ERROR_MESSAGE);
							return;
						}
						String modify;
						if (ssaMod.getSelectedItem().equals("goes to")) {
							modify = "=";
						} else if (ssaMod.getSelectedItem().equals("is added by")) {
							modify = "+";
						} else if (ssaMod.getSelectedItem().equals("is subtracted by")) {
							modify = "-";
						} else if (ssaMod.getSelectedItem().equals("is multiplied by")) {
							modify = "*";
						} else {
							modify = "/";
						}
						if (availSpecies.getSelectedItem() == null) {
							JOptionPane.showMessageDialog(biomodelsim.frame(),
									"You must select a model for simulation "
											+ "in order to add a user defined condition.",
									"Select A Model For Simulation", JOptionPane.ERROR_MESSAGE);
							return;
						}
						ssaList[ssa.getSelectedIndex()] = time1 + " "
								+ availSpecies.getSelectedItem() + " " + modify + mod1;
						ssa.setListData(ssaList);
						int[] index = ssa.getSelectedIndices();
						ssaList = Buttons.getList(ssaList, ssa);
						ssa.setSelectedIndices(index);
						ArrayList<Double> sort = new ArrayList<Double>();
						for (int i = 0; i < ssaList.length; i++) {
							sort.add(Double.parseDouble(((String) ssaList[i]).split(" ")[0]));
						}
						int in, out;
						for (out = 1; out < sort.size(); out++) {
							double temp = sort.get(out);
							String temp2 = (String) ssaList[out];
							in = out;
							while (in > 0 && sort.get(in - 1) > temp) {
								sort.set(in, sort.get(in - 1));
								ssaList[in] = ssaList[in - 1];
								--in;
							}
							sort.set(in, temp);
							ssaList[in] = temp2;
						}
						ssa.setListData(ssaList);
					}
				}
			} else if (e.getSource() == properties) {
				Buttons.remove(properties, props);
			}
		}
	}

	/**
	 * This method currently does nothing.
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * This method currently does nothing.
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * This method currently does nothing.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * This method currently does nothing.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Saves the simulate options.
	 */
	public void save() {
		double timeLimit = 100.0;
		double printInterval = 1.0;
		double timeStep = 1.0;
		String outDir = ".";
		long rndSeed = 314159;
		int run = 1;
		try {
			timeLimit = Double.parseDouble(limit.getText().trim());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Time Limit Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			printInterval = Double.parseDouble(interval.getText().trim());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Print Interval Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (step.isEnabled()) {
				timeStep = Double.parseDouble(step.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The Time Step Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		outDir = root + separator + simName;
		try {
			if (seed.isEnabled()) {
				rndSeed = Long.parseLong(seed.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter An Integer In The Random Seed Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (runs.isEnabled()) {
				run = Integer.parseInt(runs.getText().trim());
				if (run < 0) {
					JOptionPane.showMessageDialog(biomodelsim.frame(),
							"Must Enter A Positive Integer In The Runs Field."
									+ "\nProceding With Default:  1", "Error",
							JOptionPane.ERROR_MESSAGE);
					run = 1;
				}
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Positive Integer In The Runs Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String printer_id = "tsd.printer";
		String printer_track_quantity = "amount";
		int[] index = terminations.getSelectedIndices();
		String[] termCond = Buttons.getList(termConditions, terminations);
		terminations.setSelectedIndices(index);
		index = species.getSelectedIndices();
		String[] intSpecies = Buttons.getList(interestingSpecies, species);
		species.setSelectedIndices(index);
		String selectedButtons = "";
		double rap1 = 0.1;
		double rap2 = 0.1;
		double qss = 0.1;
		int con = 15;
		try {
			if (rapid1.isEnabled()) {
				rap1 = Double.parseDouble(rapid1.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter A Double In The Rapid"
					+ " Equilibrium Condition 1 Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (rapid2.isEnabled()) {
				rap2 = Double.parseDouble(rapid2.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter A Double In The Rapid"
					+ " Equilibrium Condition 2 Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (qssa.isEnabled()) {
				qss = Double.parseDouble(qssa.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Must Enter A Double In The QSSA Condition Field.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			if (maxCon.isEnabled()) {
				con = Integer.parseInt(maxCon.getText().trim());
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Must Enter An Integer In The Max"
					+ " Concentration Threshold Field.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (none.isSelected() && ODE.isSelected()) {
			selectedButtons = "none_ODE";
		} else if (none.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "none_monteCarlo";
		} else if (abstraction.isSelected() && ODE.isSelected()) {
			selectedButtons = "abs_ODE";
		} else if (abstraction.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "abs_monteCarlo";
		} else if (nary.isSelected() && monteCarlo.isSelected()) {
			selectedButtons = "nary_monteCarlo";
		} else if (nary.isSelected() && markov.isSelected()) {
			selectedButtons = "nary_markov";
		} else if (none.isSelected()) {
			selectedButtons = "none";
		} else if (abstraction.isSelected()) {
			selectedButtons = "abs";
		} else if (nary.isSelected()) {
			selectedButtons = "nary";
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(root + separator + simName
					+ separator + "user-defined.dat"));
			int[] indecies = ssa.getSelectedIndices();
			ssaList = Buttons.getList(ssaList, ssa);
			ssa.setSelectedIndices(indecies);
			String save = "";
			for (int i = 0; i < ssaList.length; i++) {
				if (i == ssaList.length - 1) {
					save += ssaList[i];
				} else {
					save += ssaList[i] + "\n";
				}
			}
			byte[] output = save.getBytes();
			out.write(output);
			out.close();
			if (!usingSSA.isSelected() && save.trim().equals("")) {
				new File(root + separator + simName + separator + "user-defined.dat").delete();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Unable to save user defined file!",
					"Error Saving File", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Run runProgram = new Run();
		int cut = 0;
		String[] getFilename = sbmlFile.split(separator);
		for (int i = 0; i < getFilename[getFilename.length - 1].length(); i++) {
			if (getFilename[getFilename.length - 1].charAt(i) == '.') {
				cut = i;
			}
		}
		String propName = sbmlFile.substring(0, sbmlFile.length()
				- getFilename[getFilename.length - 1].length())
				+ getFilename[getFilename.length - 1].substring(0, cut) + ".properties";
		String monteLimit = "";
		String monteInterval = "";
		try {
			if (new File(propName).exists()) {
				Properties getProps = new Properties();
				getProps.load(new FileInputStream(new File(propName)));
				if (getProps.containsKey("monte.carlo.simulation.time.limit")) {
					monteLimit = getProps.getProperty("monte.carlo.simulation.time.limit");
					monteInterval = getProps.getProperty("monte.carlo.simulation.print.interval");
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Unable to add properties to property file.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		log.addText("Creating properties file:\n" + propName + "\n");
		if (sadFile.getText().trim().length() != 0) {
			try {
				FileOutputStream out = new FileOutputStream(new File(root + separator + outDir
						+ separator + outDir + ".sad"));
				byte[] output = sadFile.getText().trim().getBytes();
				out.write(output);
				out.close();
			} catch (Exception e) {
			}
		}
		runProgram.createProperties(timeLimit, printInterval, timeStep, ".",
					    //outDir, 
					    rndSeed, run,
				termCond, intSpecies, printer_id, printer_track_quantity,
				sbmlFile.split(separator), selectedButtons, this, sbmlFile, rap1, rap2, qss, con,
				usingSSA, 
					    //root + separator + simName + separator + 
					    "user-defined.dat", sadFile
						.getText().trim(), new File(root + separator + outDir + separator + outDir
						+ ".sad"));
		int[] indecies = properties.getSelectedIndices();
		props = Buttons.getList(props, properties);
		properties.setSelectedIndices(indecies);
		try {
			Properties getProps = new Properties();
			getProps.load(new FileInputStream(new File(propName)));
			for (int i = 0; i < props.length; i++) {
				String[] split = ((String) props[i]).split("=");
				getProps.setProperty(split[0], split[1]);
			}
			getProps.setProperty("selected.simulator", (String) simulators.getSelectedItem());
			if (getProps.containsKey("ode.simulation.time.limit")) {
				if (!monteLimit.equals("")) {
					getProps.setProperty("monte.carlo.simulation.time.limit", monteLimit);
					getProps.setProperty("monte.carlo.simulation.print.interval", monteInterval);
				}
			}
			getProps.store(new FileOutputStream(new File(propName)),
					getFilename[getFilename.length - 1].substring(0, cut) + " Properties");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(biomodelsim.frame(),
					"Unable to add properties to property file.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		biomodelsim.refreshTree();
	}

	/**
	 * Loads the simulate options.
	 */
	public void open(String openFile) {
		Properties load = new Properties();
		try {
			if (!openFile.equals("")) {
				load.load(new FileInputStream(new File(openFile)));
				ArrayList<String> loadProperties = new ArrayList<String>();
				for (Object key : load.keySet()) {
					if (key.equals("reb2sac.abstraction.method.0.1")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.1").equals(
								"enzyme-kinetic-qssa-1")) {
							loadProperties.add("reb2sac.abstraction.method.0.1="
									+ load.getProperty("reb2sac.abstraction.method.0.1"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.2")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.2").equals(
								"reversible-to-irreversible-transformer")) {
							loadProperties.add("reb2sac.abstraction.method.0.2="
									+ load.getProperty("reb2sac.abstraction.method.0.2"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.3")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.3").equals(
								"multiple-products-reaction-eliminator")) {
							loadProperties.add("reb2sac.abstraction.method.0.3="
									+ load.getProperty("reb2sac.abstraction.method.0.3"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.4")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.4").equals(
								"multiple-reactants-reaction-eliminator")) {
							loadProperties.add("reb2sac.abstraction.method.0.4="
									+ load.getProperty("reb2sac.abstraction.method.0.4"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.5")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.5").equals(
								"single-reactant-product-reaction-eliminator")) {
							loadProperties.add("reb2sac.abstraction.method.0.5="
									+ load.getProperty("reb2sac.abstraction.method.0.5"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.6")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.6").equals(
								"dimer-to-monomer-substitutor")) {
							loadProperties.add("reb2sac.abstraction.method.0.6="
									+ load.getProperty("reb2sac.abstraction.method.0.6"));
						}
					} else if (key.equals("reb2sac.abstraction.method.0.7")) {
						if (!load.getProperty("reb2sac.abstraction.method.0.7").equals(
								"inducer-structure-transformer")) {
							loadProperties.add("reb2sac.abstraction.method.0.7="
									+ load.getProperty("reb2sac.abstraction.method.0.7"));
						}
					} else if (key.equals("reb2sac.abstraction.method.1.1")) {
						if (!load.getProperty("reb2sac.abstraction.method.1.1").equals(
								"modifier-structure-transformer")) {
							loadProperties.add("reb2sac.abstraction.method.1.1="
									+ load.getProperty("reb2sac.abstraction.method.1.1"));
						}
					} else if (key.equals("reb2sac.abstraction.method.1.2")) {
						if (!load.getProperty("reb2sac.abstraction.method.1.2").equals(
								"modifier-constant-propagation")) {
							loadProperties.add("reb2sac.abstraction.method.1.2="
									+ load.getProperty("reb2sac.abstraction.method.1.2"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.1")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.1").equals(
								"operator-site-forward-binding-remover")) {
							loadProperties.add("reb2sac.abstraction.method.2.1="
									+ load.getProperty("reb2sac.abstraction.method.2.1"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.3")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.3").equals(
								"enzyme-kinetic-rapid-equilibrium-1")) {
							loadProperties.add("reb2sac.abstraction.method.2.3="
									+ load.getProperty("reb2sac.abstraction.method.2.3"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.4")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.4").equals(
								"irrelevant-species-remover")) {
							loadProperties.add("reb2sac.abstraction.method.2.4="
									+ load.getProperty("reb2sac.abstraction.method.2.4"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.5")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.5").equals(
								"inducer-structure-transformer")) {
							loadProperties.add("reb2sac.abstraction.method.2.5="
									+ load.getProperty("reb2sac.abstraction.method.2.5"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.6")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.6").equals(
								"modifier-constant-propagation")) {
							loadProperties.add("reb2sac.abstraction.method.2.6="
									+ load.getProperty("reb2sac.abstraction.method.2.6"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.7")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.7").equals(
								"similar-reaction-combiner")) {
							loadProperties.add("reb2sac.abstraction.method.2.7="
									+ load.getProperty("reb2sac.abstraction.method.2.7"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.8")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.8").equals(
								"modifier-constant-propagation")) {
							loadProperties.add("reb2sac.abstraction.method.2.8="
									+ load.getProperty("reb2sac.abstraction.method.2.8"));
						}
					} else if (key.equals("reb2sac.abstraction.method.2.2")) {
						if (!load.getProperty("reb2sac.abstraction.method.2.2").equals(
								"dimerization-reduction")
								&& !load.getProperty("reb2sac.abstraction.method.2.2").equals(
										"dimerization-reduction-level-assignment")) {
							loadProperties.add("reb2sac.abstraction.method.2.2="
									+ load.getProperty("reb2sac.abstraction.method.2.2"));
						}
					} else if (key.equals("reb2sac.abstraction.method.3.1")) {
						if (!load.getProperty("reb2sac.abstraction.method.3.1").equals(
								"kinetic-law-constants-simplifier")
								&& !load.getProperty("reb2sac.abstraction.method.3.1").equals(
										"reversible-to-irreversible-transformer")
								&& !load.getProperty("reb2sac.abstraction.method.3.1").equals(
										"nary-order-unary-transformer")) {
							loadProperties.add("reb2sac.abstraction.method.3.1="
									+ load.getProperty("reb2sac.abstraction.method.3.1"));
						}
					} else if (key.equals("reb2sac.abstraction.method.3.2")) {
						if (!load.getProperty("reb2sac.abstraction.method.3.2").equals(
								"kinetic-law-constants-simplifier")
								&& !load.getProperty("reb2sac.abstraction.method.3.2").equals(
										"modifier-constant-propagation")) {
							loadProperties.add("reb2sac.abstraction.method.3.2="
									+ load.getProperty("reb2sac.abstraction.method.3.2"));
						}
					} else if (key.equals("reb2sac.abstraction.method.3.3")) {
						if (!load.getProperty("reb2sac.abstraction.method.3.3").equals(
								"absolute-inhibition-generator")) {
							loadProperties.add("reb2sac.abstraction.method.3.3="
									+ load.getProperty("reb2sac.abstraction.method.3.3"));
						}
					} else if (key.equals("reb2sac.abstraction.method.3.4")) {
						if (!load.getProperty("reb2sac.abstraction.method.3.4").equals(
								"final-state-generator")) {
							loadProperties.add("reb2sac.abstraction.method.3.4="
									+ load.getProperty("reb2sac.abstraction.method.3.4"));
						}
					} else if (key.equals("reb2sac.abstraction.method.3.5")) {
						if (!load.getProperty("reb2sac.abstraction.method.3.5").equals(
								"stop-flag-generator")) {
							loadProperties.add("reb2sac.abstraction.method.3.5="
									+ load.getProperty("reb2sac.abstraction.method.3.5"));
						}
					} else if (key.equals("reb2sac.nary.order.decider")) {
						if (!load.getProperty("reb2sac.nary.order.decider").equals("distinct")) {
							loadProperties.add("reb2sac.nary.order.decider="
									+ load.getProperty("reb2sac.nary.order.decider"));
						}
					} else if (key.equals("simulation.printer")) {
						if (!load.getProperty("simulation.printer").equals("tsd.printer")) {
							loadProperties.add("simulation.printer="
									+ load.getProperty("simulation.printer"));
						}
					} else if (key.equals("simulation.printer.tracking.quantity")) {
						if (!load.getProperty("simulation.printer.tracking.quantity").equals(
								"amount")) {
							loadProperties.add("simulation.printer.tracking.quantity="
									+ load.getProperty("simulation.printer.tracking.quantity"));
						}
					} else if (((String) key).length() > 27
							&& ((String) key).substring(0, 28).equals(
									"reb2sac.interesting.species.")) {
					} else if (key.equals("reb2sac.rapid.equilibrium.condition.1")) {
					} else if (key.equals("reb2sac.rapid.equilibrium.condition.2")) {
					} else if (key.equals("reb2sac.qssa.condition.1")) {
					} else if (key.equals("reb2sac.operator.max.concentration.threshold")) {
					} else if (key.equals("ode.simulation.time.limit")) {
					} else if (key.equals("ode.simulation.print.interval")) {
					} else if (key.equals("ode.simulation.time.step")) {
					} else if (key.equals("ode.simulation.out.dir")) {
					} else if (key.equals("monte.carlo.simulation.time.limit")) {
					} else if (key.equals("monte.carlo.simulation.print.interval")) {
					} else if (key.equals("monte.carlo.simulation.random.seed")) {
					} else if (key.equals("monte.carlo.simulation.runs")) {
					} else if (key.equals("monte.carlo.simulation.out.dir")) {
					} else if (key.equals("simulation.run.termination.decider")) {
						if (!load.getProperty("simulation.run.termination.decider").equals("sad")) {
							loadProperties.add("simulation.run.termination.decider="
									+ load.getProperty("simulation.run.termination.decider"));
						}
					} else if (key.equals("computation.analysis.sad.path")) {
					} else if (key.equals("simulation.time.series.species.level.file")) {
					} else if (key.equals("selected.simulator")) {
					} else if (((String) key).length() > 36
							&& ((String) key).substring(0, 37).equals(
									"simulation.run.termination.condition.")) {
					} else if (((String) key).length() > 37
							&& ((String) key).substring(0, 38).equals(
									"reb2sac.absolute.inhibition.threshold.")) {
					} else if (((String) key).length() > 27
							&& ((String) key).substring(0, 28).equals(
									"reb2sac.concentration.level.")) {
					} else if (((String) key).length() > 19
							&& ((String) key).substring(0, 20).equals("reb2sac.final.state.")) {
					} else if (key.equals("reb2sac.analysis.stop.enabled")) {
					} else if (key.equals("reb2sac.analysis.stop.rate")) {
					} else if (key.equals("monte.carlo.simulation.start.index")) {
					} else {
						loadProperties.add(key + "=" + load.getProperty((String) key));
					}
				}
				props = loadProperties.toArray(props);
				properties.setListData(props);
				String[] getFilename = openFile.split(separator);
				int cut = 0;
				for (int i = 0; i < getFilename[getFilename.length - 1].length(); i++) {
					if (getFilename[getFilename.length - 1].charAt(i) == '.') {
						cut = i;
					}
				}
				String filename = "";
				if (new File((openFile.substring(0, openFile.length()
						- getFilename[getFilename.length - 1].length()))
						+ getFilename[getFilename.length - 1].substring(0, cut) + ".sbml").exists()) {
					filename = (openFile.substring(0, openFile.length()
							- getFilename[getFilename.length - 1].length()))
							+ getFilename[getFilename.length - 1].substring(0, cut) + ".sbml";
				} else if (new File((openFile.substring(0, openFile.length()
						- getFilename[getFilename.length - 1].length()))
						+ getFilename[getFilename.length - 1].substring(0, cut) + ".xml").exists()) {
					filename = (openFile.substring(0, openFile.length()
							- getFilename[getFilename.length - 1].length()))
							+ getFilename[getFilename.length - 1].substring(0, cut) + ".xml";
				}
				try {
					sbmlFile = filename;
					ArrayList<String> listOfSpecs = new ArrayList<String>();
					SBMLReader reader = new SBMLReader();
					SBMLDocument document = reader.readSBML(filename);
					Model model = document.getModel();
					if (model != null) {
						ListOf listOfSpecies = model.getListOfSpecies();
						for (int i = 0; i < model.getNumSpecies(); i++) {
							Species species = (Species) listOfSpecies.get(i);
							listOfSpecs.add(species.getId());
						}
					}
					Object[] list = listOfSpecs.toArray();
					for (int i = 1; i < list.length; i++) {
						String index = (String) list[i];
						int j = i;
						while ((j > 0) && ((String) list[j - 1]).compareToIgnoreCase(index) > 0) {
							list[j] = list[j - 1];
							j = j - 1;
						}
						list[j] = index;
					}
					intSpecies.setListData(list);
					termCond.setListData(list);
					int rem = availSpecies.getItemCount();
					for (int i = 0; i < rem; i++) {
						availSpecies.removeItemAt(0);
					}
					for (int i = 0; i < list.length; i++) {
						availSpecies.addItem(((String) list[i]).replace(" ", "_"));
					}
				} catch (Exception e1) {
				}
				species.setListData(new Object[0]);
				terminations.setListData(new Object[0]);
				String check;
				if (load.containsKey("reb2sac.abstraction.method.3.1")) {
					check = load.getProperty("reb2sac.abstraction.method.3.1");
					if (check.equals("kinetic-law-constants-simplifier")) {
						none.setSelected(true);
						Button_Enabling.enableNoneOrAbs(ODE, monteCarlo, markov, seed, seedLabel,
								runs, runsLabel, stepLabel, step, limitLabel, limit, intervalLabel,
								interval, simulators, simulatorsLabel, explanation, description,
								none, intSpecies, species, spLabel, speciesLabel, addIntSpecies,
								removeIntSpecies, rapid1, rapid2, qssa, maxCon, rapidLabel1,
								rapidLabel2, qssaLabel, maxConLabel, usingSSA, clearIntSpecies);
					}
				}
				if (load.containsKey("reb2sac.abstraction.method.2.2")) {
					check = load.getProperty("reb2sac.abstraction.method.2.2");
					if (check.equals("dimerization-reduction")) {
						abstraction.setSelected(true);
						Button_Enabling.enableNoneOrAbs(ODE, monteCarlo, markov, seed, seedLabel,
								runs, runsLabel, stepLabel, step, limitLabel, limit, intervalLabel,
								interval, simulators, simulatorsLabel, explanation, description,
								none, intSpecies, species, spLabel, speciesLabel, addIntSpecies,
								removeIntSpecies, rapid1, rapid2, qssa, maxCon, rapidLabel1,
								rapidLabel2, qssaLabel, maxConLabel, usingSSA, clearIntSpecies);
					} else if (check.equals("dimerization-reduction-level-assignment")) {
						nary.setSelected(true);
						Button_Enabling.enableNary(ODE, monteCarlo, markov, seed, seedLabel, runs,
								runsLabel, stepLabel, step, limitLabel, limit, intervalLabel,
								interval, simulators, simulatorsLabel, explanation, description,
								intSpecies, species, spLabel, speciesLabel, addIntSpecies,
								removeIntSpecies, rapid1, rapid2, qssa, maxCon, rapidLabel1,
								rapidLabel2, qssaLabel, maxConLabel, usingSSA, clearIntSpecies);
					}
				}
				if (load.containsKey("ode.simulation.time.limit")) {
					ODE.setSelected(true);
					Button_Enabling.enableODE(seed, seedLabel, runs, runsLabel, stepLabel, step,
							limitLabel, limit, intervalLabel, interval, simulators,
							simulatorsLabel, explanation, description, usingSSA);
					limit.setText(load.getProperty("ode.simulation.time.limit"));
					interval.setText(load.getProperty("ode.simulation.print.interval"));
					step.setText(load.getProperty("ode.simulation.time.step"));
				} else if (load.containsKey("monte.carlo.simulation.time.limit")) {
					monteCarlo.setSelected(true);
					if (runFiles) {
						overwrite.setEnabled(true);
						append.setEnabled(true);
						choose3.setEnabled(true);
					}
					Button_Enabling.enableMonteCarlo(seed, seedLabel, runs, runsLabel, stepLabel,
							step, limitLabel, limit, intervalLabel, interval, simulators,
							simulatorsLabel, explanation, description, usingSSA);
					limit.setText(load.getProperty("monte.carlo.simulation.time.limit"));
					interval.setText(load.getProperty("monte.carlo.simulation.print.interval"));
					seed.setText(load.getProperty("monte.carlo.simulation.random.seed"));
					runs.setText(load.getProperty("monte.carlo.simulation.runs"));
					if (load.containsKey("simulation.run.termination.decider")
							&& load.getProperty("simulation.run.termination.decider").equals("sad")) {
					  // CHECK: This needs to be a full path
						FileInputStream input = new FileInputStream(new File(root + separator + simName + separator + load
								.getProperty("computation.analysis.sad.path")));
						int read = input.read();
						while (read != -1) {
							sadFile.append("" + (char) read);
							read = input.read();
						}
					}
				} else {
					if (nary.isSelected()) {
						markov.setSelected(true);
						Button_Enabling.enableMarkov(seed, seedLabel, runs, runsLabel, stepLabel,
								step, limitLabel, limit, intervalLabel, interval, simulators,
								simulatorsLabel, explanation, description, usingSSA);
					} else {
						sbml.setSelected(true);
						Button_Enabling.enableSbmlDotAndXhtml(seed, seedLabel, runs, runsLabel,
								stepLabel, step, limitLabel, limit, intervalLabel, interval,
								simulators, simulatorsLabel, explanation, description);
					}
				}
				ArrayList<String> getLists = new ArrayList<String>();
				int i = 1;
				while (load.containsKey("simulation.run.termination.condition." + i)) {
					getLists.add(load.getProperty("simulation.run.termination.condition." + i));
					i++;
				}
				termConditions = getLists.toArray();
				terminations.setListData(termConditions);
				getLists = new ArrayList<String>();
				i = 1;
				while (load.containsKey("reb2sac.interesting.species." + i)) {
					getLists.add(load.getProperty("reb2sac.interesting.species." + i));
					i++;
				}
				interestingSpecies = getLists.toArray();
				species.setListData(interestingSpecies);
				if (load.containsKey("reb2sac.rapid.equilibrium.condition.1")) {
					rapid1.setText(load.getProperty("reb2sac.rapid.equilibrium.condition.1"));
				}
				if (load.containsKey("reb2sac.rapid.equilibrium.condition.2")) {
					rapid2.setText(load.getProperty("reb2sac.rapid.equilibrium.condition.2"));
				}
				if (load.containsKey("reb2sac.qssa.condition.1")) {
					qssa.setText(load.getProperty("reb2sac.qssa.condition.1"));
				}
				if (load.containsKey("reb2sac.operator.max.concentration.threshold")) {
					maxCon
							.setText(load
									.getProperty("reb2sac.operator.max.concentration.threshold"));
				}
				if (load.containsKey("simulation.time.series.species.level.file")) {
					usingSSA.setEnabled(true);
					newSSA.setEnabled(true);
					usingSSA.setSelected(true);
					description.setEnabled(false);
					explanation.setEnabled(false);
					simulators.setEnabled(false);
					simulatorsLabel.setEnabled(false);
					String getData = "";
					try {
					  // CHECK: This needs to be a full path
					  Scanner scan = new Scanner(new File(root + separator + simName + separator + load
								.getProperty("simulation.time.series.species.level.file")));
						while (scan.hasNextLine()) {
							String get = scan.nextLine();
							if (get.split(" ").length == 3) {
								if (scan.hasNextLine()) {
									getData += get + "\n";
								} else {
									getData += get;
								}
							}
						}
					} catch (Exception e1) {
					}
					if (!getData.equals("")) {
						ssaList = getData.split("\n");
					} else {
						ssaList = new Object[0];
					}
					//ssa.setListData(ssaList);
					ssa.setEnabled(true);
					timeLabel.setEnabled(true);
					time.setEnabled(true);
					availSpecies.setEnabled(true);
					ssaMod.setEnabled(true);
					ssaModNum.setEnabled(true);
					addSSA.setEnabled(true);
					editSSA.setEnabled(true);
					removeSSA.setEnabled(true);
					ODE.setEnabled(false);
					markov.setEnabled(false);
				} else {
					description.setEnabled(true);
					explanation.setEnabled(true);
					simulators.setEnabled(true);
					simulatorsLabel.setEnabled(true);
					newSSA.setEnabled(false);
					usingSSA.setSelected(false);
					ssa.setEnabled(false);
					timeLabel.setEnabled(false);
					time.setEnabled(false);
					availSpecies.setEnabled(false);
					ssaMod.setEnabled(false);
					ssaModNum.setEnabled(false);
					addSSA.setEnabled(false);
					editSSA.setEnabled(false);
					removeSSA.setEnabled(false);
					if (!nary.isSelected()) {
						ODE.setEnabled(true);
					} else {
						markov.setEnabled(true);
					}
				}
			}
			if (load.containsKey("selected.simulator")) {
				simulators.setSelectedItem(load.getProperty("selected.simulator"));
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(biomodelsim.frame(), "Unable to load properties file!",
					"Error Loading Properties", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Graph createGraph(String open) {
		String outDir = root + separator + simName;
		String printer_id = "tsd.printer";
		String printer_track_quantity = "amount";
		int[] index = species.getSelectedIndices();
		species.setSelectedIndices(index);
		return new Graph(printer_track_quantity, simName + " simulation results", printer_id,
				outDir, "time", biomodelsim, open, log, null);
	}

	public JButton getRunButton() {
		return run;
	}

	public JButton getSaveButton() {
		return save;
	}

	public void setSbml(SBML_Editor sbml) {
		sbmlEditor = sbml;
	}

	public void updateSpeciesList() {
		SBMLReader reader = new SBMLReader();
		SBMLDocument document = reader.readSBML(sbmlFile);
		Model model = document.getModel();
		ArrayList<String> listOfSpecs = new ArrayList<String>();
		if (model != null) {
			ListOf listOfSpecies = model.getListOfSpecies();
			for (int i = 0; i < model.getNumSpecies(); i++) {
				Species species = (Species) listOfSpecies.get(i);
				listOfSpecs.add(species.getId());
			}
		}
		Object[] list = listOfSpecs.toArray();
		for (int i = 1; i < list.length; i++) {
			String index = (String) list[i];
			int j = i;
			while ((j > 0) && ((String) list[j - 1]).compareToIgnoreCase(index) > 0) {
				list[j] = list[j - 1];
				j = j - 1;
			}
			list[j] = index;
		}
		intSpecies.setListData(list);
		termCond.setListData(list);
		int rem = availSpecies.getItemCount();
		for (int i = 0; i < rem; i++) {
			availSpecies.removeItemAt(0);
		}
		for (int i = 0; i < list.length; i++) {
			availSpecies.addItem(((String) list[i]).replace(" ", "_"));
		}
	}

	public JButton getSaveAsButton() {
		return saveAs;
	}

	public void setSim(String newSimName) {
		for (String s : new File(root + separator + newSimName).list()) {
			if (s.length() > 3 && s.substring(s.length() - 4).equals("sbml")
					|| s.substring(s.length() - 4).equals(".xml")) {
				sbmlFile = root + separator + newSimName + separator + s;
			}
		}
		simName = newSimName;
		sbmlEditor.setFile(sbmlFile);
	}
}
