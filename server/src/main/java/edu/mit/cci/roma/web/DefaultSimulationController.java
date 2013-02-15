package edu.mit.cci.roma.web;

import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.util.ConcreteSerializableCollection;
import edu.mit.cci.roma.util.U;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

@RooWebScaffold(path = "defaultsimulations", formBackingObject = DefaultSimulation.class)
@RequestMapping("/defaultsimulations")
@Controller
public class DefaultSimulationController {




    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder)
            throws ServletException {
        binder.registerCustomEditor(Set.class, new CustomVariableSetEditor(Set.class));

    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public DefaultSimulation showXml(@PathVariable("id") Long id, Model model) {
        return DefaultServerSimulation.findDefaultServerSimulation(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/html")
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("defaultsimulation", DefaultServerSimulation.findDefaultServerSimulation(id));
        model.addAttribute("itemId", id);
        return "defaultsimulations/show";

    }

    @RequestMapping(method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public ConcreteSerializableCollection listXml(Model model) {
        return U.wrap(DefaultServerSimulation.findAllDefaultServerSimulations());

    }

    @RequestMapping(method = RequestMethod.GET, headers = "accept=text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("defaultsimulations", DefaultServerSimulation.findDefaultServerSimulationEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) DefaultServerSimulation.countDefaultServerSimulations() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("defaultsimulations", DefaultServerSimulation.findAllDefaultServerSimulations());
        }
        addDateTimeFormatPatterns(model);
        return "defaultsimulations/list";
    }

    @ModelAttribute("defaultVariables")
    public Collection<DefaultVariable> populateDefaultVariables() {
        return DefaultServerVariable.findAllDefaultVariables();
    }

    public static class CustomVariableSetEditor extends CustomCollectionEditor {


        public CustomVariableSetEditor(Class collectionType) {
            super(collectionType);
        }

        public CustomVariableSetEditor(Class collectionType, boolean nullAsEmptyCollection) {
            super(collectionType, nullAsEmptyCollection);
        }

        @Override
        public Object convertElement(Object text) {
            return text != null ? DefaultServerVariable.findDefaultVariable(Long.parseLong(text.toString())) : text;
        }

        @Override
        public String getAsText() {
            return "FOOEY";
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid DefaultSimulation defaultSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("defaultSimulation", defaultSimulation);
            addDateTimeFormatPatterns(model);
            return "defaultsimulations/create";
        }
        ((DefaultServerSimulation)defaultSimulation).persist();
        return "redirect:/defaultsimulations/" + encodeUrlPathSegment(defaultSimulation.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("defaultSimulation", new DefaultSimulation());
        addDateTimeFormatPatterns(model);
        return "defaultsimulations/create";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid DefaultSimulation defaultSimulation, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("defaultSimulation", defaultSimulation);
            addDateTimeFormatPatterns(model);
            return "defaultsimulations/update";
        }
        ((DefaultServerSimulation)defaultSimulation).merge();
        return "redirect:/defaultsimulations/" + encodeUrlPathSegment(defaultSimulation.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("defaultSimulation", DefaultServerSimulation.findDefaultServerSimulation(id));
        addDateTimeFormatPatterns(model);
        return "defaultsimulations/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        DefaultServerSimulation.findDefaultServerSimulation(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/defaultsimulations?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    void addDateTimeFormatPatterns(Model model) {
        model.addAttribute("defaultSimulation_created_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
