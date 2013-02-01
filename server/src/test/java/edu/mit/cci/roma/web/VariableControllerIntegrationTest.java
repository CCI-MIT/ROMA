package edu.mit.cci.roma.web;


import edu.mit.cci.roma.api.DataType;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.util.SpringControllerEnvironment;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: jintrone
 * Date: 3/14/11
 * Time: 11:57 AM
 */
public class VariableControllerIntegrationTest extends SpringControllerEnvironment {

    private static Logger log = Logger.getLogger(VariableControllerIntegrationTest.class);

    @Test
    public void testVariables() throws Exception {


        DefaultVariable v = new DefaultVariable();
        v.setDataType(DataType.NUM);
        v.setPrecision_(1);
        v.setName("Test");
        v.setDescription("Test");
        v.setArity(1);
        v.persist();
        String expect = String.format("<DefaultVariable Id=\"%d\"><Name>Test</Name><Description>Test</Description><Arity>1</Arity><DataType>NUM</DataType><Precision>1</Precision><indexingVariable/></DefaultVariable>",v.getId());

        request.setRequestURI("/variables/");
        request.addHeader("accept","text/xml");
        request.setMethod("GET");
        final ModelAndView mav = handle(request, response);

        Assert.assertTrue(response.getContentAsString().contains(expect));

    }

//    @Test
//    @Transactional
//    public void testSimulations() throws Exception {
//        String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><List><DefaultVariable Id=\"1\"><Name>Test</Name><Description>Test</Description><Arity>1</Arity><DataType>NUM</DataType><Precision>1</Precision></DefaultVariable></List>";
//
//
//
//        request.setRequestURI("/defaultsimulations");
//        request.addHeader("accept","text/xml");
//        request.setMethod("GET");
//        final ModelAndView mav = handle(request, response);
//        log.info(response.getContentAsString());
//        Assert.assertEquals(expect, response.getContentAsString());
//
//    }
//
//    @Test
//    @Transactional
//    public void testScenarios() throws Exception {
//        String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><List><DefaultVariable Id=\"1\"><Name>Test</Name><Description>Test</Description><Arity>1</Arity><DataType>NUM</DataType><Precision>1</Precision></DefaultVariable></List>";
//
//
//        request.setRequestURI("/defaultscenarios/");
//        request.addHeader("accept","text/xml");
//        request.setMethod("GET");
//        final ModelAndView mav = handle(request, response);
//        log.info(response.getContentAsString());
//        Assert.assertEquals(expect, response.getContentAsString());
//
//    }
//
//


}
