package edu.mit.cci.roma.web;

import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.CompositeScenario;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.ScenarioList;
import edu.mit.cci.roma.server.ServerTuple;
import edu.mit.cci.roma.server.Step;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
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
import java.util.Collection;

@RooWebScaffold(path = "compositescenarios", formBackingObject = CompositeScenario.class)
@RequestMapping("/compositescenarios")
@Controller
public class CompositeScenarioController {

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid CompositeScenario compositeScenario, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeScenario", compositeScenario);
            addDateTimeFormatPatterns(model);
            return "compositescenarios/create";
        }
        compositeScenario.persist();
        return "redirect:/compositescenarios/" + encodeUrlPathSegment(compositeScenario.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("compositeScenario", new CompositeScenario());
        addDateTimeFormatPatterns(model);
        return "compositescenarios/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("compositescenario", CompositeScenario.findCompositeScenario(id));
        model.addAttribute("itemId", id);
        return "compositescenarios/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("compositescenarios", CompositeScenario.findCompositeScenarioEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) CompositeScenario.countCompositeScenarios() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("compositescenarios", CompositeScenario.findAllCompositeScenarios());
        }
        addDateTimeFormatPatterns(model);
        return "compositescenarios/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid CompositeScenario compositeScenario, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeScenario", compositeScenario);
            addDateTimeFormatPatterns(model);
            return "compositescenarios/update";
        }
        compositeScenario.merge();
        return "redirect:/compositescenarios/" + encodeUrlPathSegment(compositeScenario.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("compositeScenario", CompositeScenario.findCompositeScenario(id));
        addDateTimeFormatPatterns(model);
        return "compositescenarios/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        CompositeScenario.findCompositeScenario(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/compositescenarios?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("defaultsimulations")
    public Collection<? extends DefaultSimulation> populateDefaultSimulations() {
        return DefaultServerSimulation.findAllDefaultServerSimulations();
    }

    @ModelAttribute("scenariolists")
    public Collection<ScenarioList> populateScenarioLists() {
        return ScenarioList.findAllScenarioLists();
    }

    @ModelAttribute("steps")
    public Collection<Step> populateSteps() {
        return Step.findAllSteps();
    }

    @ModelAttribute("tuples")
    public Collection<Tuple> populateTuples() {
        return ServerTuple.findAllTuples();
    }

    void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("compositeScenario_created_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
