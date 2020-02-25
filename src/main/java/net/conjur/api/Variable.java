package net.conjur.api;

import com.google.gson.JsonObject;

public class Variable extends Resource {
	private String secretValue;

	public Variable(String account, String id, JsonObject jsonObject) {
		super(account, ResourceKind.VARIABLE, id, jsonObject);
		secretValue = null;
	}

	public static Variable fromResource(Resource resource) {
		if (resource.getKind() != ResourceKind.VARIABLE) {
			throw new ClassCastException(String.format("Invalid resource kind '%s'. Must be of kind 'variable'", resource.getKind()));
		}
		return new Variable(resource.getAccount(), resource.getId(), resource.getJsonObject());
	}

	/**
	 * set secret value
	 * @param value value of the variables secret
	 */
	public void setSecret(String value) {
		secretValue = value;
	}

	/**
	 * get variables secret value
	 * @return variable secret value as string
	 */
	public String getSecret() {
		return secretValue;
	}
}