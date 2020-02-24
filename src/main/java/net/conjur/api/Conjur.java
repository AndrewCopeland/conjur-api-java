package net.conjur.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;

import net.conjur.api.clients.ResourceClient;

/**
 * Entry point for the Conjur API client.
 */
public class Conjur {

    private Variables variables;
    private Resources resources;
    private ResourceClient resourceClient;

    /**
     * Create a Conjur instance that uses credentials from the system properties
     */
    public Conjur() {
        this(Config.fromEnvironment());
    }

    public Conjur(Config config) {
        this.variables = new Variables();
        this.resources = new Resources();
        this.resourceClient = new ResourceClient(config, Endpoints.fromConfig(config));
    }

    /**
     * Get all resources authenticated host has access to
     * @return all Resources authenticated host has access to
     */
    public Resources getResources() {
        return getResources(null, null);
    }
    
    /**
     * Get all resources authenticated host has access to of a 
     * specific resource kind
     * @param kind kind of conjur resource
     * @return all resources of this specific kind
     */
    public Resources getResources(ResourceKind kind) {
        return getResources(kind, null);
    }

    /**
     * Get all variables authenticated host has access to
     * @return all variables 
     */
    public Variables getVariables() {
        return Variables.fromResources(getResources(ResourceKind.VARIABLE, null));
    }
    
    /**
     * Get all resources authenticated host has access to of a 
     * specific resource kind and search
     * @param kind kind of conjur resource, can be null if no specific resource kind
     * @param search search for substring contained in the resource id or annotations
     * @return all resources that match the kind and search parameters
     */
    public Resources getResources(ResourceKind kind, String search) {
        JsonArray jsonResources = resourceClient.getResources(kind, search);
        return Resources.fromJsonArray(jsonResources);
    }

    /**
     * Retrieve a secret from a conjur variable
     * @param variable conjur variable that we will retrieve the secret for
    * @return conjur variable secret as a string
     */
    public String retrieveSecret(Variable variable) {
        return retrieveSecret(variable.getId());
    }

    /**
     * Retrieve a secret from a conjur using targets variable id.
     * @param id conjur variable id
     * @return conjur variable secret as a string
     */
    public String retrieveSecret(String id) {
        return resourceClient.retrieveSecret(id);
    }

    /**
     * Retrieve a batch amount of secrets from given variables
     * @param variables conjur variables to retrieve secret of
     * @return hashmap of secret id and secret value
     */
    public Variables retrieveBatchSecrets(Variables variables) {
        HashMap<String, String> secrets = resourceClient.retrieveBatchSecrets(variables);
        for (Map.Entry<String,String> secret : secrets.entrySet()) {
            Variable variable = variables.get(secret.getKey());
            variable.setSecret(secret.getValue());
        }
        return variables;
    }

    /**
     * Retrieve a secret from a conjur using targets variable id.
     * @param variable conjur variable that is being changed
     * @param value new value of the conjur secret
     */
    public void addSecret(Variable variable, String value) {
        resourceClient.addSecret(variable.getId(), value);
    }

    /**
     * Retrieve a secret from a conjur using targets variable id.
     * @param id conjur variable id
     * @param value new value of the conjur secret
     */
    public void addSecret(String id, String value) {
        resourceClient.addSecret(id, value);
    }

    /**
     * doing it this way to keep backward compatability
     * @return this instance
     */
    public Conjur variables() {
        return this;
    }
}
