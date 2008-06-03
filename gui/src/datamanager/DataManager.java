package datamanager;

import gcm2sbml.network.GeneticNetwork;
import gcm2sbml.parser.GCMParser;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.sbml.libsbml.ListOf;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;

import parser.*;
import biomodelsim.*;
import buttons.*;

public class DataManager extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -2669704247953218544L;

	private String directory;

	private JList files;

	private JTable table;

	private JButton saveData, addData, removeData, editData, copyData;

	private JButton add, remove, rename, copy, copyFromView, importFile;

	private BioSim biosim;

	private String separator;

	private String[] species;

	private JScrollPane scroll1;

	private JPanel dataButtons;

	private JPanel dataPanel;

	private JPanel filesPanel;

	public DataManager(String directory, BioSim biosim) {
		if (File.separator.equals("\\")) {
			separator = "\\\\";
		}
		else {
			separator = File.separator;
		}

		this.biosim = biosim;
		this.directory = directory;
		try {
			Properties p = new Properties();
			FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
			p.load(load);
			load.close();
			String[] s = p.values().toArray(new String[0]);
			sort(s);
			files = new JList(s);
		}
		catch (Exception e) {
			files = new JList();
		}
		String background;
		try {
			Properties p = new Properties();
			String[] split = directory.split(separator);
			FileInputStream load = new FileInputStream(new File(directory + separator
					+ split[split.length - 1] + ".lrn"));
			p.load(load);
			load.close();
			if (p.containsKey("genenet.file")) {
				String[] getProp = p.getProperty("genenet.file").split(separator);
				background = directory.substring(0, directory.length() - split[split.length - 1].length())
						+ separator + getProp[getProp.length - 1];
			}
			else {
				background = null;
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(biosim.frame(), "Unable to load background file.", "Error",
					JOptionPane.ERROR_MESSAGE);
			background = null;
		}
		species = null;
		if (background != null) {
			if (background.contains(".gcm")) {
				GCMParser parser = new GCMParser(background);
				GeneticNetwork network = parser.buildNetwork();
				network.outputSBML(directory + separator + "temp.sbml");
				SBMLReader reader = new SBMLReader();
				SBMLDocument document = reader.readSBML(directory + separator + "temp.sbml");
				Model model = document.getModel();
				ListOf ids = model.getListOfSpecies();
				ArrayList<String> getSpecies = new ArrayList<String>();
				for (int i = 0; i < model.getNumSpecies(); i++) {
					getSpecies.add(ids.get(i).getId());
				}
				species = getSpecies.toArray(new String[0]);
				new File(directory + separator + "temp.sbml").delete();
			}
			else {
				SBMLReader reader = new SBMLReader();
				SBMLDocument document = reader.readSBML(background);
				Model model = document.getModel();
				ListOf ids = model.getListOfSpecies();
				ArrayList<String> getSpecies = new ArrayList<String>();
				for (int i = 0; i < model.getNumSpecies(); i++) {
					getSpecies.add(ids.get(i).getId());
				}
				species = getSpecies.toArray(new String[0]);
			}
		}
		if (species != null) {
			sort(species);
		}
		JScrollPane scroll = new JScrollPane();
		scroll.setMinimumSize(new Dimension(260, 200));
		scroll.setPreferredSize(new Dimension(276, 132));
		scroll.setViewportView(files);
		files.addMouseListener(this);
		add = new JButton("Add");
		add.addActionListener(this);
		remove = new JButton("Remove");
		remove.addActionListener(this);
		rename = new JButton("Rename");
		rename.addActionListener(this);
		copy = new JButton("Copy");
		copy.addActionListener(this);
		copyFromView = new JButton("Copy From View");
		copyFromView.addActionListener(this);
		importFile = new JButton("Import");
		importFile.addActionListener(this);
		JPanel buttons = new JPanel(new BorderLayout());
		JPanel topButtons = new JPanel();
		JPanel bottomButtons = new JPanel();
		topButtons.add(add);
		topButtons.add(remove);
		topButtons.add(rename);
		bottomButtons.add(copy);
		bottomButtons.add(copyFromView);
		bottomButtons.add(importFile);
		buttons.add(topButtons, "North");
		buttons.add(bottomButtons, "South");
		table = new JTable();
		scroll1 = new JScrollPane();
		scroll1.setMinimumSize(new Dimension(260, 200));
		scroll1.setPreferredSize(new Dimension(276, 132));
		scroll1.setViewportView(table);
		saveData = new JButton("Save");
		saveData.addActionListener(this);
		addData = new JButton("Add Data Point");
		addData.addActionListener(this);
		removeData = new JButton("Remove Data Point");
		removeData.addActionListener(this);
		editData = new JButton("Edit Data Point");
		editData.addActionListener(this);
		copyData = new JButton("Copy Data Point");
		copyData.addActionListener(this);
		dataButtons = new JPanel();
		dataButtons.add(saveData);
		dataButtons.add(addData);
		dataButtons.add(removeData);
		dataButtons.add(editData);
		dataButtons.add(copyData);
		filesPanel = new JPanel(new BorderLayout());
		filesPanel.add(scroll, "Center");
		filesPanel.add(buttons, "South");
		dataPanel = new JPanel(new BorderLayout());
		dataPanel.add(scroll1, "Center");
		dataPanel.add(dataButtons, "South");
		this.setLayout(new BorderLayout());
		this.add(filesPanel, "West");
		this.add(dataPanel, "Center");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
		}
		else if (e.getSource() == remove) {
			if (files.getSelectedIndices().length > 0) {
				try {
					Properties p = new Properties();
					FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
					p.load(load);
					load.close();
					Object[] delete = files.getSelectedValues();
					for (Object file : delete) {
						int run = 0;
						String[] list = new File(directory).list();
						for (int i = 0; i < list.length; i++) {
							if (!(new File(directory + separator + list[i]).isDirectory())
									&& list[i].length() > 4) {
								String end = "";
								for (int j = 1; j < 5; j++) {
									end = list[i].charAt(list[i].length() - j) + end;
								}
								if (end.equals(".tsd")) {
									if (list[i].contains("run-")) {
										int tempNum = Integer.parseInt(list[i].substring(4, list[i].length()
												- end.length()));
										if (tempNum > run) {
											run = tempNum;
										}
									}
								}
							}
						}
						for (String s : p.keySet().toArray(new String[0])) {
							if (p.getProperty(s).equals(file)) {
								if (s.equals("run-" + run + ".tsd")) {
									new File(directory + separator + s).delete();
									p.remove(s);
								}
								else {
									new File(directory + separator + s).delete();
									new File(directory + separator + "run-" + run + ".tsd").renameTo(new File(
											directory + separator + s));
									p.setProperty(s, p.getProperty("run-" + run + ".tsd"));
									p.remove("run-" + run + ".tsd");
								}
								break;
							}
						}
					}
					FileOutputStream store = new FileOutputStream(new File(directory + separator + ".lrn"));
					p.store(store, "Learn File Data");
					store.close();
					String[] s = p.values().toArray(new String[0]);
					sort(s);
					files.setListData(s);
					biosim.refreshLearn(directory.split(separator)[directory.split(separator).length - 1],
							true);
				}
				catch (Exception e1) {
					JOptionPane.showMessageDialog(biosim.frame(), "Unable to remove selected files.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if (e.getSource() == rename) {
			if (files.getSelectedIndices().length > 1) {
				JOptionPane
						.showMessageDialog(biosim.frame(), "You can only select one file to rename at a time.",
								"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if (files.getSelectedIndices().length < 1) {
				JOptionPane.showMessageDialog(biosim.frame(), "You must select a file to rename.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				String rename = JOptionPane.showInputDialog(biosim.frame(), "Please Enter New Name:",
						"Rename", JOptionPane.PLAIN_MESSAGE);
				if (rename != null) {
					rename = rename.trim();
				}
				else {
					return;
				}
				Properties p = new Properties();
				FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
				p.load(load);
				load.close();
				int index = files.getSelectedIndex();
				String[] list = Buttons
						.getList(new String[p.keySet().toArray(new String[0]).length], files);
				files.setSelectedIndex(index);
				for (String s : list) {
					if (s.equals(rename)) {
						JOptionPane.showMessageDialog(biosim.frame(),
								"A file with that description already exists.", "Description Must Be Unique",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				for (String s : p.keySet().toArray(new String[0])) {
					if (p.getProperty(s).equals(files.getSelectedValue())) {
						p.setProperty(s, rename);
					}
				}
				FileOutputStream store = new FileOutputStream(new File(directory + separator + ".lrn"));
				p.store(store, "Learn File Data");
				store.close();
				String[] s = p.values().toArray(new String[0]);
				sort(s);
				files.setListData(s);
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(biosim.frame(), "Unable to rename selected file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == copy) {
			if (files.getSelectedIndices().length > 1) {
				JOptionPane.showMessageDialog(biosim.frame(),
						"You can only select one file to copy at a time.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if (files.getSelectedIndices().length < 1) {
				JOptionPane.showMessageDialog(biosim.frame(), "You must select a file to copy.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				String copy = JOptionPane.showInputDialog(biosim.frame(), "Please Enter New Name:", "Copy",
						JOptionPane.PLAIN_MESSAGE);
				if (copy != null) {
					copy = copy.trim();
				}
				else {
					return;
				}
				int run = 0;
				String[] list = new File(directory).list();
				for (int i = 0; i < list.length; i++) {
					if (!(new File(directory + separator + list[i]).isDirectory()) && list[i].length() > 4) {
						String end = "";
						for (int j = 1; j < 5; j++) {
							end = list[i].charAt(list[i].length() - j) + end;
						}
						if (end.equals(".tsd")) {
							if (list[i].contains("run-")) {
								int tempNum = Integer.parseInt(list[i]
										.substring(4, list[i].length() - end.length()));
								if (tempNum > run) {
									run = tempNum;
								}
							}
						}
					}
				}
				Properties p = new Properties();
				FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
				p.load(load);
				load.close();
				int index = files.getSelectedIndex();
				list = Buttons.getList(new String[p.keySet().toArray(new String[0]).length], files);
				files.setSelectedIndex(index);
				for (String s : list) {
					if (s.equals(copy)) {
						JOptionPane.showMessageDialog(biosim.frame(),
								"A file with that description already exists.", "Description Must Be Unique",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				for (String s : p.keySet().toArray(new String[0])) {
					if (p.getProperty(s).equals(files.getSelectedValue())) {
						String end = "run-" + (run + 1) + ".tsd";
						FileOutputStream out = new FileOutputStream(new File(directory + separator + end));
						FileInputStream in = new FileInputStream(new File(directory + separator + s));
						int read = in.read();
						while (read != -1) {
							out.write(read);
							read = in.read();
						}
						in.close();
						out.close();
						p.setProperty(end, copy);
					}
				}
				FileOutputStream store = new FileOutputStream(new File(directory + separator + ".lrn"));
				p.store(store, "Learn File Data");
				store.close();
				String[] s = p.values().toArray(new String[0]);
				sort(s);
				files.setListData(s);
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(biosim.frame(), "Unable to copy selected file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == copyFromView) {
			String root = directory.substring(0, directory.length()
					- directory.split(separator)[directory.split(separator).length - 1].length());
			ArrayList<String> list = new ArrayList<String>();
			for (String s : new File(root).list()) {
				if (new File(root + separator + s).isDirectory()
						&& !s.equals(directory.split(separator)[directory.split(separator).length - 1])) {
					boolean add = false;
					for (String ss : new File(root + separator + s).list()) {
						if (new File(root + separator + s + separator + ss).isDirectory()) {
							boolean add2 = false;
							for (String sss : new File(root + separator + s + separator + ss).list()) {
								if (sss.contains(".tsd")) {
									add2 = true;
								}
							}
							if (add2) {
								list.add(s + separator + ss);
							}
						}
						else if (ss.contains(".tsd")) {
							add = true;
						}
					}
					if (add) {
						list.add(s);
					}
				}
			}
			if (list.size() > 0) {
				String[] s = list.toArray(new String[0]);
				sort(s);
				JList sims = new JList(s);
				JScrollPane scroll = new JScrollPane();
				scroll.setMinimumSize(new Dimension(260, 200));
				scroll.setPreferredSize(new Dimension(276, 132));
				scroll.setViewportView(sims);
				sims.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				sims.setSelectedIndex(0);
				Object[] options = { "Select", "Cancel" };
				int value = JOptionPane.showOptionDialog(biosim.frame(), scroll, "Select View",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (value == JOptionPane.YES_OPTION) {
					int run = 0;
					String[] lists = new File(directory).list();
					for (int i = 0; i < lists.length; i++) {
						if (!(new File(directory + separator + lists[i]).isDirectory())
								&& lists[i].length() > 4) {
							String end = "";
							for (int j = 1; j < 5; j++) {
								end = lists[i].charAt(lists[i].length() - j) + end;
							}
							if (end.equals(".tsd")) {
								if (lists[i].contains("run-")) {
									int tempNum = Integer.parseInt(lists[i].substring(4, lists[i].length()
											- end.length()));
									if (tempNum > run) {
										run = tempNum;
									}
								}
							}
						}
					}
					String[] list1 = new File(root + separator + sims.getSelectedValue()).list();
					Properties p = new Properties();
					try {
						FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
						p.load(load);
						load.close();
					}
					catch (Exception e1) {
					}
					for (int i = 0; i < list1.length; i++) {
						if (!(new File(root + separator + sims.getSelectedValue() + separator + list1[i])
								.isDirectory())
								&& list1[i].length() > 4) {
							String end = "";
							for (int j = 1; j < 5; j++) {
								end = list1[i].charAt(list1[i].length() - j) + end;
							}
							if (end.equals(".tsd")) {
								try {
									String last = "run-" + (run + 1) + ".tsd";
									TSDParser tsd = new TSDParser(root + separator + sims.getSelectedValue()
											+ separator + list1[i], biosim);
									ArrayList<String> specs = tsd.getSpecies();
									ArrayList<ArrayList<Double>> data = tsd.getData();
									ArrayList<String> newSpecs = new ArrayList<String>();
									ArrayList<ArrayList<Double>> newData = new ArrayList<ArrayList<Double>>();
									newSpecs.add(specs.get(0));
									newData.add(data.get(0));
									for (int j = 0; j < species.length; j++) {
										if (specs.contains(species[j])) {
											int index = specs.indexOf(species[j]);
											newSpecs.add(specs.get(index));
											newData.add(data.get(index));
										}
										else {
											newSpecs.add(species[j]);
											ArrayList<Double> dat = new ArrayList<Double>();
											for (int k = 0; k < newData.get(0).size(); k++) {
												dat.add(0.0);
											}
											newData.add(dat);
										}
									}
									tsd.setSpecies(newSpecs);
									tsd.setData(newData);
									tsd.outputTSD(directory + separator + last);
									/*
									 * FileOutputStream out = new FileOutputStream( new
									 * File(directory + separator + last)); FileInputStream in =
									 * new FileInputStream(new File(root + separator +
									 * sims.getSelectedValue() + separator + list1[i])); int read =
									 * in.read(); while (read != -1) { out.write(read); read =
									 * in.read(); } in.close(); out.close();
									 */
									p.setProperty(last, sims.getSelectedValue() + separator + list1[i]);
									run++;
								}
								catch (Exception e1) {
									JOptionPane.showMessageDialog(biosim.frame(), "Unable to copy from view.",
											"Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
					try {
						String[] ss = p.values().toArray(new String[0]);
						sort(ss);
						files.setListData(ss);
						FileOutputStream store = new FileOutputStream(new File(directory + separator + ".lrn"));
						p.store(store, "Learn File Data");
						store.close();
						if (ss.length > 0) {
							biosim.refreshLearn(
									directory.split(separator)[directory.split(separator).length - 1], true);
						}
					}
					catch (Exception e1) {
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(biosim.frame(), "There are no views to copy data from.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == importFile) {
			String importFile = Buttons.browse(biosim.frame(), null, null,
					JFileChooser.FILES_AND_DIRECTORIES, "Import");
			if (importFile != null && !importFile.trim().equals("")) {
				importFile = importFile.trim();
				int run = 0;
				String[] list = new File(directory).list();
				for (int i = 0; i < list.length; i++) {
					if (!(new File(directory + separator + list[i]).isDirectory()) && list[i].length() > 4) {
						String end = "";
						for (int j = 1; j < 5; j++) {
							end = list[i].charAt(list[i].length() - j) + end;
						}
						if (end.equals(".tsd")) {
							if (list[i].contains("run-")) {
								int tempNum = Integer.parseInt(list[i]
										.substring(4, list[i].length() - end.length()));
								if (tempNum > run) {
									run = tempNum;
								}
							}
						}
					}
				}
				if (new File(importFile).isDirectory()) {
					String[] list1 = new File(importFile).list();
					Properties p = new Properties();
					try {
						FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
						p.load(load);
						load.close();
					}
					catch (Exception e1) {
					}
					for (int i = 0; i < list1.length; i++) {
						if (!(new File(importFile + separator + list1[i]).isDirectory())
								&& list1[i].length() > 4) {
							String end = "";
							for (int j = 1; j < 5; j++) {
								end = list1[i].charAt(list1[i].length() - j) + end;
							}
							if (end.equals(".tsd")) {
								try {
									String last = "run-" + (run + 1) + ".tsd";
									TSDParser tsd = new TSDParser(importFile + separator + list1[i], biosim);
									ArrayList<String> specs = tsd.getSpecies();
									ArrayList<ArrayList<Double>> data = tsd.getData();
									ArrayList<String> newSpecs = new ArrayList<String>();
									ArrayList<ArrayList<Double>> newData = new ArrayList<ArrayList<Double>>();
									newSpecs.add(specs.get(0));
									newData.add(data.get(0));
									for (int j = 0; j < species.length; j++) {
										if (specs.contains(species[j])) {
											int index = specs.indexOf(species[j]);
											newSpecs.add(specs.get(index));
											newData.add(data.get(index));
										}
										else {
											newSpecs.add(species[j]);
											ArrayList<Double> dat = new ArrayList<Double>();
											for (int k = 0; k < newData.get(0).size(); k++) {
												dat.add(0.0);
											}
											newData.add(dat);
										}
									}
									tsd.setSpecies(newSpecs);
									tsd.setData(newData);
									tsd.outputTSD(directory + separator + last);
									/*
									 * FileOutputStream out = new FileOutputStream( new
									 * File(directory + separator + last)); FileInputStream in =
									 * new FileInputStream(new File(importFile + separator +
									 * list1[i])); int read = in.read(); while (read != -1) {
									 * out.write(read); read = in.read(); } in.close();
									 * out.close();
									 */
									p.setProperty(last, importFile + separator + list1[i]);
									run++;
								}
								catch (Exception e1) {
									JOptionPane.showMessageDialog(biosim.frame(), "Unable to import file.", "Error",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
					try {
						String[] s = p.values().toArray(new String[0]);
						sort(s);
						files.setListData(s);
						FileOutputStream store = new FileOutputStream(new File(directory + separator + ".lrn"));
						p.store(store, "Learn File Data");
						store.close();
						if (s.length > 0) {
							biosim.refreshLearn(
									directory.split(separator)[directory.split(separator).length - 1], true);
						}
					}
					catch (Exception e1) {
					}
				}
				else {
					if (importFile.length() > 3
							&& (importFile.substring(importFile.length() - 4, importFile.length()).equals(".tsd")
									|| importFile.substring(importFile.length() - 4, importFile.length()).equals(
											".csv") || importFile.substring(importFile.length() - 4, importFile.length())
									.equals(".dat"))) {
						try {
							String end = "run-" + (run + 1) + ".tsd";
							Parser parse;
							if (importFile.substring(importFile.length() - 4, importFile.length()).equals(".csv")) {
								parse = new CSVParser(importFile, biosim);
							}
							else if (importFile.substring(importFile.length() - 4, importFile.length()).equals(
									".dat")) {
								parse = new DATParser(importFile, biosim);
							}
							else {
								parse = new TSDParser(importFile, biosim);
							}
							ArrayList<String> specs = parse.getSpecies();
							ArrayList<ArrayList<Double>> data = parse.getData();
							ArrayList<String> newSpecs = new ArrayList<String>();
							ArrayList<ArrayList<Double>> newData = new ArrayList<ArrayList<Double>>();
							newSpecs.add(specs.get(0));
							newData.add(data.get(0));
							for (int j = 0; j < species.length; j++) {
								if (specs.contains(species[j])) {
									int index = specs.indexOf(species[j]);
									newSpecs.add(specs.get(index));
									newData.add(data.get(index));
								}
								else {
									newSpecs.add(species[j]);
									ArrayList<Double> dat = new ArrayList<Double>();
									for (int k = 0; k < newData.get(0).size(); k++) {
										dat.add(0.0);
									}
									newData.add(dat);
								}
							}
							parse.setSpecies(newSpecs);
							parse.setData(newData);
							parse.outputTSD(directory + separator + end);
							Properties p = new Properties();
							FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
							p.load(load);
							load.close();
							p.setProperty(end, importFile);
							FileOutputStream store = new FileOutputStream(
									new File(directory + separator + ".lrn"));
							p.store(store, "Learn File Data");
							store.close();
							String[] s = p.values().toArray(new String[0]);
							sort(s);
							files.setListData(s);
							if (s.length > 0) {
								biosim.refreshLearn(
										directory.split(separator)[directory.split(separator).length - 1], true);
							}
						}
						catch (Exception e1) {
							JOptionPane.showMessageDialog(biosim.frame(), "Unable to import file.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(biosim.frame(), "Unable to import file."
								+ "\nImported file must be a tsd file.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		else if (e.getSource() == saveData) {
		}
		else if (e.getSource() == addData) {
			TableModel m = table.getModel();
			String[][] dat = new String[m.getRowCount() + 1][m.getColumnCount()];
			for (int i = 0; i < m.getColumnCount(); i++) {
				for (int j = 0; j < m.getRowCount(); j++) {
					dat[j + 1][i] = (String) m.getValueAt(j, i);
				}
			}
			for (int i = 0; i < m.getColumnCount(); i++) {
				dat[0][i] = "0.0";
			}
			String[] spec = new String[m.getColumnCount()];
			for (int i = 0; i < m.getColumnCount(); i++) {
				spec[i] = m.getColumnName(i);
			}
			createTable(dat, spec);
		}
		else if (e.getSource() == removeData) {
		}
		else if (e.getSource() == editData) {
		}
		else if (e.getSource() == copyData) {
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (files.getSelectedValue() != null) {
			try {
				String file = (String) files.getSelectedValue();
				Properties p = new Properties();
				FileInputStream load = new FileInputStream(new File(directory + separator + ".lrn"));
				p.load(load);
				load.close();
				for (String s : p.keySet().toArray(new String[0])) {
					if (p.getProperty(s).equals(file)) {
						TSDParser tsd = new TSDParser(directory + separator + s, biosim);
						ArrayList<String> specs = tsd.getSpecies();
						ArrayList<ArrayList<Double>> data = tsd.getData();
						String[] spec = specs.toArray(new String[0]);
						String[][] dat = new String[data.get(0).size()][data.size()];
						for (int i = 0; i < data.size(); i++) {
							for (int j = 0; j < data.get(i).size(); j++) {
								dat[j][i] = "" + data.get(i).get(j);
							}
						}
						createTable(dat, spec);
					}
				}
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(biosim.frame(), "Unable to display file.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private void sort(String[] sort) {
		int i, j;
		String index;
		for (i = 1; i < sort.length; i++) {
			index = sort[i];
			j = i;
			while ((j > 0) && sort[j - 1].compareToIgnoreCase(index) > 0) {
				sort[j] = sort[j - 1];
				j = j - 1;
			}
			sort[j] = index;
		}
	}

	private void createTable(String[][] dat, String[] spec) {
		this.removeAll();
		table = new JTable(dat, spec);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
				double d1 = Double.parseDouble(s1);
				double d2 = Double.parseDouble(s2);
				if (d1 > d2) {
					return 1;
				}
				else if (d2 > d1) {
					return -1;
				}
				else {
					return 0;
				}
			}
		};
		sorter.setComparator(0, comparator);
		sorter.setSortsOnUpdates(true);
		for (int i = 0; i < spec.length; i++) {
			sorter.setSortable(i, false);
		}
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		table.setRowSorter(sorter);
		scroll1 = new JScrollPane();
		scroll1.setMinimumSize(new Dimension(260, 200));
		scroll1.setPreferredSize(new Dimension(276, 132));
		scroll1.setViewportView(table);
		scroll1.revalidate();
		scroll1.repaint();
		dataPanel = new JPanel(new BorderLayout());
		dataPanel.add(scroll1, "Center");
		dataPanel.add(dataButtons, "South");
		this.setLayout(new BorderLayout());
		this.add(filesPanel, "West");
		this.add(dataPanel, "Center");
		this.revalidate();
		this.repaint();
	}
}
