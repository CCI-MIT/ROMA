package edu.mit.cci.roma.web;

import edu.mit.cci.roma.api.Scenario;
import edu.mit.cci.roma.api.SimulationException;
import edu.mit.cci.roma.api.Variable;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerScenario;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.ServerTuple;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 2/14/11
 * Time: 3:11 PM
 */
@Controller
@RequestMapping("/defaultsimulations/{simid}/run")
@SessionAttributes("runsim")

public class RunSimulationForm  {

    public static String USER_PARAM = "userId";

    private static Logger log = Logger.getLogger(RunSimulationForm.class);

    private Map<String, String> inputs = new HashMap<String, String>();

    private String userId;

    private Long simid;

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@PathVariable("simid") long simId, Model model) {
        DefaultSimulation sim = DefaultServerSimulation.findDefaultServerSimulation(simId);
        model.addAttribute("roma", sim);
        this.simid = sim.getId();
        model.addAttribute("form", this);
        return "defaultsimulations/runsimulation";
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional()
    public String processSubmit(@PathVariable("simid") long simId, RunSimulationForm form, Model model, HttpServletRequest request) throws SimulationException {

        Map<String,Long> remap = new HashMap<String,Long>();
        DefaultSimulation sim = null;
        Map<String,String> linputs;
        String userid = null;
        if (this.simid == null) {
            sim = DefaultServerSimulation.findDefaultServerSimulation(simId);
            linputs = new HashMap<String,String>();
            for (Object key:request.getParameterMap().keySet()) {
                String skey = key.toString();
                if (skey.equals(USER_PARAM)) {
                    userid = request.getParameterValues(USER_PARAM)[0];

                } else {
                    linputs.put(skey,request.getParameterValues(skey)[0]);
                }
            }

        } else {
            sim = DefaultServerSimulation.findDefaultServerSimulation(simid);
            userid = form.userId;
            linputs = form.inputs;
        }


        for (Variable v:sim.getInputs()) {
            if (v.getExternalName()!=null) {
                remap.put(v.getExternalName(),v.getId());
            }
        }

        List<Tuple> simInputs = new ArrayList<Tuple>();

        for (Map.Entry<String,String> ent:linputs.entrySet()) {
            DefaultVariable v = DefaultServerVariable.findDefaultVariable(remap.containsKey(ent.getKey()) ? remap.get(ent.getKey()) : Long.parseLong(ent.getKey()));
            if (v == null) throw new SimulationException("Could not identify variable "+ent.getKey());
            Tuple t = new ServerTuple(v);
            t.setValue_(ent.getValue());
            simInputs.add(t);
        }


        Scenario s = sim.run(simInputs);
        s.setUser(userid);
        ((DefaultServerScenario)s).persist();
        return "redirect:/defaultscenarios/"+s.getId();

    }


    public Map<String, String> getInputs() {
        return this.inputs;
    }

    public void setInputs(Map<String, String> inputs) {
        this.inputs = inputs;
    }

    public Long getSimid() {
        return this.simid;
    }

    public void setSimid(Long simid) {
        this.simid = simid;
    }

      public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inputs: ").append(getInputs() == null ? "null" : getInputs().size()).append(", ");
        sb.append("Simid: ").append(getSimid());
        return sb.toString();
    }



}
