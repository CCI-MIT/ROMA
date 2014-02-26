package edu.mit.cci.roma.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.ServerTuple;

@RequestMapping("/emfsimulation")
@Controller
public class EMFExcelSimulationRunController {
	
	@RequestMapping
	public String showForm(Model model, EMFParameters emfParameters) throws SimulationException {
		
		System.out.println(emfParameters);
		if (emfParameters != null) {
			// find model
	        DefaultSimulation sim = DefaultServerSimulation.findDefaultServerSimulation(32L);
	        List<Tuple> inputs = new ArrayList<Tuple>();
	        
	        for (Variable v : sim.getInputs()) {

	            ServerTuple t = new ServerTuple(v);
	            t.persist();
	            if (v.getName().equals("Input scenario")) {
	                t.setValues(new String[] {emfParameters.getScenario()});
	            }
	            else if (v.getName().equals("Input level")) {
	                t.setValues(new String[] {emfParameters.getLevel()});
	            }
	            else {
	                t.setValues(new String[] {emfParameters.getOvershootstabilization()});
	            }
	            inputs.add(t);
	        }

	        DefaultScenario defaultScenario = (DefaultScenario) sim.run(inputs);
	        Tuple t = defaultScenario.getVariableValue(sim.getOutputs().iterator().next());
	        model.addAttribute("emfParameters", emfParameters);
	        
	        model.addAttribute("outputs", t.getValues());
		}
		else {
	        model.addAttribute("emfParameters", new EMFParameters());
		}
		
		return "emfsimulation";
	}
	
	public static class EMFParameters {
		private String scenario;
		private String level;
		private String overshootstabilization;
		public String getScenario() {
			return scenario;
		}
		public void setScenario(String scenario) {
			this.scenario = scenario;
		}
		public String getLevel() {
			return level;
		}
		public void setLevel(String level) {
			this.level = level;
		}
		public String getOvershootstabilization() {
			return overshootstabilization;
		}
		public void setOvershootstabilization(String overshootstabilization) {
			this.overshootstabilization = overshootstabilization;
		}
		@Override
		public String toString() {
			return "EMFParameters [scenario=" + scenario + ", level=" + level
					+ ", overshootstabilization=" + overshootstabilization
					+ "]";
		}
		
		
	}

}
