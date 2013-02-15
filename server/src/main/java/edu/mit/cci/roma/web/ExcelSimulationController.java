package edu.mit.cci.roma.web;

import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.excel.ExcelVariable;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.server.DefaultServerSimulation;
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

@RooWebScaffold(path = "excelsimulations", formBackingObject = ExcelSimulation.class)
@RequestMapping("/excelsimulations")
@Controller
public class ExcelSimulationController {

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid ExcelSimulation excelSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("excelSimulation", excelSimulation);
            addDateTimeFormatPatterns(model);
            return "excelsimulations/create";
        }
        excelSimulation.persist();
        return "redirect:/excelsimulations/" + encodeUrlPathSegment(excelSimulation.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("excelSimulation", new ExcelSimulation());
        addDateTimeFormatPatterns(model);
        return "excelsimulations/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("excelsimulation", ExcelSimulation.findExcelSimulation(id));
        model.addAttribute("itemId", id);
        return "excelsimulations/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("excelsimulations", ExcelSimulation.findExcelSimulationEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) ExcelSimulation.countExcelSimulations() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("excelsimulations", ExcelSimulation.findAllExcelSimulations());
        }
        addDateTimeFormatPatterns(model);
        return "excelsimulations/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid ExcelSimulation excelSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("excelSimulation", excelSimulation);
            addDateTimeFormatPatterns(model);
            return "excelsimulations/update";
        }
        excelSimulation.merge();
        return "redirect:/excelsimulations/" + encodeUrlPathSegment(excelSimulation.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("excelSimulation", ExcelSimulation.findExcelSimulation(id));
        addDateTimeFormatPatterns(model);
        return "excelsimulations/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        ExcelSimulation.findExcelSimulation(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/excelsimulations?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("excelvariables")
    public Collection<ExcelVariable> populateExcelVariables() {
        return ExcelVariable.findAllExcelVariables();
    }

    @ModelAttribute("defaultsimulations")
    public Collection<? extends DefaultSimulation> populateDefaultSimulations() {
        return DefaultServerSimulation.findAllDefaultServerSimulations();
    }

    void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("excelSimulation_creation_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
