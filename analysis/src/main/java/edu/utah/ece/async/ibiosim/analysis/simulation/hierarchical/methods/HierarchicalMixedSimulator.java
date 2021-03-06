/*******************************************************************************
 *
 * This file is part of iBioSim. Please visit <http://www.async.ece.utah.edu/ibiosim>
 * for the latest version of iBioSim.
 *
 * Copyright (C) 2017 University of Utah
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online at <http://www.async.ece.utah.edu/ibiosim/License>.
 *
 *******************************************************************************/
package edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.methods;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;

import edu.utah.ece.async.ibiosim.analysis.properties.AnalysisProperties;
import edu.utah.ece.async.ibiosim.analysis.properties.SimulationProperties;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.HierarchicalModel;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.HierarchicalSimulation;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.HierarchicalModel.ModelType;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.states.VectorWrapper;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.util.setup.ModelSetup;
import edu.utah.ece.async.ibiosim.dataModels.util.exceptions.BioSimException;

/**
 * Hierarchical simulator that allows the coupling of ODE simulation and FBA.
 *
 * @author Leandro Watanabe
 * @author Chris Myers
 * @author <a href="http://www.async.ece.utah.edu/ibiosim#Credits"> iBioSim Contributors </a>
 * @version %I%
 */
public final class HierarchicalMixedSimulator extends HierarchicalSimulation {

  private double fbaTime;
  private HierarchicalFBASimulator fbaSim;
  private HierarchicalODERKSimulator odeSim;
  private VectorWrapper wrapper;
  // private HierarchicalSimulation ssaSim;

  /**
   * Creates an instance of a mixed simulator.
   *
   * @param properties
   *          - the analysis properties.
   * @throws IOException
   *           - if there is a problem with the model file.
   * @throws XMLStreamException
   *           - if there is a problem parsing the SBML file.
   * @throws BioSimException
   *           - if an error occur in the initialization.
   */
  public HierarchicalMixedSimulator(AnalysisProperties properties) throws IOException, XMLStreamException, BioSimException {
    super(properties, SimType.MIXED);
  }

  /**
   * Initializes the simulator.
   *
   * @param runNumber
   *          - the run index.
   * @throws IOException
   *           - if there is a problem with the model file.
   * @throws XMLStreamException
   *           - if there is a problem parsing the SBML file.
   * @throws BioSimException
   *           - if an error occur in the initialization.
   */
  public void initialize(int runNumber) throws IOException, XMLStreamException, BioSimException {
    if (!isInitialized) {
      currProgress = 0;
      setCurrentTime(0);
      this.wrapper = new VectorWrapper();

      ModelSetup.setupModels(this, ModelType.HODE, wrapper);
      computeFixedPoint();

      setupForOutput(runNumber);
      isInitialized = true;
    }

  }

  @Override
  public void simulate() throws IOException, XMLStreamException, BioSimException {

    if (!isInitialized) {
      initialize(getCurrentRun());
    }
    double nextEndTime = currentTime.getState().getValue();
    fbaTime = nextEndTime;
    double dt = getTopLevelValue("dt");
    SimulationProperties simProperties = properties.getSimulationProperties();
    double timeLimit = simProperties.getTimeLimit();
    double maxTimeStep = simProperties.getMaxTimeStep();
    while (nextEndTime < timeLimit) {
      nextEndTime = nextEndTime + maxTimeStep;

      if (nextEndTime > fbaTime) {
        nextEndTime = fbaTime;
      }

      if (nextEndTime > printTime) {
        nextEndTime = printTime;
      }

      if (nextEndTime > timeLimit) {
        nextEndTime = timeLimit;
      }

      simProperties.setTimeLimit(nextEndTime);

      if (nextEndTime <= fbaTime) {
        fbaSim.simulate();

        fbaTime = nextEndTime + dt;
      }
      computeAssignmentRules();
      odeSim.simulate();
      setCurrentTime(nextEndTime);
      printToFile();
    }

    // Restore
    simProperties.setTimeLimit(timeLimit);
    closeWriter();
  }

  @Override
  public void cancel() {}

  @Override
  public void setupForNewRun(int newRun) {
    fbaTime = 0;
  }

  /**
   * Sets the ODE part of the simulator.
   *
   * @param topmodel
   * @param odeModels
   * @throws IOException
   * @throws XMLStreamException
   * @throws BioSimException
   */
  public void createODESim(HierarchicalModel topmodel, List<HierarchicalModel> odeModels) throws IOException, XMLStreamException, BioSimException {
    odeSim = new HierarchicalODERKSimulator(this.properties, false);
    odeSim.setTopmodel(topmodel);
    odeSim.setListOfHierarchicalModels(odeModels);
  }

  /**
   * Sets the SSA part of the simulator.
   *
   * @param topmodel
   * @param submodels
   */
  public void createSSASim(HierarchicalModel topmodel, Map<String, HierarchicalModel> submodels) {
    // TODO:
  }

  /**
   * Sets the FBA part of the simulator.
   *
   * @param topmodel
   * @param model
   */
  public void createFBASim(HierarchicalModel topmodel, Model model) {
    fbaSim = new HierarchicalFBASimulator(this, topmodel);
    fbaSim.setFBA(model);
  }

  @Override
  public void printStatisticsTSD() {
    // TODO Auto-generated method stub

  }

  VectorWrapper getVectorWrapper() {
    return this.wrapper;
  }

}
