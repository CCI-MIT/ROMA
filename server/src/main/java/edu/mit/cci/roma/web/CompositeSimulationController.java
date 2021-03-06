package edu.mit.cci.roma.web;


import edu.mit.cci.roma.server.CompositeServerSimulation;
import edu.mit.cci.roma.server.CompositeStepMapping;
import edu.mit.cci.roma.server.Step;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Collection;


@RequestMapping("/compositesimulations")
@Controller
public class CompositeSimulationController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=application/xml")
    @ResponseBody
    public CompositeServerSimulation showXml(@PathVariable("id") Long id, Model model) {
        return CompositeServerSimulation.findCompositeSimulation(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/html")
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("compositesimulation", CompositeServerSimulation.findCompositeSimulation(id));
        model.addAttribute("itemId", id);
        return "compositesimulations/show";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid CompositeServerSimulation compositeSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeSimulation", compositeSimulation);
            addDateTimeFormatPatterns(model);
            return "compositesimulations/create";
        }
        compositeSimulation.persist();
        return "redirect:/compositesimulations/" + encodeUrlPathSegment(compositeSimulation.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("compositeSimulation", new CompositeServerSimulation());
        addDateTimeFormatPatterns(model);
        return "compositesimulations/create";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("compositesimulations", CompositeServerSimulation.findCompositeSimulationEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) CompositeServerSimulation.countCompositeSimulations() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("compositesimulations", CompositeServerSimulation.findAllCompositeSimulations());
        }
        addDateTimeFormatPatterns(model);
        return "compositesimulations/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid CompositeServerSimulation compositeSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeSimulation", compositeSimulation);
            addDateTimeFormatPatterns(model);
            return "compositesimulations/update";
        }
        compositeSimulation.merge();
        return "redirect:/compositesimulations/" + encodeUrlPathSegment(compositeSimulation.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("compositeSimulation", CompositeServerSimulation.findCompositeSimulation(id));
        addDateTimeFormatPatterns(model);
        return "compositesimulations/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        CompositeServerSimulation.findCompositeSimulation(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/compositesimulations?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("compositestepmappings")
    public Collection<CompositeStepMapping> populateCompositeStepMappings() {
        return CompositeStepMapping.findAllCompositeStepMappings();
    }

    @ModelAttribute("steps")
    public Collection<Step> populateSteps() {
        return Step.findAllSteps();
    }

    void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("compositeSimulation_created_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
