package sbol;


import java.awt.*;

import javax.swing.*;

import org.sbolstandard.core.*;

import biomodel.util.Utility;

import java.io.*;

import java.util.*;

import main.Gui;

public class SbolBrowser extends JPanel {
	
	private String[] options = {"Ok", "Cancel"};
	private JPanel selectionPanel = new JPanel(new GridLayout(1,2));
	private JTextArea viewArea = new JTextArea();
	private JScrollPane viewScroll = new JScrollPane();
	private LibraryPanel libPanel;
	private DnaComponentPanel compPanel;
	private String selection = "";
	
	//Constructor when browsing a single RDF file from the main gui
	public SbolBrowser(String filePath, Gui gui) {
		super(new BorderLayout());
		
		HashMap<String, org.sbolstandard.core.Collection> libMap = new HashMap<String, org.sbolstandard.core.Collection>();
		
		org.sbolstandard.core.Collection lib = SbolUtility.loadXML(filePath);
		if (lib != null) {
			String fileId = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
			libMap.put(fileId + "/" + lib.getDisplayId(), lib);

			constructBrowser(libMap, "");

			JPanel browserPanel = new JPanel();
			browserPanel.add(selectionPanel, "North");
			browserPanel.add(viewScroll, "Center");

			JTabbedPane browserTab = new JTabbedPane();
			browserTab.add("SBOL Browser", browserPanel);
			this.add(browserTab);
			gui.addTab(fileId, this, null);
		}
	}
	
	//Constructor when browsing RDF file subsets for SBOL to GCM association
	public SbolBrowser(HashSet<String> sbolFiles, String filter, String defaultSelection) {
		super(new GridLayout(2,1));
		
		HashMap<String, org.sbolstandard.core.Collection> libMap = new HashMap<String, org.sbolstandard.core.Collection>();
		for (String filePath : sbolFiles) {
			org.sbolstandard.core.Collection lib = SbolUtility.loadXML(filePath);
			if (lib != null) {
				String fileId = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
				libMap.put(fileId + "/" + lib.getDisplayId(), lib);
			}
		}
		
		if (libMap.size() > 0) {
			constructBrowser(libMap, filter);

			this.add(selectionPanel);
			this.add(viewScroll);

			boolean display = true;
			while (display)
				display = browserOpen(defaultSelection);
		} else {
			selection = defaultSelection;
			JOptionPane.showMessageDialog(Gui.frame, "No SBOL files are found in project.", 
					"File Not Found", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean browserOpen(String defaultSelection) {
		boolean selectionValid;
		do {
			selectionValid = true;
			int option = JOptionPane.showOptionDialog(Gui.frame, this,
					"SBOL Browser", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (option == JOptionPane.YES_OPTION) {
				String[] libIds = libPanel.getSelectedIds();
				String[] compIds = compPanel.getSelectedIds();
				if (libIds.length > 0)
					selection = libIds[0];
				else {
					selectionValid = false;
					JOptionPane.showMessageDialog(Gui.frame, "No collection is selected.",
							"Invalid Selection", JOptionPane.ERROR_MESSAGE);
				}
				if (compIds.length > 0)
					selection = selection + "/" + compIds[0];
				else if (libIds.length > 0) {
					selectionValid = false;
					JOptionPane.showMessageDialog(Gui.frame, "No DNA component is selected.",
							"Invalid Selection", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				selection = defaultSelection;
				selectionValid = true;
			}
		} while(!selectionValid);
		return false;
	}
	
	private void constructBrowser(HashMap<String, org.sbolstandard.core.Collection> libMap, String filter) {
		viewScroll.setMinimumSize(new Dimension(780, 400));
		viewScroll.setPreferredSize(new Dimension(828, 264));
//		viewScroll.setMinimumSize(new Dimension(552, 80));
//		viewScroll.setPreferredSize(new Dimension(552, 80));
		viewScroll.setViewportView(viewArea);
		viewArea.setLineWrap(true);
		viewArea.setEditable(false);
		
		HashMap<String, DnaComponent> compMap = new HashMap<String, DnaComponent>();
		
		compPanel = new DnaComponentPanel(compMap, viewArea);
		libPanel = new LibraryPanel(libMap, compMap, viewArea, compPanel, filter);
		libPanel.setLibraries(libMap.keySet());
		
		selectionPanel.add(libPanel);
		selectionPanel.add(compPanel);
	}
	
	public String getSelection() {
		return selection;
	}
}
