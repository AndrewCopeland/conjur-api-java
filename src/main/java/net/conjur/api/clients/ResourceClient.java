package net.conjur.api.clients;

import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.conjur.api.Config;
import net.conjur.api.Credentials;
import net.conjur.api.Endpoints;
import net.conjur.api.Resource;
import net.conjur.api.ResourceProvider;
import net.conjur.api.Token;
import net.conjur.api.Variables;
import net.conjur.util.EncodeUriComponent;
import net.conjur.util.rs.TokenAuthFilter;
import net.conjur.api.ResourceKind;

/**
 * Conjur service client.
 */
public class ResourceClient implements ResourceProvider {

    private WebTarget secrets;
    private WebTarget batchSecrets;
    private final Endpoints endpoints;
    private WebTarget resources;
    
    public ResourceClient(final Credentials credentials, final Endpoints endpoints) {
        this.endpoints = endpoints;

        init(credentials);
    }

	// Build ResourceClient using a Conjur auth token
	public ResourceClient(final Token token, final Endpoints endpoints) {
        this.endpoints = endpoints;

        init(token);
    }
    
    public ResourceClient(final Config config, final Endpoints endpoints) {
        this.endpoints = endpoints;

        init(config);
	}
	
    public String retrieveSecret(String variableId) {
        Response response = secrets.path(variableId).request().get(Response.class);
        validateResponse(response);

        return response.readEntity(String.class);
    }

    public HashMap<String,String> retrieveBatchSecrets(Variables variables) {
        String queryValue = "";
        for (Resource variableResource : variables.asArrayList()) {
            queryValue += variableResource.getFullId() + ",";
        }
        Response response = batchSecrets.queryParam("variable_ids", queryValue).request().get(Response.class);
        validateResponse(response);
        String jsonBody = response.readEntity(String.class);
        return new Gson().fromJson(jsonBody, HashMap.class);
    }

    public JsonArray getResources(ResourceKind kind, String search) {
        WebTarget target = resources;
        if(kind != null) {
            target = target.queryParam("kind", kind.toString());
        }
        if(search != null) {
            target = target.queryParam("search", search);
        }
        Response response = target.request().get(Response.class);
        validateResponse(response);
        String jsonResponse = response.readEntity(String.class);
        JsonArray resources = new JsonParser().parse(jsonResponse).getAsJsonArray();
        return resources;
    }

    public void addSecret(String variableId, String secret) {
        Response response = secrets.path(EncodeUriComponent.encodeUriComponent(variableId)).request().post(Entity.text(secret), Response.class);
        validateResponse(response);
    }

    private Endpoints getEndpoints() {
        return endpoints;
    }

    private void init(Credentials credentials){
        final ClientBuilder builder = ClientBuilder.newBuilder()
                .register(new TokenAuthFilter(new AuthnClient(credentials, endpoints)));

        Client client = builder.build();

        secrets = client.target(getEndpoints().getSecretsUri());
        resources = client.target(getEndpoints().getResourcesUri());
        batchSecrets = client.target(getEndpoints().getBatchSecretsUri());
    }

    private void init(Token token){
        final ClientBuilder builder = ClientBuilder.newBuilder()
                .register(new TokenAuthFilter(new AuthnTokenClient(token)));

        Client client = builder.build();

        secrets = client.target(getEndpoints().getSecretsUri());
        resources = client.target(getEndpoints().getResourcesUri());
        batchSecrets = client.target(getEndpoints().getBatchSecretsUri());
    }

    private void init(Config config) {
        if(config.getCredentials() != null){
            init(config.getCredentials());
        } else if (config.getToken() != null){
            init(config.getToken());
        }
    }

    // TODO orenbm: Remove when we have a response filter to handle this
    private void validateResponse(Response response) {
        int status = response.getStatus();
        if (status < 200 || status >= 400) {
            String errorMessage = String.format("Error code: %d, Error message: %s", status, response.readEntity(String.class));
            throw new WebApplicationException(errorMessage, status);
        }
    }
}
