package net.conjur.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Assert;
import org.junit.Test;

public class ResourceTest {

	private static final String DEFAULT_ACCOUNT = "conjur";
	private static final ResourceKind DEFAULT_KIND = ResourceKind.POLICY;
	private static final String DEFAULT_ID = "root";
	private static final String DEFAULT_FULL_ID = DEFAULT_ACCOUNT + ":" + DEFAULT_KIND + ":" + DEFAULT_ID;
	private static final JsonParser parser = new JsonParser();

	private static final JsonObject DEFAULT_JSON_OBJECT = (JsonObject)parser.parse("{\"created_at\":\"2020-02-24T16:37:05.786+00:00\",\"id\":\"conjur:variable:team1/secret\",\"owner\":\"conjur:policy:team1\",\"policy\":\"conjur:policy:root\",\"permissions\":[{\"privilege\":\"read\",\"role\":\"conjur:group:team1/jit-jobs\",\"policy\":\"conjur:policy:root\"},{\"privilege\":\"execute\",\"role\":\"conjur:group:team1/jit-jobs\",\"policy\":\"conjur:policy:root\"}],\"annotations\":{},\"secrets\":[{\"version\":1,\"expires_at\":null}]}");
	private static final ResourceKind DEFAULT_JSON_OBJECT_RESOURCE_KIND = ResourceKind.VARIABLE;
	private static final String DEFAULT_JSON_OBJECT_ID = "team1/secret";
	private static final String DEFAULT_JSON_FULL_ID = DEFAULT_ACCOUNT + ":" + DEFAULT_JSON_OBJECT_RESOURCE_KIND + ":" + DEFAULT_JSON_OBJECT_ID;

	public ResourceTest() {
	}

	@Test
	public void testResourceGetAccount() {
		Resource resource = new Resource(DEFAULT_ACCOUNT, DEFAULT_KIND, DEFAULT_ID);
		Assert.assertEquals(DEFAULT_ACCOUNT, resource.getAccount());
	}

	@Test
	public void testResourceGetKind() {
		Resource resource = new Resource(DEFAULT_ACCOUNT, DEFAULT_KIND, DEFAULT_ID);
		Assert.assertEquals(DEFAULT_KIND, resource.getKind());
	}

	@Test
	public void testResourceGetID() {
		Resource resource = new Resource(DEFAULT_ACCOUNT, DEFAULT_KIND, DEFAULT_ID);
		Assert.assertEquals(DEFAULT_ID, resource.getId());
	}

	@Test
	public void testResourceGetFullID() {
		Resource resource = new Resource(DEFAULT_ACCOUNT, DEFAULT_KIND, DEFAULT_ID);
		Assert.assertEquals(DEFAULT_FULL_ID, resource.getFullId());
	}

	@Test
	public void testResourceFromJsonObject() {
		Resource resource = Resource.fromJsonObject(DEFAULT_JSON_OBJECT);

		Assert.assertEquals(DEFAULT_ACCOUNT, resource.getAccount());
		Assert.assertEquals(DEFAULT_JSON_OBJECT_RESOURCE_KIND, resource.getKind());
		Assert.assertEquals(DEFAULT_JSON_OBJECT_ID, resource.getId());
		Assert.assertEquals(DEFAULT_JSON_FULL_ID, resource.getFullId());
	}
}