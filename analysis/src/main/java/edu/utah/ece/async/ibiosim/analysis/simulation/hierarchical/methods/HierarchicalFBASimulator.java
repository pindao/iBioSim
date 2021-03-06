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

import java.util.HashMap;
import java.util.Map;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;

import edu.utah.ece.async.ibiosim.analysis.fba.FluxBalanceAnalysis;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.HierarchicalModel;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.HierarchicalSimulation;
import edu.utah.ece.async.ibiosim.analysis.simulation.hierarchical.math.HierarchicalNode;

/**
 * Flux-Balance analyzer.
 *
 * @author Leandro Watanabe
 * @author Chris Myers
 * @author <a href="http://www.async.ece.utah.edu/ibiosim#Credits"> iBioSim Contributors </a>
 * @version %I%
 */
class HierarchicalFBASimulator extends HierarchicalSimulation {

  private FluxBalanceAnalysis fba;
  private final HashMap<String, Double> values;

  public HierarchicalFBASimulator(HierarchicalSimulation simulation, HierarchicalModel topmodel) {
    super(simulation);
    setTopmodel(topmodel);
    values = new HashMap<>();
  }

  /**
   * Sets the FBA model.
   *
   * @param model
   *          - the FBA model.
   */
  public void setFBA(Model model) {
    for (Parameter parameter : model.getListOfParameters()) {
      values.put(parameter.getId(), parameter.getValue());
    }
    fba = new FluxBalanceAnalysis(model, 1e-9);
    this.addObservable(fba);
  }

  @Override
  public void simulate() {
    // TODO: check return value of fba
    getState();
    fba.setBoundParameters(values);
    fba.PerformFluxBalanceAnalysis();
    retrieveFbaState();
  }

  private void retrieveFbaState() {
    Map<String, Double> flux = fba.getFluxes();
    for (String reaction : flux.keySet()) {
      topmodel.getNode(reaction).setValue(topmodel.getIndex(), flux.get(reaction));
    }
  }

  @Override
  public void cancel() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setupForNewRun(int newRun) {
    // TODO Auto-generated method stub

  }

  @Override
  public void printStatisticsTSD() {
    // TODO Auto-generated method stub

  }

  /**
   * Retrieves state from FBA.
   */
  void getState() {
    for (String name : values.keySet()) {
      HierarchicalNode node = topmodel.getNode(name);
      double value = node.getValue(topmodel.getIndex());
      if (Math.abs(value) <= 1e-6) {
        value = 0;
      }
      values.put(name, value);
    }
  }

}
