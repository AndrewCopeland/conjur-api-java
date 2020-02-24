package net.conjur.api;

import com.google.gson.JsonObject;

public class Resource {

	private String account;
	private ResourceKind kind;
	private String id;
	private JsonObject jsonObject;

    public Resource(String account, ResourceKind kind, String id) {
		this(account, kind, id, null);
	}

	public Resource(String account, ResourceKind kind, String id, JsonObject jsonObject) {
		this.account = account;
		this.kind = kind;
		this.id = id;
		this.jsonObject = jsonObject;
	}

	private static Resource parseFullId(String fullId) {
		String[] tokens = fullId.split(":", 3);
		if(tokens.length != 3) {
			throw new IllegalArgumentException(String.format("Failed to parse fullID into token '%s'", fullId));
		}
		return new Resource(tokens[0], ResourceKind.valueOf(tokens[1].toUpperCase()), tokens[2]);
	}
	
	public static Resource fromJsonObject(JsonObject jsonObject) {
		String fullId = jsonObject.get("id").getAsString();
		Resource newResource = parseFullId(fullId);
		newResource.setJsonObject(jsonObject);
		return newResource;
	}

	private void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getAccount() {
		return account;
	}

	public ResourceKind getKind() {
		return kind;
	}

	public String getId() {
		return id;
	}

	public String getFullId() {
		return account + ":" + kind + ":" + id;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}
}
