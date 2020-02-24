package net.conjur.api;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.conjur.util.Properties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Test for the Conjur class
 *
 * Before running this test, verify that:
 *  - Conjur CE is running, healthy and accessible
 *  - A Policy that provides permission for this application to access a secret is loaded
 *  - This policy has an account and a variable named 'test/testVariable' related to that account
 *  - The following system properties are loaded:
 *      * CONJUR_ACCOUNT=myorg
 *      * CONJUR_AUTHN_LOGIN=host/myhost.example.com
 *      * CONJUR_AUTHN_API_KEY=sb0ncv1yj9c4w2e9pb1a2s
 *      * CONJUR_APPLIANCE_URL=https://conjur.myorg.com/api
 */
public class ConjurTest {

    private static final String VARIABLE_KEY = "test/testVariable";
    private static final String VARIABLE_VALUE = "testSecret";
    private static final String NON_EXISTING_VARIABLE_KEY = UUID.randomUUID().toString();
    private static final String NOT_FOUND_STATUS_CODE = "404";
    private static final String UNAUTHORIZED_STATUS_CODE = "401";
    private static final String INVALID_APPLIANCE_URL = "UnknownHostException invoking http://";

    private static final String APPLIANCE_URL = "http://conjur:3000";
	private static final String ACCOUNT = "cucumber";
	private static final String USERNAME = "admin";
    private static final String PASSWORD = "3jske9f3rg1sp7xh5p862fxcejj2jr9gw22pjvngr3gcg5cy1xwj69e";
    
    public ConjurTest() {
    }

    private String getPassword() {
        if (PASSWORD.isEmpty() || PASSWORD == null) {
            return Properties.getMandatoryProperty(Constants.CONJUR_AUTHN_API_KEY_PROPERTY);
        }
        return PASSWORD;
    }

    private Config getConfig() {
        return new Config(APPLIANCE_URL, ACCOUNT, USERNAME, getPassword());
    }

    @Test
    public void testLogin() {
        Conjur conjur = new Conjur(getConfig());
        // The Conjur object is returned with an Authn client logged in
        Assert.assertNotNull(conjur);
    }

    @Test
    public void testLoginInvalidUsername() {
        expectedException.expect(WebApplicationException.class);
        expectedException.expectMessage(UNAUTHORIZED_STATUS_CODE);

        Config config = new Config(APPLIANCE_URL, ACCOUNT, "invalidUsername", getPassword());
        new Conjur(config);
    }

    @Test
    public void testLoginInvalidPassword() {
        expectedException.expect(WebApplicationException.class);
        expectedException.expectMessage(UNAUTHORIZED_STATUS_CODE);

        Config config = new Config(APPLIANCE_URL, ACCOUNT, USERNAME, "invalidPassword");
        new Conjur(config);
    }

    @Test
    public void testLoginInvalidApplianceUrl() {
        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage(INVALID_APPLIANCE_URL);

        Config config = new Config("http://invalid-url", ACCOUNT, USERNAME, getPassword());
        new Conjur(config);
    }

    @Test
    public void testAddSecretAndRetrieveSecret() {
        Conjur conjur = new Conjur(getConfig());
        
        conjur.variables().addSecret(VARIABLE_KEY, VARIABLE_VALUE);

        String retrievedSecret = conjur.variables().retrieveSecret(VARIABLE_KEY);

        Assert.assertEquals(retrievedSecret, VARIABLE_VALUE);
    }

    @Test
    public void testSetVariableWithoutVariableInPolicy() {
        expectedException.expect(WebApplicationException.class);
        expectedException.expectMessage(NOT_FOUND_STATUS_CODE);

        Conjur conjur = new Conjur(getConfig());

        conjur.variables().addSecret(NON_EXISTING_VARIABLE_KEY, VARIABLE_VALUE);
    }

    @Test
    public void testLogonWithAlterativeAuthenticator() {
        expectedException.expect(ProcessingException.class);
        expectedException.expectMessage(UNAUTHORIZED_STATUS_CODE);

        Config config = getConfig();
        config.setCredentials(USERNAME, PASSWORD, APPLIANCE_URL + "/authn-iam/test");
        Conjur conjur = new Conjur(config);
        conjur.variables().retrieveSecret(VARIABLE_KEY);
    }

    @Test
    public void testConjurGetAllResources() {
        Conjur conjur = new Conjur(getConfig());
        Resources resources = conjur.getResources();

        Assert.assertEquals(8, resources.getLength());
    }

    @Test
    public void testConjurGetAllVariableResources() {
        Conjur conjur = new Conjur(getConfig());
        Resources resources = conjur.getResources(ResourceKind.VARIABLE, null);

        Assert.assertEquals(3, resources.getLength());
    }

    @Test
    public void testConjurGetSearchForNonExistantResources() {
        Conjur conjur = new Conjur(getConfig());
        Resources resources = conjur.getResources(null, "notReal");

        Assert.assertEquals(0, resources.getLength());
    }

    @Test
    public void testConjurGetSearchForExistantResources() {
        Conjur conjur = new Conjur(getConfig());
        Resources resources = conjur.getResources(null, "test");

        Assert.assertEquals(5, resources.getLength());
    }

    @Test
    public void testConjurGetAllVariablesAndRetrieveAllVariables() {
        Conjur conjur = new Conjur(getConfig());

        // Get all variables
        Variables variables = conjur.getVariables();
        variables = conjur.retrieveBatchSecrets(variables);

        // print out all retrieved secret values
        for(Variable variable : variables.asList()) {
            String value = variable.getSecret();
            System.out.println(value);
        }

        // get each secret explicitly
        String testVariable = variables.get("cucumber:variable:test/testVariable").getSecret();
        String secret1 = variables.get("cucumber:variable:secret1").getSecret();
        String secret2 = variables.get("cucumber:variable:secret2").getSecret();

        Assert.assertEquals("testSecret", testVariable);
        Assert.assertEquals("testSecret1", secret1);
        Assert.assertEquals("testSecret2", secret2);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
}