package edu.mit.cci.roma.web;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.server.DefaultServerScenario;
import edu.mit.cci.roma.server.ScenarioList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;


@RequestMapping("/scenariolists")
@Controller
public class ScenarioListController {

     @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid ScenarioList scenarioList, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("scenarioList", scenarioList);
            return "scenariolists/create";
        }
        scenarioList.persist();
        return "redirect:/scenariolists/" + encodeUrlPathSegment(scenarioList.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("scenarioList", new ScenarioList());
        return "scenariolists/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("scenariolist", ScenarioList.findScenarioList(id));
        model.addAttribute("itemId", id);
        return "scenariolists/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("scenariolists", ScenarioList.findScenarioListEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) ScenarioList.countScenarioLists() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("scenariolists", ScenarioList.findAllScenarioLists());
        }
        return "scenariolists/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid ScenarioList scenarioList, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("scenarioList", scenarioList);
            return "scenariolists/update";
        }
        scenarioList.merge();
        return "redirect:/scenariolists/" + encodeUrlPathSegment(scenarioList.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("scenarioList", ScenarioList.findScenarioList(id));
        return "scenariolists/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        ScenarioList.findScenarioList(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/scenariolists?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("datatypes")
    public Collection<DataType> populateDataTypes() {
        return Arrays.asList(DataType.class.getEnumConstants());
    }

    @ModelAttribute("defaultscenarios")
    public Collection<DefaultServerScenario> populateDefaultScenarios() {
        return DefaultServerScenario.findAllDefaultScenarios();
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
