package edu.utah.ece.async.ibiosim.synthesis.GateGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

import edu.utah.ece.async.ibiosim.synthesis.TestingFiles;
import edu.utah.ece.async.ibiosim.synthesis.GeneticGates.DecomposedGraph;
import edu.utah.ece.async.ibiosim.synthesis.GeneticGates.DecomposedGraphNode;
import edu.utah.ece.async.ibiosim.synthesis.GeneticGates.GateIdentifier;
import edu.utah.ece.async.ibiosim.synthesis.GeneticGates.GeneticGate;
import edu.utah.ece.async.ibiosim.synthesis.GeneticGates.NOTGate;

public class DecomposedNotGate_Test {

	private static DecomposedGraph decomposedGraph ;
	
	@BeforeClass
	public static void setupTest() throws SBOLValidationException, IOException, SBOLConversionException, GateGenerationExeception { 
		SBOLDocument inFile = SBOLReader.read(new File(TestingFiles.NOT_LibSize1));
		Assert.assertEquals(1,  inFile.getRootModuleDefinitions().size());
		
		ModuleDefinition md = inFile.getRootModuleDefinitions().iterator().next();
		GateIdentifier sortInstance = new GateIdentifier(inFile, md);
		GeneticGate gate = sortInstance.getIdentifiedGate();
		Assert.assertTrue(gate instanceof NOTGate);
		
		NOTGate notGate = (NOTGate) gate;
		decomposedGraph = notGate.getDecomposedGraph();
		
	}
	
	@Test
	public void Test_graphSize() {
		Assert.assertEquals(3, decomposedGraph.topologicalSort().size());
	}
	
	@Test
	public void Test_outputNode() {
		DecomposedGraphNode n = decomposedGraph.getRootNode();
		Assert.assertEquals(1, n.getChildrenNodeList().size());
		Assert.assertEquals(0, n.getParentNodeList().size());
		URI expectedUri = URI.create("https://synbiohub.programmingbiology.org/public/Eco1C1G1T1/PsrA_protein_production/PsrA/1");
		
	}
	
	@Test
	public void Test_inputNodes() {
		for(DecomposedGraphNode n : decomposedGraph.getLeafNodes()) {
			Assert.assertEquals(1, n.getParentNodeList().size());
			Assert.assertEquals(0, n.getChildrenNodeList().size());
		}
	}
	
	@Test
	public void Test_tuNode() {
		DecomposedGraphNode n = decomposedGraph.getRootNode().getChildrenNodeList().iterator().next();
		Assert.assertEquals(1, n.getChildrenNodeList().size());
		Assert.assertEquals(1, n.getParentNodeList().size());
	}
}
