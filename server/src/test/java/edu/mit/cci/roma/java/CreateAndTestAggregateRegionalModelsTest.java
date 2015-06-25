package edu.mit.cci.roma.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.Simulation;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.RegionalScallingFraction;
import edu.mit.cci.roma.server.ServerTuple;
import edu.mit.cci.roma.util.SimulationValidationException;
import flexjson.JSONDeserializer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml")
@Transactional
public class CreateAndTestAggregateRegionalModelsTest {

	private static final String ENROADS_SIMULATION_NAME = "Climate Interactive EnROADS (fast running simulator)";
	private static final String COMBINING_SCENARIOS_SIMULATION_NAME = "Combine scenarios simulation";
	private static final String AGGREGATED_OUTPUT_NAME = "GHG emissions";
	private static final Map<String, String> enroadsInputValues = (Map<String, String>) new JSONDeserializer()
			.deserialize("{\"734\":\"2\",\"735\":\"2.2\",\"736\":\"1\",\"737\":\"1\","
					+ "\"738\":\"0\",\"739\":\"2015\",\"740\":\"2100\",\"741\":\"0\","
					+ "\"742\":\"2015\",\"743\":\"2100\",\"744\":\"0\",\"745\":\"2015\","
					+ "\"746\":\"2100\",\"747\":\"0\",\"748\":\"2015\",\"749\":\"2100\","
					+ "\"750\":\"0\",\"751\":\"2020\",\"752\":\"0\",\"753\":\"2020\","
					+ "\"754\":\"0\",\"755\":\"2020\",\"756\":\"0\",\"757\":\"2020\","
					+ "\"758\":\"0\",\"759\":\"2015\",\"760\":\"3\",\"761\":\"0\",\"762\":\"0\","
					+ "\"763\":\"0\",\"764\":\"2015\",\"765\":\"2050\", \"region\":\"US\"}");

