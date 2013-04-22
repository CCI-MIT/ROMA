package edu.mit.cci.roma.web;

import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerScenario;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.ServerTuple;
import edu.mit.cci.roma.util.ConcreteSerializableCollection;
import edu.mit.cci.roma.util.U;
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


@RequestMapping("/defaultscenarios")
@Controller
public class DefaultScenarioController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public DefaultScenario showXml(@PathVariable("id") Long id, Model model) {
        return DefaultServerScenario.findDefaultScenario(id);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  headers = "accept=text/html")
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("defaultscenario", DefaultServerScenario.findDefaultScenario(id));
        model.addAttribute("itemId", id);
        return "defaultscenarios/show";
    }

    @RequestMapping(method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public ConcreteSerializableCollection listXml(Model model) {
       return U.wrap(DefaultServerScenario.findAllDefaultScenarios());

    }



     @RequestMapping(method = RequestMethod.GET,  headers = "accept=text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("defaultscenarios", DefaultServerScenario.findDefaultScenarioEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) DefaultServerScenario.countDefaultScenarios() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("defaultscenarios", DefaultServerScenario.findAllDefaultScenarios());
        }
        addDateTimeFormatPatterns(model);
        return "defaultscenarios/list";
    }

    @RequestMapping(method = RequestMethod.POST)
       public String create(@Valid DefaultScenario defaultScenario, BindingResult result, Model model, HttpServletRequest request) {
           if (result.hasErrors()) {
               model.addAttribute("defaultScenario", defaultScenario);
               addDateTimeFormatPatterns(model);
               return "defaultscenarios/create";
           }
           ((DefaultServerScenario)defaultScenario).persist();
           return "redirect:/defaultscenarios/" + encodeUrlPathSegment(defaultScenario.getId().toString(), request);
       }

       @RequestMapping(params = "form", method = RequestMethod.GET)
       public String createForm(Model model) {
           model.addAttribute("defaultScenario", new DefaultScenario());
           addDateTimeFormatPatterns(model);
           return "defaultscenarios/create";
       }

       @RequestMapping(method = RequestMethod.PUT)
       public String update(@Valid DefaultScenario defaultScenario, BindingResult result, Model model, HttpServletRequest request) {
           if (result.hasErrors()) {
               model.addAttribute("defaultScenario", defaultScenario);
               addDateTimeFormatPatterns(model);
               return "defaultscenarios/update";
           }
           ((DefaultServerScenario)defaultScenario).merge();
           return "redirect:/defaultscenarios/" + encodeUrlPathSegment(defaultScenario.getId().toString(), request);
       }

       @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
       public String updateForm(@PathVariable("id") Long id, Model model) {
           model.addAttribute("defaultScenario", DefaultServerScenario.findDefaultScenario(id));
           addDateTimeFormatPatterns(model);
           return "defaultscenarios/update";
       }

       @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
       public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
           ((DefaultServerScenario)DefaultServerScenario.findDefaultScenario(id)).remove();
           model.addAttribute("page", (page == null) ? "1" : page.toString());
           model.addAttribute("size", (size == null) ? "10" : size.toString());
           return "redirect:/defaultscenarios?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
       }

       @ModelAttribute("defaultsimulations")
       public Collection<? extends DefaultSimulation> populateDefaultSimulations() {
           return DefaultServerSimulation.findAllDefaultServerSimulations();
       }

       @ModelAttribute("tuples")
       public Collection<ServerTuple> populateTuples() {
           return ServerTuple.findAllTuples();
       }

       void addDateTimeFormatPatterns(Model model) {
           model.addAttribute("defaultScenario_created_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
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
