package gcm2sbml.parser;

import gcm2sbml.network.BaseSpecies;
import gcm2sbml.network.ConstantSpecies;
import gcm2sbml.network.GeneticNetwork;
import gcm2sbml.network.Promoter;
import gcm2sbml.network.Reaction;
import gcm2sbml.network.SpasticSpecies;
import gcm2sbml.network.SpeciesInterface;
import gcm2sbml.util.GlobalConstants;
import gcm2sbml.util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses a genetic circuit model.
 * 
 * @author Nam Nguyen
 * 
 */
public class GCMParser {
	
	private String separator;

	public GCMParser(String filename) {
		this(filename, false);
	}

	public GCMParser(String filename, boolean debug) {
		if (File.separator.equals("\\")) {
			separator = "\\\\";
		}
		else {
			separator = File.separator;
		}
		this.debug = debug;
		gcm = new GCMFile(filename.substring(0, filename.length()
				- filename.split(separator)[filename.split(separator).length - 1]
						.length()));
		gcm.load(filename);
		data = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String str;
			while ((str = in.readLine()) != null) {
				data.append(str + "\n");
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Error opening file");
		}
	}

	public GeneticNetwork buildNetwork() {
		org.sbml.libsbml.SBMLDocument sbml = gcm.flattenGCM(true);
		HashMap<String, Properties> speciesMap = gcm.getSpecies();
		HashMap<String, Properties> reactionMap = gcm.getInfluences();
		HashMap<String, Properties> promoterMap = gcm.getPromoters();

		species = new HashMap<String, SpeciesInterface>();
		stateMap = new HashMap<String, SpeciesInterface>();
		promoters = new HashMap<String, Promoter>();

		for (String s : speciesMap.keySet()) {
			SpeciesInterface specie = parseSpeciesData(s, speciesMap.get(s));
			species.put(specie.getId(), specie);
			stateMap.put(specie.getStateName(), specie);
		}
		
		for (String s : reactionMap.keySet()) {
			Reaction reaction = parseReactionData(s, reactionMap.get(s));			
		}
		
		for (String s : promoterMap.keySet()) {
			if (!promoters.containsKey(s)) {
				Promoter p = new Promoter();
				p.setId(s);
				promoters.put(s, p);
			}
			promoters.get(s).addProperties(promoterMap.get(s));
		}
		
		GeneticNetwork network = new GeneticNetwork(species, stateMap,
				promoters, gcm);
		
		network.setSBMLFile(gcm.getSBMLFile());
		if (sbml != null) {
			network.setSBML(sbml);
		}
		return network;
	}

	public void printFile() {
		System.out.println(data.toString());
	}
	

	public HashMap<String, SpeciesInterface> getSpecies() {
		return species;
	}

	public void setSpecies(HashMap<String, SpeciesInterface> species) {
		this.species = species;
	}

	public HashMap<String, SpeciesInterface> getStateMap() {
		return stateMap;
	}

	public void setStateMap(HashMap<String, SpeciesInterface> stateMap) {
		this.stateMap = stateMap;
	}

	public HashMap<String, Promoter> getPromoters() {
		return promoters;
	}

	public void setPromoters(HashMap<String, Promoter> promoters) {
		this.promoters = promoters;
	}

