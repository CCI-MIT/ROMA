package edu.mit.cci.roma.web;

import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.server.CompositeServerSimulation;
import edu.mit.cci.roma.server.CompositeStepMapping;
import edu.mit.cci.roma.server.DefaultServerVariable;
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

@RooWebScaffold(path = "compositestepmappings", formBackingObject = CompositeStepMapping.class)
@RequestMapping("/compositestepmappings")
@Controller
public class CompositeStepMappingController {

     @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid CompositeStepMapping compositeStepMapping, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeStepMapping", compositeStepMapping);
            return "compositestepmappings/create";
        }
        compositeStepMapping.persist();
        return "redirect:/compositestepmappings/" + encodeUrlPathSegment(compositeStepMapping.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("compositeStepMapping", new CompositeStepMapping());
        return "compositestepmappings/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("compositestepmapping", CompositeStepMapping.findCompositeStepMapping(id));
        model.addAttribute("itemId", id);
        return "compositestepmappings/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("compositestepmappings", CompositeStepMapping.findCompositeStepMappingEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) CompositeStepMapping.countCompositeStepMappings() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("compositestepmappings", CompositeStepMapping.findAllCompositeStepMappings());
        }
        return "compositestepmappings/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid CompositeStepMapping compositeStepMapping, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("compositeStepMapping", compositeStepMapping);
            return "compositestepmappings/update";
        }
        compositeStepMapping.merge();
        return "redirect:/compositestepmappings/" + encodeUrlPathSegment(compositeStepMapping.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("compositeStepMapping", CompositeStepMapping.findCompositeStepMapping(id));
        return "compositestepmappings/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        CompositeStepMapping.findCompositeStepMapping(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/compositestepmappings?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("compositesimulations")
    public Collection<CompositeServerSimulation> populateCompositeSimulations() {
        return CompositeServerSimulation.findAllCompositeSimulations();
    }

    @ModelAttribute("defaultvariables")
    public Collection<DefaultVariable> populateDefaultVariables() {
        return DefaultServerVariable.findAllDefaultVariables();
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
