package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import com.chinamobile.social.server.ActionsDoLoginRequestBuilder;
import com.chinamobile.social.server.ActionsDoValidateTokenRequestBuilder;
import com.chinamobile.social.server.ActionsRequestBuilders;
import com.chinamobile.social.server.Contact;
import com.chinamobile.social.server.ContactArray;
import com.chinamobile.social.server.ContactDoFindAcquaintanceContactsRequestBuilder;
import com.chinamobile.social.server.ContactDoGetContactsRequestBuilder;
import com.chinamobile.social.server.ContactRequestBuilders;
import com.chinamobile.social.server.User;
import com.chinamobile.social.server.UserCreateRequestBuilder;
import com.chinamobile.social.server.UserRequestBuilders;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.common.IdResponse;

import java.util.Collections;

public class Application extends Controller {
    // Create an HttpClient and wrap it in an abstraction layer
    private static final HttpClientFactory http = new HttpClientFactory();
    private static final Client r2Client = new TransportClientAdapter(
            http.getClient(Collections.<String, String> emptyMap()));
    private static final String BASE_URL = "http://localhost:8080/social/";
    private static RestClient restClient = new RestClient(r2Client, BASE_URL);

	public static Result index() {
		contact("7346766278");
		return ok(views.html.index.render());
	}
	
	public static void contact(String phone)  {
		com.chinamobile.social.server.Contact[] contacts = findContacts(phone);
		if(contacts!= null && contacts.length > 0)
			ok(contacts[0].getFriendPhone());
		else
			ok("doesn't find any contact");
			
		
	}
	
    public static com.chinamobile.social.server.Contact[] findContacts(String ownerPhone) {
        try {
            ContactDoGetContactsRequestBuilder contactsRequestBuilder = new ContactRequestBuilders().actionGetContacts();
            ActionRequest<ContactArray> contactArrayActionRequest = contactsRequestBuilder.phoneParam(ownerPhone).build();

            ResponseFuture<ContactArray> responseFuture = restClient.sendRequest(contactArrayActionRequest);
            Response<ContactArray> response = responseFuture.getResponse();

            return response.getEntity().toArray(new Contact[response.getEntity().size()]);
        } catch (RemoteInvocationException ex) {
            ex.printStackTrace();
        }

        return null;       
    }
}