	/**
	 * Parses the reactions in the network
	 * 
	 * @param reaction
	 *            the reaction to parse
	 * @param stateNameOutput
	 *            the name of the output
	 * 
	 */
	// TODO: Match rate constants
	private Reaction parseReactionData(String reaction, Properties property) {

		String promoterName = "";
		Promoter promoter = null;
		Reaction r = new Reaction();		
		r.generateName();		

		if (property.containsKey(GlobalConstants.PROMOTER)) {
			promoterName = property.getProperty(GlobalConstants.PROMOTER);
		} else {
			promoterName = "Promoter_" + gcm.getOutput(reaction);
		}

		// Check if promoter exists. If not, create it.
		if (promoters.containsKey(promoterName)) {
			promoter = promoters.get(promoterName);
		} else {
			promoter = new Promoter();
			promoter.setId(promoterName);
			promoters.put(promoter.getId(), promoter);
		}

		if (property.containsKey(GlobalConstants.BIO) && property.get(GlobalConstants.BIO).equals("yes")) {
			Utility.print(debug, "GCMParser: Biochemical");
			r.setBiochemical(true);
		}
		
		if (property.containsKey(GlobalConstants.MAX_DIMER_STRING)) {
			r.setDimer(Integer.parseInt(property.getProperty(GlobalConstants.MAX_DIMER_STRING)));
		} else if (gcm != null) {
			r.setDimer(Integer.parseInt(gcm.getParameter((GlobalConstants.MAX_DIMER_STRING))));
		} else {
			r.setDimer(1);
		}
		
		if (property.containsKey(GlobalConstants.COOPERATIVITY_STRING)) {
			r.setCoop(Double.parseDouble(property.getProperty(GlobalConstants.COOPERATIVITY_STRING)));
		} else if (gcm != null) {
			r.setCoop(Double.parseDouble(gcm.getParameter((GlobalConstants.COOPERATIVITY_STRING))));
		} else {
			r.setCoop(1);
		}
		
		if (property.containsKey(GlobalConstants.KBIO_STRING)) {
			r.setKbio(Double.parseDouble(property.getProperty(GlobalConstants.KBIO_STRING)));
		} else if (gcm != null) {
			r.setKbio(Double.parseDouble(gcm.getParameter((GlobalConstants.KBIO_STRING))));
		} else {
			r.setKbio(0.05);
		}
		
		r.setInputState(gcm.getInput(reaction));
		r.setOutputState(gcm.getOutput(reaction));
		if (property.getProperty(GlobalConstants.TYPE).equals(GlobalConstants.ACTIVATION)) {
			r.setType("vee");
			if (property.containsKey(GlobalConstants.KACT_STRING)) {
				r.setBindingConstant(Double.parseDouble(property.getProperty(GlobalConstants.KACT_STRING)));
			} else if (gcm != null) {
				r.setBindingConstant(Double.parseDouble(gcm.getParameter((GlobalConstants.KACT_STRING))));
			} else {
				r.setBindingConstant(0.0033);
			}
		} else if (property.getProperty(GlobalConstants.TYPE).equals(GlobalConstants.REPRESSION)) {
			r.setType("tee");
			if (property.containsKey(GlobalConstants.KREP_STRING)) {
				r.setBindingConstant(Double.parseDouble(property.getProperty(GlobalConstants.KREP_STRING)));
			} else if (gcm != null) {
				r.setBindingConstant(Double.parseDouble(gcm.getParameter((GlobalConstants.KREP_STRING))));
			} else {
				r.setBindingConstant(0.5);
			}			
		} else {
			r.setType("dot");
			if (property.containsKey(GlobalConstants.KREP_STRING)) {
				r.setBindingConstant(Double.parseDouble(property.getProperty(GlobalConstants.KREP_STRING)));
			}					
		}
		promoter.addReaction(r);
		return r;
	}
	

	/**
	 * Parses the data and put it into the species
	 * 
	 * @param name
	 *            the name of the species
	 * @param properties
	 *            the properties of the species
	 */
	private SpeciesInterface parseSpeciesData(String name, Properties property) {
		SpeciesInterface species = null;

		if (property.getProperty(GlobalConstants.TYPE).equals(GlobalConstants.CONSTANT) ||
				property.getProperty(GlobalConstants.TYPE).equals(GlobalConstants.INPUT)) {
			species = new ConstantSpecies();
		} else if (property.getProperty(GlobalConstants.TYPE).equals(GlobalConstants.SPASTIC)) {
			species = new SpasticSpecies();
		} else {
			species = new BaseSpecies();
		}

		species.setId(property.getProperty(GlobalConstants.ID));
		species.setName(property.getProperty(GlobalConstants.NAME,
				property.getProperty(GlobalConstants.ID)));
		species.setStateName(property.getProperty(GlobalConstants.ID));
		species.setProperties(property);
		return species;
	}
	
	public void setParameters(HashMap<String, String> parameters) {
		gcm.setParameters(parameters);
	}

	// Holds the text of the GCM
	private StringBuffer data = null;

	private HashMap<String, SpeciesInterface> species = null;

	// StateMap, species
	private HashMap<String, SpeciesInterface> stateMap = null;

	private HashMap<String, Promoter> promoters = null;

	private GCMFile gcm = null;

	// A regex that matches information
	private static final String STATE = "(^|\\n) *([^- \\n]*) *\\[(.*)\\]";

	private static final String REACTION = "(^|\\n) *([^ \\n]*) *\\-\\> *([^ \n]*) *\\[(.*)arrowhead=([^,\\]]*)(.*)";

	private static final String PROPERTY_NUMBER = "([a-zA-Z]+)=\"([\\d]*[\\.\\d]?\\d+)\"";

	// private static final String PROPERTY_STATE = "([a-zA-Z]+)=([^\\s,.\"]+)";

	// private static final String PROPERTY_QUOTE =
	// "([a-zA-Z]+)=\"([^\\s,.\"]+)\"";

	private static final String PROPERTY_STATE = "([a-zA-Z\\s\\-]+)=([^\\s,]+)";

	// Debug level
	private boolean debug = false;
}
