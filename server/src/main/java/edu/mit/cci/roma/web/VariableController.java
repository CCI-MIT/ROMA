package edu.mit.cci.roma.web;

import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.util.ConcreteSerializableCollection;
import edu.mit.cci.roma.util.U;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
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

@RooWebScaffold(path = "variables", formBackingObject = DefaultVariable.class)
@RequestMapping("/variables")
@Controller
public class VariableController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public DefaultVariable showXml(@PathVariable("id") Long id, Model model) {
        return DefaultServerVariable.findDefaultVariable(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "accept=text/html")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("defaultVariable", DefaultServerVariable.findDefaultVariable(id));
        model.addAttribute("itemId", id);
        return "variables/show";
    }

    @RequestMapping(method = RequestMethod.GET, headers = "accept=text/xml")
    @ResponseBody
    public ConcreteSerializableCollection list(Model model) {
        return U.wrap(DefaultServerVariable.findAllDefaultVariables());
    }

    @RequestMapping(method = RequestMethod.GET, headers = "accept=text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("defaultVariables", DefaultServerVariable.findDefaultVariableEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) DefaultServerVariable.countDefaultVariables() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("defaultVariables", DefaultServerVariable.findAllDefaultVariables());
        }
        return "variables/list";
    }

     @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid DefaultVariable defaultVariable, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("defaultVariable", defaultVariable);
            return "variables/create";
        }
        ((DefaultServerVariable)defaultVariable).persist();
        return "redirect:/variables/" + encodeUrlPathSegment(defaultVariable.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("defaultVariable", new DefaultVariable());
        return "variables/create";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid DefaultVariable defaultVariable, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("defaultVariable", defaultVariable);
            return "variables/update";
        }
        ((DefaultServerVariable)defaultVariable).merge();
        return "redirect:/variables/" + encodeUrlPathSegment(defaultVariable.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("defaultVariable", DefaultServerVariable.findDefaultVariable(id));
        return "variables/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        ((DefaultServerVariable)DefaultServerVariable.findDefaultVariable(id)).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/variables?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }

    @ModelAttribute("datatypes")
    public Collection<DataType> populateDataTypes() {
        return Arrays.asList(DataType.class.getEnumConstants());
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
