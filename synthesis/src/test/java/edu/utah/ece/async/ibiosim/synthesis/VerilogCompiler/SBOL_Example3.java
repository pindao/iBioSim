package edu.utah.ece.async.ibiosim.synthesis.VerilogCompiler;

import java.net.URI;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SystemsBiologyOntology;


/**
 * Test a+b decomposed into SBOL. 
 * @author Tramy Nguyen
 *
 */
public class SBOL_Example3 extends AbstractVerilogParserTest{

	private static SBOLDocument sbolDoc;
	private static ModuleDefinition sbolDesign;

	@BeforeClass
	public static void setupTest() {
		
		String[] cmd = {"-v", reader.getFile("contAssign4.v"), "-sbol"};
		
		VerilogCompiler compiledVerilog = reader.runCompiler(cmd);
		String vName = "contAssign4";
		WrappedSBOL sbolWrapper = compiledVerilog.getSBOLWrapper(vName);
		Assert.assertNotNull(sbolWrapper);
		sbolDoc = sbolWrapper.getSBOLDocument();
		Assert.assertEquals(1, sbolDoc.getModuleDefinitions().size());
		sbolDesign = sbolDoc.getModuleDefinition(vName, "1.0");
	}

	@Test
	public void Test_cdSize() {
		Assert.assertEquals(14, sbolDoc.getComponentDefinitions().size());
	}

	@Test
	public void Test_fcSize() {
		Assert.assertEquals(6, sbolDesign.getFunctionalComponents().size());
	}

	@Test
	public void Test_proteinSize() {
		int actualSize = 0; 
		for(ComponentDefinition cd : sbolDoc.getComponentDefinitions()) {
			if(cd.getTypes().iterator().next().equals(ComponentDefinition.PROTEIN)) {
				actualSize++;
			}
		}
		Assert.assertEquals(4, actualSize);
	}

	@Test
	public void Test_dnaSize() {
		int actualSize = 0; 
		for(ComponentDefinition cd : sbolDoc.getComponentDefinitions()) {
			if(cd.getTypes().iterator().next().equals(ComponentDefinition.DNA)) {
				actualSize++;
			}
		}
		Assert.assertEquals(10, actualSize);
	}

	@Test
	public void Test_interactionSize() {
		Assert.assertEquals(5, sbolDesign.getInteractions().size());
	}

	@Test
	public void Test_inhibitionSize() {
		int actualSize = 0; 
		for(Interaction inter : sbolDesign.getInteractions()) {
			if(inter.getTypes().iterator().next().equals(SystemsBiologyOntology.INHIBITION)) {
				actualSize++;
			}
		}
		Assert.assertEquals(3, actualSize);
	}

	@Test
	public void Test_productionSize() {
		int actualSize = 0; 
		for(Interaction inter : sbolDesign.getInteractions()) {
			if(inter.getTypes().iterator().next().equals(SystemsBiologyOntology.GENETIC_PRODUCTION)) {
				actualSize++;
			}
		}
		Assert.assertEquals(2, actualSize);
	}

	@Test
	public void Test_NOT1() {
		FunctionalComponent gate = sbolDesign.getFunctionalComponent("FC4_notGate");
		Assert.assertNotNull(gate);
		
		FunctionalComponent input = sbolDesign.getFunctionalComponent("FC3_wiredProtein");
		Assert.assertNotNull(input);
		
		FunctionalComponent output = sbolDesign.getFunctionalComponent("FC2_y");
		Assert.assertNotNull(output);
		
		Interaction inhibition = sbolDesign.getInteraction("I0");
		Assert.assertNotNull(inhibition);
		Assert.assertEquals(SystemsBiologyOntology.INHIBITION, inhibition.getTypes().iterator().next());
		Assert.assertEquals(2, inhibition.getParticipations().size());

		for(Participation p : inhibition.getParticipations()) {
			URI role = p.getRoles().iterator().next();
			if(role.equals(SystemsBiologyOntology.INHIBITOR)) {
				Assert.assertEquals(input, p.getParticipant());
			}
			else if(role.equals(SystemsBiologyOntology.INHIBITED)){
				Assert.assertEquals(gate, p.getParticipant());
			}
		}
	
		Interaction production = sbolDesign.getInteraction("I1");
		Assert.assertNotNull(production);
		Assert.assertEquals(SystemsBiologyOntology.GENETIC_PRODUCTION, production.getTypes().iterator().next());
		Assert.assertEquals(2, production.getParticipations().size());

		for(Participation p : production.getParticipations()) {
			URI role = p.getRoles().iterator().next();
			if(role.equals(SystemsBiologyOntology.PROMOTER)) {
				Assert.assertEquals(gate, p.getParticipant());
			}
			else if(role.equals(SystemsBiologyOntology.PRODUCT)){
				Assert.assertEquals(output, p.getParticipant());
			}
		}
	}

	@Test
	public void Test_NOR1() {
		FunctionalComponent gate = sbolDesign.getFunctionalComponent("FC5_norGate");
		Assert.assertNotNull(gate);
		
		FunctionalComponent in1 = sbolDesign.getFunctionalComponent("FC0_a");
		Assert.assertNotNull(in1);
		
		FunctionalComponent in2 = sbolDesign.getFunctionalComponent("FC1_b");
		Assert.assertNotNull(in2);
		
		
		FunctionalComponent out = sbolDesign.getFunctionalComponent("FC3_wiredProtein");
		Assert.assertNotNull(out);
		
		Interaction inhibition1 = sbolDesign.getInteraction("I2");
		Assert.assertNotNull(inhibition1);
		Assert.assertEquals(SystemsBiologyOntology.INHIBITION, inhibition1.getTypes().iterator().next());
		
		for(Participation p : inhibition1.getParticipations()) {
			URI role = p.getRoles().iterator().next();
			if(role.equals(SystemsBiologyOntology.INHIBITOR)) {
				Assert.assertEquals(in1, p.getParticipant());
			}
			else if(role.equals(SystemsBiologyOntology.INHIBITED)){
				Assert.assertEquals(gate, p.getParticipant());
			}
		}
		
		Interaction inhibition2 = sbolDesign.getInteraction("I3");
		Assert.assertNotNull(inhibition2);
		Assert.assertEquals(SystemsBiologyOntology.INHIBITION, inhibition2.getTypes().iterator().next());

		for(Participation p : inhibition2.getParticipations()) {
			URI role = p.getRoles().iterator().next();
			if(role.equals(SystemsBiologyOntology.INHIBITOR)) {
				Assert.assertEquals(in2, p.getParticipant());
			}
			else if(role.equals(SystemsBiologyOntology.INHIBITED)){
				Assert.assertEquals(gate, p.getParticipant());
			}
		}
		Interaction production = sbolDesign.getInteraction("I4");
		Assert.assertNotNull(production);
		Assert.assertEquals(SystemsBiologyOntology.GENETIC_PRODUCTION, production.getTypes().iterator().next());
		Assert.assertEquals(2, production.getParticipations().size());

		for(Participation p : production.getParticipations()) {
			URI role = p.getRoles().iterator().next();
			if(role.equals(SystemsBiologyOntology.PROMOTER)) {
				Assert.assertEquals(gate, p.getParticipant());
			}
			else if(role.equals(SystemsBiologyOntology.PRODUCT)){
				Assert.assertEquals(out, p.getParticipant());
			}
		}
		
	}
}