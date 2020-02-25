package net.conjur.api;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExpectedException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigTest {
	private static final String APPLIANCE_URL = "https://conjur-master";
	private static final String ACCOUNT = "conjur";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String AUTHN_URL = "https://conjur-master/authn";
	private static final Path TOKEN_FILE = Paths.get("./token_file.tmp");
	private static final String TOKEN_CONTENT = "{\"protected\":\"eyJhbGciOiJjb25qdXIub3JnL3Nsb3NpbG8vdjIiLCJraWQiOiI4N2UwMTkxMDQ2Zjg2ZjI2NjQ2OTcwYWJmMDBhZTY3NiJ9\",\"payload\":\"eyJzdWIiOiJhZG1pbiIsImlhdCI6MTU4MDQxMDA1Mn0=\",\"signature\":\"Cgz9k5a19rXMdMsRH-fZFCQAMtLf-hX59XRd9bTW_m2St2ietnmaAknNMeIuBLznmRScTJMPuyK9Xk8ak7GUkP5bgnRgTmTF_EeHNhLKD0YWlwFSXzPs8UNFnBWvJYU17fPI67LnLvi2PsvZwG6em7kDNZjA2FFITL9HgrINefOeCr6irvDPaxUkjifOzaP6-fr_ZcX6EIwuX6_b25lnDrn3Pl-63geNUZG4nLQQvf-Y5bZBmJzdUlW-J3Ubc0nQEuLZb-OeMKeD-vrv5YhKbVQwDINJDpzWdW8JPFgc9nZ37Kcr2r2O9ikPLfm4uQD2LGsOFIVzH0VaxFHclgDP6z-0NfxMRZ4pj9UURUFsDaOXDJSnguAB9dgWv8cl6DLb\"}";
	private static final Path INVALID_TOKEN_FILE = Paths.get("invalid_token_file.tmp");

	public ConfigTest() {
	}

	@Test
	public void testConfigWithUsernameAndPassword() {
		Config config = new Config(APPLIANCE_URL, ACCOUNT, USERNAME, PASSWORD);
		Assert.assertEquals(APPLIANCE_URL, config.getApplianceUrl());
		Assert.assertEquals(ACCOUNT, config.getAccount());
		Assert.assertEquals(USERNAME, config.getCredentials().getUsername());
		Assert.assertEquals(PASSWORD, config.getCredentials().getPassword());
		Assert.assertEquals(AUTHN_URL, config.getCredentials().getAuthnUrl());
	}

	@Test
	public void testConfigWithTokenFile() {
		try {
			FileWriter myWriter = new FileWriter(TOKEN_FILE.toAbsolutePath().toString());
			myWriter.write(TOKEN_CONTENT);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Config config = new Config(APPLIANCE_URL, ACCOUNT, TOKEN_FILE);
		Assert.assertEquals(APPLIANCE_URL, config.getApplianceUrl());
		Assert.assertEquals(ACCOUNT, config.getAccount());
		Assert.assertEquals(TOKEN_CONTENT, config.getToken().toString());
	}

	@Test
	public void testConfigWithTokenContent() {
		Config config = new Config(APPLIANCE_URL, ACCOUNT, TOKEN_CONTENT);

		Assert.assertEquals(APPLIANCE_URL, config.getApplianceUrl());
		Assert.assertEquals(ACCOUNT, config.getAccount());
		Assert.assertEquals(TOKEN_CONTENT, config.getToken().toString());
	}

	@Test
	public void testConfigFromEnvironment() {
		environmentVariables.set(Constants.CONJUR_APPLIANCE_URL_PROPERTY, APPLIANCE_URL);
		environmentVariables.set(Constants.CONJUR_ACCOUNT_PROPERTY, ACCOUNT);
		environmentVariables.set(Constants.CONJUR_AUTHN_LOGIN_PROPERTY, USERNAME);
		environmentVariables.set(Constants.CONJUR_AUTHN_API_KEY_PROPERTY, PASSWORD);

		Config config = Config.fromEnvironment();

		Assert.assertEquals(APPLIANCE_URL, config.getApplianceUrl());
		Assert.assertEquals(ACCOUNT, config.getAccount());
		Assert.assertNotNull(config.getCredentials());
		Assert.assertEquals(USERNAME, config.getCredentials().getUsername());
		Assert.assertEquals(PASSWORD, config.getCredentials().getPassword());
		Assert.assertEquals(AUTHN_URL, config.getCredentials().getAuthnUrl());
	}

	@Test
	public void testConfigFromEnvironmentWithoutProperties() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Conjur config property 'CONJUR_APPLIANCE_URL' was not provided");
		Config.fromEnvironment();
	}

	@Test
	public void testConfigFromEnvironmentWithOnlyApplianceUrl() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Conjur config property 'CONJUR_ACCOUNT' was not provided");
		environmentVariables.set(Constants.CONJUR_APPLIANCE_URL_PROPERTY, APPLIANCE_URL);
		Config.fromEnvironment();
	}

	@Test
	public void testConfigFromEnvironmentWithApplianceUrlAndAccount() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Conjur config property 'CONJUR_AUTHN_LOGIN' was not provided");
		
		environmentVariables.set(Constants.CONJUR_APPLIANCE_URL_PROPERTY, APPLIANCE_URL);
		environmentVariables.set(Constants.CONJUR_ACCOUNT_PROPERTY, ACCOUNT);

		Config.fromEnvironment();
	}

	@Test
	public void testConfigInvalidTokenFile() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Invalid token file path");
		
		new Config(APPLIANCE_URL, ACCOUNT, INVALID_TOKEN_FILE);
	}
	
	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
 
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
}