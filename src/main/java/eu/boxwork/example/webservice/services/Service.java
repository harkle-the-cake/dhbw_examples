package eu.boxwork.example.webservice.services;
import eu.boxwork.example.webservice.Webserver;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * service end point for a device, all tokens are device tokens
 * */
@Path( Webserver.BASEURL+Service.SERVICE_PATH) // set the root path of this service
public class Service {
	public static final String SERVICE_PATH = "/service/";
		
	@GET // Method type used by the client to get the current server version
	@Path( "answer/{r}/" ) // sub path hello => http://<serverip>/rest/hello/name of user
	@Produces({ MediaType.APPLICATION_JSON  }) // we return a json to be parsed
	public Response getAnswerJSON(
			@HeaderParam("user")String user,
			@HeaderParam("authorize")String auth,
			@PathParam("r") String response)
	{
		Response b;
		if (!checkInput(user, auth, response))
		{
			b = Response.status(400)
					.header("content-type", "application/json")
					.header("Access-Control-Allow-Origin", "*")
					.entity("{\"error\":\"input not valid.\"}").build();
			return b;
		}

		if (!checkUser(user, auth))
		{
			b = Response.status(401)
					.header("content-type", "application/json")
					.header("Access-Control-Allow-Origin", "*")
					.entity("{\"error\":\"user not valid.\"}").build();
			return b;
		}

		b = Response.status(200)
				.header("content-type", "application/json")
				.header("Access-Control-Allow-Origin", "*")
				.entity("{\"answer\":\""+response+"\"}").build();
		return b;
	}

	@GET // Method type used by the client to get the current user-id for this screen
	@Path( "answer/{r}/" ) // sub path hello => http://<serverip>/rest/hello/name of user
	@Produces({ MediaType.TEXT_PLAIN  }) // we return a json to be parsed
	public Response getAnswerAsTextPlain(
			@HeaderParam("user")String user,
			@HeaderParam("authorize")String auth,
			@PathParam("r") String response)
	{
		Response b;
		if (!checkInput(user, auth, response))
		{
			b = Response.status(400)
					.header("content-type", "text/plain")
					.header("Access-Control-Allow-Origin", "*")
					.entity("input not valid").build();
			return b;
		}

		if (!checkUser(user, auth))
		{
			b = Response.status(401)
					.header("content-type", "text/plain")
					.header("Access-Control-Allow-Origin", "*")
					.entity("user not valid").build();
			return b;
		}

		b = Response.status(200)
				.header("content-type", "text/plain")
				.header("Access-Control-Allow-Origin", "*")
				.entity(response).build();
		return b;
	}


	@GET // Method type used by the client to get the current user-id for this screen
	@Path( "answer/{r}/" ) // sub path hello => http://<serverip>/rest/hello/name of user
	@Produces({ MediaType.TEXT_HTML  }) // we return a json to be parsed
	public Response getHelloAsHTML(
			@PathParam("r") String response) // we don't use any auth, but we could
	{
		Response b;
		if (!checkInput(response))
		{
			b = Response.status(400)
					.header("content-type", "text/plain")
					.header("Access-Control-Allow-Origin", "*")
					.entity("input not valid").build();
			return b;
		}

		b = Response.status(200)
				.header("content-type", "text/html")
				.header("Access-Control-Allow-Origin", "*")
				.entity(toHTML(response)).build();
		return b;
	}


	@GET // Method type used by the client to get the current user-id for this screen
	@Path( "time" ) // sub path hello => http://<serverip>/rest/hello/name of user
	@Produces({ MediaType.TEXT_HTML  }) // we return a json to be parsed
	public Response getTimeAsHTML() // we don't use any auth, but we could
	{
		Response b;
		String time = (new Date()).toString();
				b = Response.status(200)
				.header("content-type", "text/html")
				.header("Access-Control-Allow-Origin", "*")
				.entity(toHTML(time)).build();
		return b;
	}

	/*
	* ##########################################
	* HELPER and CHECKS
	* ##########################################
	* */
	/**
	 * method used to check the input data, maybe contain complex checks than here
	 * @param user user id
	 * @param auth authentication string
	 * @param response input string that should be returned as response
	 * @return true, if basic checks are ok, else false
	 * */
	private boolean checkInput(String user, String auth, String response) {
		if (user==null || auth == null || response == null)
			return false;

		if ("".equals(response)) return false; // response not be empty
		else if ("".equals(user)) return false; // user may not be empty
		else if (!auth.contains("Bearer")) return false; // basic check
		// at the end everything is fine
		else return true;
	}

	/**
	 * method used to check the input data, maybe contain complex checks than here
	 * @param response input string that should be returned as response
	 * @return true, if basic checks are ok, else false
	 * */
	private boolean checkInput(String response) {
		if (response == null)
			return false;
		else if ("".equals(response)) return false; // response not be empty
		// at the end everything is fine
		else return true;
	}

	/**
	 * checks a user
	 * @param user user id
	 * @param auth authentication string
	 * @return true, if checks are ok, else false
	 * */
	private boolean checkUser(String user, String auth) {
		if (!user.equals("test")) return false; // this may be a user in a DB
		else if (!auth.equals("Bearer ABCD")) return false; // may be a check against a IDP
		// at the end everything is fine
		else return true;
	}

	/**
	 * converts a string to a basic html
	 * @param input text to include
	 * @return a basic HTML string
	 * */
	private String toHTML(String input) {
		String ret = "<HTML><BODY>";
		ret = ret + "<div style=\"color:red;\">";
		ret = ret + input;
		ret = ret + "</div>";
		ret = ret + "</BODY></HTML>";
		return ret;
	}
}
