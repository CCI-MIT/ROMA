package edu.mit.cci.roma.web;


import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.ManyToOneMapping;
import edu.mit.cci.roma.server.MappedServerSimulation;
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
import java.util.Arrays;
import java.util.Collection;


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

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid MappedServerSimulation mappedSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("mappedSimulation", mappedSimulation);
            addDateTimeFormatPatterns(model);
            return "mappedsimulations/create";
        }
        mappedSimulation.persist();
        return "redirect:/mappedsimulations/" + encodeUrlPathSegment(mappedSimulation.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("mappedSimulation", new  MappedServerSimulation());
        addDateTimeFormatPatterns(model);
        return "mappedsimulations/create";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("mappedsimulations",  MappedServerSimulation.findMappedSimulationEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float)  MappedServerSimulation.countMappedSimulations() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("mappedsimulations",  MappedServerSimulation.findAllMappedSimulations());
        }
        addDateTimeFormatPatterns(model);
        return "mappedsimulations/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid  MappedServerSimulation mappedSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("mappedSimulation", mappedSimulation);
            addDateTimeFormatPatterns(model);
            return "mappedsimulations/update";
        }
        mappedSimulation.merge();
        return "redirect:/mappedsimulations/" + encodeUrlPathSegment(mappedSimulation.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("mappedSimulation",  MappedServerSimulation.findMappedSimulation(id));
        addDateTimeFormatPatterns(model);
        return "mappedsimulations/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
         MappedServerSimulation.findMappedSimulation(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/mappedsimulations?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("defaultsimulations")
    public Collection<DefaultServerSimulation> populateDefaultSimulations() {
        return DefaultServerSimulation.findAllDefaultServerSimulations();
    }

    @ModelAttribute("defaultvariables")
    public Collection<DefaultServerVariable> populateDefaultVariables() {
        return DefaultServerVariable.findAllDefaultVariables();
    }

    @ModelAttribute("manytoonemappings")
    public Collection<ManyToOneMapping> populateManyToOneMappings() {
        return Arrays.asList(ManyToOneMapping.class.getEnumConstants());
    }

    void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("mappedSimulation_created_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
