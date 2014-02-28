package edu.mit.cci.roma.client.comm;

import edu.mit.cci.roma.client.MetaData;
import edu.mit.cci.roma.client.Scenario;
import edu.mit.cci.roma.client.Simulation;
import edu.mit.cci.roma.client.Variable;
import edu.mit.cci.roma.client.model.transitional.AdaptedSimulation;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jintrone
 * Date: Jul 1, 2010
 * Time: 3:41:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientRepositoryCategoriesTest {


    ClientRepository repo;

    @Before
    public void setup() throws Exception {
        System.setProperty(RepositoryManager.CACHE_PROPERTY, 3 + "");

         repo = ClientRepository.instance("http://localhost:8080/roma-server");
    }

    @Test
    public void testCategories() throws IOException {
    	MetaData md = repo.getMetaData(254L);
    	
    	System.out.println(Arrays.toString(md.getCategories()));
    	
    }


}
