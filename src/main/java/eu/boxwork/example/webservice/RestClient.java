package eu.boxwork.example.webservice;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import eu.boxwork.example.webservice.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;

public class RestClient {
    private static final Logger logger = LogManager.getLogger(RestClient.class);
    private static String SERVICE_PATH_HELLO = Service.SERVICE_PATH;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    private String base = "";
    private String SERVICE_BASE = "";
    private String userId = "";
    private String authorization = "";

    WebResource userService = null;
    private int connectTimeout = CONNECT_TIMEOUT;
    private int readTimeout = READ_TIMEOUT;


    /**
     * Default Constructor, nothing special
     * */
    public RestClient()
    {

    }

    /**
     * initialises this client
     * @param baseUrl the base URL to set
     * @param userId user id to use
     * @param authorize authorization token
     * */
    public void initialise(String baseUrl, String userId, String authorize) {
        this.base = baseUrl;
        this.userId = userId;
        this.authorization=authorize;
        this.SERVICE_BASE = this.base + SERVICE_PATH_HELLO;
    }

    /**
     * sets the timeout values, must be set before connect
     * @param connectTimeout connection time out to be set
     * @param readTimeout read timeout for each read
     * */
    public void setTimeouts(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * connect to the web resource and sets the timeouts
     * @return true if connected, else false (currently only true)
     * */
    public boolean connect() {
        Client c = Client.create();
        c.setConnectTimeout( connectTimeout );
        c.setReadTimeout( readTimeout );
        userService = c.resource( SERVICE_BASE );
        return true;
    }

    /**
     * sets the authorisation information with token
     * @param authorizeInfo e.g. bearer token incl. 'Bearer'
     * */
    public void setAuthorization(String authorizeInfo) {
        this.authorization = authorizeInfo;
    }
    /*
    * #######################################################
    * effective request calls
    * #######################################################
    * */

    /**
     * returns the response as JSONObject
     * @param stringToReturn string to put into the json by the server
     * @return the {@link JSONObject} or null in case of an error;
     * answer must contain the key "answer"
     * */
    public JSONObject requestHelloJSON(String stringToReturn) {
        try {
            // now we request a json but print it as String
            Builder b = userService.path("answer/"+stringToReturn).accept(
                    MediaType.APPLICATION_JSON
            );

            b.header("user", userId);
            b.header("authorize", authorization);

            ClientResponse response = b.get(ClientResponse.class);
            int status = response.getStatus();

            if (status==200)
            {
                String contentAsJson = response.getEntity(String.class);

                JSONObject jsonObject = new JSONObject(contentAsJson);
                if (jsonObject.has("answer"))
                {
                    return jsonObject;
                }
                else
                {
                    logger.error("answer tag not returned.");
                    return null;
                }
            }
            else
            {
                logger.error("server returned '"+status+"'");
                return null;
            }
        } catch (Exception e) {
            logger.error( "unable to connect to server: "+
                    e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * returns the response as String, uses text/plain
     * @param stringToReturn string to put into the json by the server
     * @return the {@link String} or null in case of an error;
     * */
    public String requestHello(String stringToReturn) {
        try {
            // now we request a json but print it as String
            Builder b = userService.path("answer/"+stringToReturn).accept(
                   MediaType.TEXT_PLAIN
            );

            b.header("user", userId);
            b.header("authorize", authorization);

             ClientResponse response = b.get(ClientResponse.class);
            int status = response.getStatus();

            if (status==200)
            {
                String content = response.getEntity(String.class);
                return content;
            }
            else
            {
                logger.error("server returned '"+status+"'");
                return null;
            }
        } catch (Exception e) {
            logger.error( "unable to connect to server: "+
                    e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * a small main, needs following paramters
     * 0: server ip
     * 1: server port
     * 2: single word to return
     * */
    public static void main(String[] args) {
        if (args.length!=3)
        {
            logger.error("unable to start client." +
                    " client needs 3 params: <SERVER IP> <SERVER PORT> <WORD>");
            System.exit(-1);
        }

        String ip = args[0];
        String port = args[1];
        String word = args[2];

        String serverURL = "http://"+ip+":"+port+"/"+Webserver.BASEURL;

        RestClient client = new RestClient();
        client.initialise(serverURL,"test","Bearer ABCD");

        if (client.connect())
        {
            while(true)
            {
                String helloString = client.requestHello(word);
                String helloJSONString = client.requestHelloJSON(word).toString();
                logger.info("Response as String: "+helloString);
                logger.info("Response as JSON "+helloJSONString);
            }
        }
        else {
            logger.error("unable to connect to: "+serverURL);
        }
    }
}
