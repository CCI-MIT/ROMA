package edu.mit.cci.roma.pangaea.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.HttpContext;

import edu.mit.cci.roma.pangaea.core.PangaeaException;
import edu.mit.cci.roma.pangaea.corenew.PangaeaPropsUtils;
import edu.mit.cci.roma.pangaea.corenew.VensimModelResults;
import edu.mit.cci.roma.pangaea.corenew.VensimModelRunner;

@Produces("text/plain")
@Path("/run/{modelName}")
public class  PangaeaRunnerResource {

	public static Logger log = Logger.getLogger(PangaeaRunnerResource.class);

	
	@GET
	public Response runModelGet(@PathParam("modelName") String modelName, @Context HttpContext hc) {
		return doRunModel(modelName, hc.getUriInfo().getQueryParameters());
	}
	
	@POST
	public Response runModelPost(@PathParam("modelName") String modelName,
			MultivaluedMap<String, String> parameters) {
		return doRunModel(modelName, parameters);
	}
	
	private Response doRunModel(String modelName, MultivaluedMap<String, String> parameters) {
		Response r = null; 
		try {
			VensimModelRunner modelRunner = new VensimModelRunner(PangaeaPropsUtils.getModelForName(modelName));
			Map<String, String> params = new HashMap<String, String>();
			
			
			for (String key: parameters.keySet()) {
				if (! parameters.get(key).get(0).equals("NaN")) {
					params.put(key, parameters.get(key).get(0));
				}
			}
			
			VensimModelResults results = modelRunner.runTheModel(params);
			r = Response.ok(results.toString()).build();
		}
		catch (PangaeaException e) {
			log.error("Exception has been thrown by pangaea core", e);
			r = Response.serverError().build();
		}

		return r;
		
	}
	
}
