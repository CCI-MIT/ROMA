package edu.mit.cci.roma.web;

import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.DefaultServerVariable;
import edu.mit.cci.roma.server.ServerTuple;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RooWebScaffold(path = "tuples", formBackingObject = Tuple.class)
@RequestMapping("/tuples")
@Controller
public class TupleController {

     @RequestMapping(value = "/{id}", method = RequestMethod.GET)
     @ResponseBody
    public Tuple show(@PathVariable("id") Long id, Model model) {
//        model.addAttribute("tuple", Tuple.findTuple(id));
//        model.addAttribute("itemId", id);
//        return "tuples/show";
        return ServerTuple.findTuple(id);
    }

     @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Tuple tuple, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("tuple", tuple);
            return "tuples/create";
        }
        ((ServerTuple)tuple).persist();
        return "redirect:/tuples/" + encodeUrlPathSegment(tuple.getId().toString(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("tuple", new Tuple());
        List dependencies = new ArrayList();
        if (DefaultServerVariable.countDefaultVariables() == 0) {
            dependencies.add(new String[]{"var", "defaultvariables"});
        }
        model.addAttribute("dependencies", dependencies);
        return "tuples/create";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("tuples", ServerTuple.findTupleEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) ServerTuple.countTuples() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("tuples", ServerTuple.findAllTuples());
        }
        return "tuples/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Tuple tuple, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("tuple", tuple);
            return "tuples/update";
        }
        ((ServerTuple)tuple).merge();
        return "redirect:/tuples/" + encodeUrlPathSegment(tuple.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("tuple", ServerTuple.findTuple(id));
        return "tuples/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        ((ServerTuple)ServerTuple.findTuple(id)).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/tuples?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
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
