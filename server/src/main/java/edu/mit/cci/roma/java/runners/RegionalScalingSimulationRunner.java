package edu.mit.cci.roma.java.runners;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.java.JavaSimulationRunner;
import edu.mit.cci.roma.server.RegionalScallingFraction;

public class RegionalScalingSimulationRunner implements JavaSimulationRunner {

	public static final String REGION_PARAM = "region";


	@Override
	public void init(Map properties) throws SimulationException {
		
	}

	@Override
	public Map<Variable, Object[]> run(List<Tuple> params, Set<Variable> outputs) throws SimulationException {
		
		Tuple regionNameTuple = getRegionFromParams(params);
		if (regionNameTuple == null) {
			throw new SimulationException(String.format("Can't find region parameter %s", REGION_PARAM));
		}
		String regionName = regionNameTuple.getValues()[0];
		
		List<RegionalScallingFraction> scallingVectorList = RegionalScallingFraction.findByRegion(regionName);
		if (scallingVectorList == null || scallingVectorList.isEmpty()) {
			throw new SimulationException(String.format("Can't find scaling data for region %s", regionName));
		}
		
		
		Collections.sort(scallingVectorList);
		RegionalScallingFraction[] scalingVector = new RegionalScallingFraction[scallingVectorList.size()]; 
		scallingVectorList.toArray(scalingVector);
		
		
		Map<Variable, Object[]> result = new HashMap<Variable, Object[]>();
		
		for (Tuple param: params) {
			if (outputs.contains(param.getVar())) {
				result.put(param.getVar(), param.getValues());
			}
			
			if (param == regionNameTuple) continue;
			
			Variable indexingVariable = param.getVar().getIndexingVariable();
			if (indexingVariable == null) continue;
			if (param.getVar().getDataType() != DataType.NUM) continue;
			Tuple indexTuple = null;
			for (Tuple indexParam: params) {
				if (indexingVariable == indexParam.getVar()) {
					indexTuple = indexParam;
					break;
				}
			}
			if (indexTuple == null) {
				throw new SimulationException("Can't find index tuple for variable: " + param.getVar().getId());
			}
			
			int[] indexYears = new int[indexTuple.getValues().length];
			for (int i = 0; i < indexTuple.getValues().length; i++) {
				indexYears[i] = Integer.parseInt(indexTuple.getValues()[i]);
			}
			
			int scalingVectorIdx = 1;
			double fraction = scalingVector[0].getFraction();
			
			Double newValues[] = new Double[param.getValues().length];
			for (int i = 0; i < param.getValues().length; i++) {
				if (scalingVectorIdx < scalingVector.length && scalingVector[scalingVectorIdx].getId().getYear() <= indexYears[i]) {
					fraction = scalingVector[scalingVectorIdx].getFraction();
					scalingVectorIdx++;
				}
				
				newValues[i] = Double.parseDouble(param.getValues()[i]) * fraction;
			}
			result.put(param.getVar(), newValues);
		}
		
		return result;	
	}


	private Tuple getRegionFromParams(List<Tuple> params) {
		for (Tuple param: params) {
			if (param.getVar().getName().equals(REGION_PARAM)) {
				return param;
			}
		}
		return null;
	}

	@Override
	public void prePersistScenario(Scenario scenario) throws SimulationException {
		
	}


	@Override
	public Simulation getResultSimulation(Simulation defaultServerSimulation) {
		return defaultServerSimulation;
	}
}
