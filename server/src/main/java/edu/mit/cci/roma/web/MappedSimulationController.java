package edu.mit.cci.roma.web;


import edu.mit.cci.roma.server.MappedServerSimulation;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RooWebScaffold(path = "mappedsimulations", formBackingObject = MappedServerSimulation.class)
@RequestMapping("/mappedsimulations")
@Controller
public class MappedSimulationController {


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public MappedServerSimulation showXml(@PathVariable("id") Long id, Model model) {
       return MappedServerSimulation.findMappedSimulation(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/html")
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("mappedsimulation", MappedServerSimulation.findMappedSimulation(id));
        model.addAttribute("itemId", id);
        return "mappedsimulations/show";
    }

}