	@Test
	@Rollback(false)
	public void testRegionalEMF() throws SimulationValidationException, SimulationException {
		Map<String, String[]> outputsToAggregate = new HashMap<String, String[]>();
		outputsToAggregate.put("BET", new String[] { "CO2Fossil-Ind_Gtons_outBET", "CO2LandUse_Gtons_outBET" });
		outputsToAggregate.put("GCAM", new String[] { "CO2Fossil-Ind_Gtons_outGCAM", "CO2LandUse_Gtons_outGCAM" });
		outputsToAggregate.put("GRAPE", new String[] { "CO2Fossil-Ind_Gtons_outGRAPE", "CO2LandUse_Gtons_outGRAPE" });
		outputsToAggregate.put("IMAGE", new String[] { "CO2Fossil-Ind_Gtons_outIMAGE", "CO2LandUse_Gtons_outIMAGE" });
		outputsToAggregate.put("MESSAGE", new String[] { "CO2Fossil-Ind_Gtons_outMESSAGE", "CO2LandUse_Gtons_outMESSAGE" });
		outputsToAggregate.put("REMIND", new String[] { "CO2Fossil-Ind_Gtons_outREMIND", "CO2LandUse_Gtons_outREMIND" });
		outputsToAggregate.put("TIAM-WORLD", new String[] { "CO2Fossil-Ind_Gtons_outTIAM-WORLD", "CO2LandUse_Gtons_outTIAM-WORLD" });
		outputsToAggregate.put("WITCH", new String[] { "CO2Fossil-Ind_Gtons_outWITCH", "CO2LandUse_Gtons_outWITCH" });
		

		String baseModelName = "Stanford EMF27";
		Map<String, String> inputValues = (Map<String, String>) new JSONDeserializer()
				.deserialize("{\"366\":\"EMF27G7\", \"region\":\"US\"}");

		DefaultServerSimulation dss = RegionalModelTestUtils.findSimulation(baseModelName);
		List<Variable> outputs = new ArrayList<Variable>(dss.getOutputs());
		Collections.sort(outputs, new Comparator<Variable>() {

			@Override
			public int compare(Variable o1, Variable o2) {
				if (o1.getName().equals(o2.getName())) {
					return (int) (o1.getLabels().compareTo(o2.getLabels()));
				}

				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Variable v : outputs) {
			if (v.getLabels().toLowerCase().contains("emissions"))
			System.out.println(v.getId() + ", " + v.getName() + ", " + v.getExternalName() + ", " + v.getLabels());
		}

		createAndTestRegionalAggregated(baseModelName, outputsToAggregate, inputValues);
	}


	@Test
	@Rollback(false)
	public void testRegionalEnRoads() throws SimulationValidationException, SimulationException {
		Map<String, String[]> outputsToAggregate = new HashMap<String, String[]>();
		outputsToAggregate.put(AGGREGATED_OUTPUT_NAME, new String[] { "Emissions from energy",
				"Emissions from land use", "Other GHG emissions" });
		String baseModelName = ENROADS_SIMULATION_NAME;
		Map<String, String> inputValues = enroadsInputValues;

		createAndTestRegionalAggregated(baseModelName, outputsToAggregate, inputValues);

	}

	
	@Test
	@Rollback(false)
	public void testCombiningScenarios() throws SimulationValidationException, SimulationException {
		testRegionalEnRoads(); 
		DefaultServerSimulation scenarioAggregatingSimulation = findOrCreateScenarioAggregatingSimulation();

		DefaultServerSimulation regionalEnroadsSimulation = RegionalModelTestUtils
				.findSimulation(RegionalModelTestUtils.getRegionalSimulationNameForBaseName(ENROADS_SIMULATION_NAME));

		final int scenariosToCombine = 3;
		String scenarioIds = "";
		double[] aggregatedValues = null;
		Variable aggregatedVar = regionalEnroadsSimulation.findVariableWithExternalName(AGGREGATED_OUTPUT_NAME, false);
		for (int i = 0; i < scenariosToCombine; i++) {
			Scenario regionalScenario = regionalEnroadsSimulation.run(convertInputValuesMapToTuples(enroadsInputValues,
					regionalEnroadsSimulation));
			if (scenarioIds.length() > 0)
				scenarioIds += ",";
			scenarioIds += String.valueOf(regionalScenario.getId());
			String[] valuesToAdd = regionalScenario.getVariableValue(aggregatedVar).getValues();
			if (aggregatedValues == null) {
				aggregatedValues = new double[valuesToAdd.length];
				for (int j = 0; j < valuesToAdd.length; j++)
					aggregatedValues[i] = 0;
			}
			for (int j = 0; j < valuesToAdd.length; j++) {
				aggregatedValues[j] += Double.parseDouble(valuesToAdd[j]);
			}
		}

		Variable scenarioIdsVariable = scenarioAggregatingSimulation.getInputs().iterator().next();

		List<Tuple> inputs = new ArrayList<Tuple>();
		Tuple scenarioIdsTuple = new Tuple(scenarioIdsVariable);
		scenarioIdsTuple.setValues(new String[] { scenarioIds });
		inputs.add(scenarioIdsTuple);

		Scenario combinedScenario = scenarioAggregatingSimulation.run(inputs);
		String[] values = combinedScenario.getVariableValue(aggregatedVar).getValues();
		Assert.assertEquals(values.length, aggregatedValues.length);

		for (int i = 0; i < values.length; i++) {
			Assert.assertEquals(aggregatedValues[i], Double.parseDouble(values[i]), 0.01);
		}

	}

	private DefaultServerSimulation findOrCreateScenarioAggregatingSimulation() {

		return RegionalModelTestUtils.findOrCreateScenarioAggregatingSimulation(COMBINING_SCENARIOS_SIMULATION_NAME);
	}

	private void createAndTestRegionalAggregated(String baseModelName, Map<String, String[]> outputsToAggregateMap,
			Map<String, String> inputValues) throws SimulationValidationException, SimulationException {

		DefaultServerSimulation baseSimulation = RegionalModelTestUtils.findSimulation(baseModelName);
		DefaultServerSimulation regionalSimulation = RegionalModelTestUtils.findOrCreateRegionalSimulation(
				baseModelName, outputsToAggregateMap);

		Scenario baseScenario = baseSimulation.run(convertInputValuesMapToTuples(inputValues, baseSimulation));
		Scenario regionalScenario = regionalSimulation.run(convertInputValuesMapToTuples(inputValues,
				regionalSimulation));

		for (Map.Entry<String, String[]> outputsToAggregateMapEntry : outputsToAggregateMap.entrySet()) {
			String aggregatedOutputName = outputsToAggregateMapEntry.getKey();

			Variable regionalAggregatedVariable = regionalSimulation.findVariableWithExternalName(aggregatedOutputName,
					false);
			Tuple regionalAggregatedVariableValue = regionalScenario.getVariableValue(regionalAggregatedVariable);

			Variable regionalIndexingVariable = regionalAggregatedVariable.getIndexingVariable();

			String[] indexStrValues = regionalScenario.getVariableValue(regionalIndexingVariable).getValues();
			int[] index = new int[indexStrValues.length];
			double[] regionalValues = new double[index.length];
			for (int i = 0; i < index.length; i++) {
				index[i] = Integer.parseInt(indexStrValues[i]);
				regionalValues[i] = Double.parseDouble(regionalAggregatedVariableValue.getValues()[i]);
			}

			double[] aggregatedValues = new double[index.length];
			for (int i = 0; i < aggregatedValues.length; i++) {
				aggregatedValues[i] = 0;
			}
			for (String aggregatedOutput : outputsToAggregateMapEntry.getValue()) {
				Variable v = baseSimulation.findVariableWithExternalName(aggregatedOutput, false);
				// Assert.assertEquals(regionalIndexingVariable,
				// v.getIndexingVariable());
				Tuple variableTuple = baseScenario.getVariableValue(v);
				for (int i = 0; i < aggregatedValues.length; i++) {
					aggregatedValues[i] += Double.parseDouble(variableTuple.getValues()[i]);
				}
			}

			List<RegionalScallingFraction> scalingFractions = RegionalScallingFraction.findByRegion("US");
			ListIterator<RegionalScallingFraction> iterator = scalingFractions.listIterator(scalingFractions.size());

			RegionalScallingFraction currentFraction = iterator.previous();

			for (int i = index.length - 1; i >= 0; --i) {
				if (iterator.hasPrevious() && index[i] < currentFraction.getId().getYear()) {
					currentFraction = iterator.previous();
				}

				Assert.assertEquals("wrong answer for i: " + i, regionalValues[i], aggregatedValues[i]
						* currentFraction.getFraction(), 0.01);
			}
		}
	}

	private List<Tuple> convertInputValuesMapToTuples(Map<String, String> inputValues, Simulation simulation)
			throws SimulationValidationException {
		List<Tuple> simInputs = new ArrayList<Tuple>();
		for (Variable var : simulation.getInputs()) {
			String value = inputValues.get(var.getId().toString());
			if (value == null) {
				value = inputValues.get(var.getExternalName());
			}
			Tuple t = new ServerTuple(var);
			t.setValues(new String[] { value });
			simInputs.add(t);
		}
		return simInputs;
	}
}
