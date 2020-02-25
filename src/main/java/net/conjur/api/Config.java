package net.conjur.api;

import net.conjur.util.Properties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for the Conjur API
 */
public class Config {
	private String applianceUrl;
	private String account;
	private Credentials credentials;
	private Token token;

	/**
     * @param applianceUrl the url to the Conjur instance
     * @param authnUrl the url used when authenticating to the Conjur instance
	 * @param account the Conjur organization account
	 * @param username the username/login for this Conjur identity
	 * @param password the password or api key for this Conjur identity
     */
	public Config(String applianceUrl, String authnUrl, String account, String username, String password) {
		setApplianceUrl(applianceUrl);
		setAccount(account);
		setCredentials(username, password, authnUrl);
		this.token = null;
	}

	/**
     * @param applianceUrl the url to the Conjur instance
	 * @param account the Conjur organization account
	 * @param username the username/login for this Conjur identity
	 * @param password the password or api key for this Conjur identity
     */
	public Config(String applianceUrl, String account, String username, String password) {
		this(applianceUrl, applianceUrl + "/authn", account, username, password);
	}

	/**
     * @param applianceUrl the url to the Conjur instance
	 * @param account the Conjur organization account
	 * @param tokenFile the path to the file containing session token. e.g. used for k8s authentication with the sidecar
     */
	public Config(String applianceUrl, String account, Path tokenFile){
		setApplianceUrl(applianceUrl);
		setAccount(account);
		setToken(tokenFile);
		this.credentials = null;
	}

	/**
     * @param applianceUrl the url to the Conjur instance
	 * @param account the Conjur organization account
	 * @param tokenFile the content of the conjur session token. e.g. used for k8s authentication with the sidecar.
     */
	public Config(String applianceUrl, String account, String tokenContent){
		setApplianceUrl(applianceUrl);
		setAccount(account);
		setToken(tokenContent);
		this.credentials = null;
	}

	/**
     * Creates a Config instance from the environment variables
     * {@link Constants#CONJUR_APPLIANCE_URL_PROPERTY},
     * {@link Constants#CONJUR_ACCOUNT_PROPERTY},
	 * {@link Constants#CONJUR_AUTHN_LOGIN_PROPERTY},
     * {@link Constants#CONJUR_AUTHN_API_KEY_PROPERTY},
	 * {@link Constants#CONJUR_AUTHN_TOKEN_PROPERTY},
	 * {@link Constants#CONJUR_AUTHN_TOKEN_FILE_PROPERTY} and
	 * {@link Constants#CONJUR_AUTHN_URL_PROPERTY}
     * @return the config stored from the environment variables
     */
	public static Config fromEnvironment() {
		String applianceUrl = Properties.getMandatoryProperty(Constants.CONJUR_APPLIANCE_URL_PROPERTY);
		String account = Properties.getMandatoryProperty(Constants.CONJUR_ACCOUNT_PROPERTY);

		// if using token username and password are not needed
		String tokenContent = Properties.getProperty(Constants.CONJUR_AUTHN_TOKEN_PROPERTY);
		String tokenFile = Properties.getProperty(Constants.CONJUR_AUTHN_TOKEN_FILE_PROPERTY);

		if(tokenContent != null) {
			return new Config(applianceUrl, account, tokenContent);
		}

		if(tokenFile != null) {
			return new Config(applianceUrl, account, Paths.get(tokenFile));
		}

		// Since token was not provided then authenticate
		// with username and password
		String username = Properties.getMandatoryProperty(Constants.CONJUR_AUTHN_LOGIN_PROPERTY);
		String password = Properties.getMandatoryProperty(Constants.CONJUR_AUTHN_API_KEY_PROPERTY);

		String authnUrl = Properties.getProperty(Constants.CONJUR_AUTHN_URL_PROPERTY);
		// Default to api key authentication
		if(authnUrl == null) {
			authnUrl = String.format("%s/%s", applianceUrl, "authn");
		}

		return new Config(applianceUrl, authnUrl, account, username, password);
	}

	/**
	 * Set conjur appliance url
	 * @param applianceUrl conjur appliance url
	 */
	public void setApplianceUrl(String applianceUrl) {
		// trim trailing '/'
		this.applianceUrl = applianceUrl.replaceAll("/+$", "");
	}

	/**
	 * Set conjur account
	 * @param account conjur account
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * Set credentials used to logon to conjur
	 * @param username conjur username
	 * @param password conjur password
	 * @param authnUrl conjur authentication url
	 */
	public void setCredentials(String username, String password, String authnUrl) {
		this.credentials = new Credentials(username, password, authnUrl);
	}

	/**
	 * Set the session token file
	 * @param tokenFile path to file storing the conjur session token
	 */
	public void setToken(Path tokenFile) {
		try {
			this.token = Token.fromFile(tokenFile);
		} catch(IOException e) {
			throw new IllegalArgumentException(
				String.format("Invalid token file path '%s'", tokenFile.toAbsolutePath().toString())
			);
		}
	}

	/**
	 * Set the session token content
	 * @param tokenContent conjur session token as a string
	 */
	public void setToken(String tokenContent) {
		this.token = Token.fromJson(tokenContent);
	}

	/**
	 * Get conjur appliance url
	 */
	public String getApplianceUrl() {
		return applianceUrl;
	}

	/**
	 * Get conjur account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Get conjur credentials
	 */
	public Credentials getCredentials() {
		return credentials;
	}

	/**
	 * Get conjur token
	 */
	public Token getToken() {
		return token;
	}
}