package edu.mit.cci.roma.web;

import edu.mit.cci.roma.excel.ExcelRunnerStrategy;
import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.excel.ExcelVariable;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.server.DefaultServerSimulation;
import edu.mit.cci.roma.server.DefaultServerVariable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@RequestMapping("/defaultsimulations/createexcel")
@Controller
public class ExcelSimulationForm {


    private byte[] filedata;

    private List<String> inputRanges = new ArrayList<String>();

    private List<String> outputRanges = new ArrayList<String>();

    private List<String> inputWorksheetNames = new ArrayList<String>();

    private List<String> outputWorksheetNames = new ArrayList<String>();

    private List<DefaultVariable> inputVars = new ArrayList<DefaultVariable>();

    private List<DefaultVariable> outputVars = new ArrayList<DefaultVariable>();

    @Size(max = 75)
    private String simulationName;

    @Size(max = 1024)
    private String simulationDescription;


    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder)
            throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }


    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid ExcelSimulationForm eSim, BindingResult result, Model model, @RequestParam("filedata") MultipartFile fildata, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("excelSimulationForm", new ExcelSimulationForm());
            return "defaultsimulations/createexcel";
        }
        DefaultSimulation sim = createSimulation(eSim);
        return "redirect:/defaultsimulations/" + encodeUrlPathSegment(sim.getId().toString(), request);
    }

    private DefaultSimulation createSimulation(ExcelSimulationForm form) {
        DefaultServerSimulation sim = new DefaultServerSimulation();
        sim.getInputs().addAll(form.inputVars);
        sim.getOutputs().addAll(form.outputVars);
        sim.setSimulationVersion(1l);
        sim.setCreated(new Date());
        sim.setName(form.simulationName);
        sim.setDescription(form.simulationDescription);
        sim.persist();


        ExcelSimulation esim = new ExcelSimulation();
        esim.setSimulation(sim);
        esim.persist();
        int i = 0;
        for (DefaultVariable v : form.inputVars) {
            ExcelVariable var = new ExcelVariable();
            var.setWorksheetName(form.inputWorksheetNames.get(i));
            var.setCellRange(form.inputRanges.get(i));
            var.setSimulationVariable((DefaultServerVariable) v);
            var.setExcelSimulation(esim);
            var.persist();
            i++;

        }

        i = 0;
        for (DefaultVariable v : form.outputVars) {
            ExcelVariable var = new ExcelVariable();
            var.setWorksheetName(form.outputWorksheetNames.get(i));
            var.setCellRange(form.outputRanges.get(i));
            var.setSimulationVariable((DefaultServerVariable) v);
            var.setExcelSimulation(esim);
            var.persist();
            i++;
        }
        esim.persist();
        esim.setFile(form.filedata);
        esim.persist();
        sim.setUrl(ExcelSimulation.EXCEL_URL + "/" + esim.getId());
        ExcelRunnerStrategy strategy = new ExcelRunnerStrategy(sim);
        return sim;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String createForm(Model model) {
        model.addAttribute("excelSimulationForm", new ExcelSimulationForm());
        return "defaultsimulations/createexcel";
    }

    @ModelAttribute("variables")
    public Collection<DefaultServerVariable> populateVariables() {
        return DefaultServerVariable.findAllDefaultVariables();
    }


    String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }


    public byte[] getFiledata() {
        return this.filedata;
    }

    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

    public List<String> getInputRanges() {
        return this.inputRanges;
    }

    public void setInputRanges(List<String> inputRanges) {
        this.inputRanges = inputRanges;
    }

    public List<String> getOutputRanges() {
        return this.outputRanges;
    }

    public void setOutputRanges(List<String> outputRanges) {
        this.outputRanges = outputRanges;
    }

    public List<String> getInputWorksheetNames() {
        return this.inputWorksheetNames;
    }

    public void setInputWorksheetNames(List<String> inputWorksheetNames) {
        this.inputWorksheetNames = inputWorksheetNames;
    }

    public List<String> getOutputWorksheetNames() {
        return this.outputWorksheetNames;
    }

    public void setOutputWorksheetNames(List<String> outputWorksheetNames) {
        this.outputWorksheetNames = outputWorksheetNames;
    }

    public List<DefaultVariable> getInputVars() {
        return this.inputVars;
    }

    public void setInputVars(List<DefaultVariable> inputVars) {
        this.inputVars = inputVars;
    }

    public List<DefaultVariable> getOutputVars() {
        return this.outputVars;
    }

    public void setOutputVars(List<DefaultVariable> outputVars) {
        this.outputVars = outputVars;
    }

    public String getSimulationName() {
        return this.simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getSimulationDescription() {
        return this.simulationDescription;
    }

    public void setSimulationDescription(String simulationDescription) {
        this.simulationDescription = simulationDescription;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Filedata: ").append(java.util.Arrays.toString(getFiledata())).append(", ");
        sb.append("InputRanges: ").append(getInputRanges() == null ? "null" : getInputRanges().size()).append(", ");
        sb.append("OutputRanges: ").append(getOutputRanges() == null ? "null" : getOutputRanges().size()).append(", ");
        sb.append("InputWorksheetNames: ").append(getInputWorksheetNames() == null ? "null" : getInputWorksheetNames().size()).append(", ");
        sb.append("OutputWorksheetNames: ").append(getOutputWorksheetNames() == null ? "null" : getOutputWorksheetNames().size()).append(", ");
        sb.append("InputVars: ").append(getInputVars() == null ? "null" : getInputVars().size()).append(", ");
        sb.append("OutputVars: ").append(getOutputVars() == null ? "null" : getOutputVars().size()).append(", ");
        sb.append("SimulationName: ").append(getSimulationName()).append(", ");
        sb.append("SimulationDescription: ").append(getSimulationDescription());
        return sb.toString();
    }
}
