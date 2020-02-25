package net.conjur.api;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class EndpointsTest {
	private static final String APPLIANCE_URL = "https://conjur-master";
	private static final String ACCOUNT = "conjur";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String AUTHN_URL = "https://conjur-master/authn/conjur";
	private static final String IAM_AUTHN_URL = "https://conjur-master/authn-iam/test";
	private static final String SECRETS_URL = "https://conjur-master/secrets/conjur/variable";

	private static final URI AUTHN_URI = URI.create(AUTHN_URL);
	private static final URI SECRETS_URI = URI.create(SECRETS_URL);
	private static final URI AUTHN_IAM_URI = URI.create(IAM_AUTHN_URL + "/" + ACCOUNT);



	public EndpointsTest() {
	}

	@Test
	public void testAuthnEndpoint() {
		Config config = new Config(APPLIANCE_URL, ACCOUNT, USERNAME, PASSWORD);
		Endpoints endpoints = Endpoints.fromConfig(config);
		Assert.assertEquals(AUTHN_URI, endpoints.getAuthnUri());
	}

	@Test
	public void testAuthnIamEndpoint() {
		Config config = new Config(APPLIANCE_URL, IAM_AUTHN_URL, ACCOUNT, USERNAME, PASSWORD);
		Endpoints endpoints = Endpoints.fromConfig(config);
		Assert.assertEquals(AUTHN_IAM_URI, endpoints.getAuthnUri());
	}

	@Test
	public void testSecretsEndpoint() {
		Config config = new Config(APPLIANCE_URL, ACCOUNT, USERNAME, PASSWORD);
		Endpoints endpoints = Endpoints.fromConfig(config);
		Assert.assertEquals(SECRETS_URI, endpoints.getSecretsUri());
	}
}