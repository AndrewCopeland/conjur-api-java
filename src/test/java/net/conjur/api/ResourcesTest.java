package net.conjur.api;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {
	private static final String DEFAULT_ACCOUNT = "conjur";
	private static final JsonParser parser = new JsonParser();
	private  JsonArray DEFAULT_RESOURCES_JSON_ARRAY = (JsonArray)parser.parse("[{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:policy:root\",\"owner\":\"cucumber:user:admin\",\"permissions\":[],\"annotations\":{},\"policy_versions\":[{\"version\":1,\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"policy_text\":\"- !policy\\n  id: test\\n  body:\\n    - !variable\\n      id: testVariable\\n\\n    - !user\\n      id: alice\\n\\n    - !host myapp\\n\\n    - !group secrets-users\\n\\n    - !grant\\n      role: !group secrets-users\\n      member: !user alice\\n\\n    - !grant\\n      role: !group secrets-users\\n      member: !host myapp\\n\\n    - !permit\\n      resource: !variable testVariable\\n      privileges: [ read, execute, update]\\n      roles: !group secrets-users\\n\\n\\n\",\"policy_sha256\":\"2bb05c579edcec71af37195f3344b2206e1e01536aed900cf71d6a887c98e515\",\"finished_at\":\"2020-02-24T17:39:52.638+00:00\",\"id\":\"cucumber:policy:root\",\"role\":\"cucumber:user:admin\"}]},{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:policy:test\",\"owner\":\"cucumber:user:admin\",\"policy\":\"cucumber:policy:root\",\"permissions\":[],\"annotations\":{},\"policy_versions\":[]},{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:variable:test/testVariable\",\"owner\":\"cucumber:policy:test\",\"policy\":\"cucumber:policy:root\",\"permissions\":[{\"privilege\":\"read\",\"role\":\"cucumber:group:test/secrets-users\",\"policy\":\"cucumber:policy:root\"},{\"privilege\":\"execute\",\"role\":\"cucumber:group:test/secrets-users\",\"policy\":\"cucumber:policy:root\"},{\"privilege\":\"update\",\"role\":\"cucumber:group:test/secrets-users\",\"policy\":\"cucumber:policy:root\"}],\"annotations\":{},\"secrets\":[{\"version\":1,\"expires_at\":null},{\"version\":2,\"expires_at\":null}]},{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:user:alice@test\",\"owner\":\"cucumber:policy:test\",\"policy\":\"cucumber:policy:root\",\"permissions\":[],\"annotations\":{},\"restricted_to\":[]},{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:host:test/myapp\",\"owner\":\"cucumber:policy:test\",\"policy\":\"cucumber:policy:root\",\"permissions\":[],\"annotations\":{},\"restricted_to\":[]},{\"created_at\":\"2020-02-24T17:39:49.911+00:00\",\"id\":\"cucumber:group:test/secrets-users\",\"owner\":\"cucumber:policy:test\",\"policy\":\"cucumber:policy:root\",\"permissions\":[],\"annotations\":{}}]");

	public ResourcesTest() {
	}

	private Resources getDefaultResources() {
		ArrayList<Resource> resources = new ArrayList<Resource>();
		resources.add(new Resource(DEFAULT_ACCOUNT, ResourceKind.POLICY, "root"));
		resources.add(new Resource(DEFAULT_ACCOUNT, ResourceKind.VARIABLE, "secret1"));
		resources.add(new Resource(DEFAULT_ACCOUNT, ResourceKind.VARIABLE, "secret2"));
		return new Resources(resources);
	}

	@Test
	public void testResourcesGetExistent() {
		Resource resource = getDefaultResources().get(ResourceKind.VARIABLE, "secret1");

		Assert.assertNotNull(resource);
		Assert.assertEquals(DEFAULT_ACCOUNT, resource.getAccount());
		Assert.assertEquals(ResourceKind.VARIABLE, resource.getKind());
		Assert.assertEquals("secret1", resource.getId());
		Assert.assertEquals("conjur:variable:secret1", resource.getFullId());
	}

	@Test
	public void testResourcesGetExistentFullID() {
		Resource resource = getDefaultResources().get("conjur:variable:secret1");

		Assert.assertNotNull(resource);
		Assert.assertEquals(DEFAULT_ACCOUNT, resource.getAccount());
		Assert.assertEquals(ResourceKind.VARIABLE, resource.getKind());
		Assert.assertEquals("secret1", resource.getId());
		Assert.assertEquals("conjur:variable:secret1", resource.getFullId());
	}

	@Test
	public void testResourcesGetNonExistent() {
		Resource resource = getDefaultResources().get(ResourceKind.VARIABLE, "notreal");
		Assert.assertNull(resource);
	}

	@Test
	public void testResourcesFilterMultipleVariables() {
		Resources resources = getDefaultResources().filter(ResourceKind.VARIABLE);
		Assert.assertEquals(2, resources.getLength());
	}

	@Test
	public void testResourcesFilterKindAndSearch() {
		Resources resources = getDefaultResources().filter(ResourceKind.VARIABLE, "secret1");
		Assert.assertEquals(1, resources.getLength());
		Resource resource = resources.getIndex(0);
		Assert.assertEquals("conjur:variable:secret1", resource.getFullId());
	}

	@Test
	public void testResourcesFilterSinglePolicy() {
		Resources resources = getDefaultResources().filter(ResourceKind.POLICY);
		Assert.assertEquals(1, resources.getLength());
	}

	@Test
	public void testResourcesFromJsonArray() {
		Resources resources = Resources.fromJsonArray(DEFAULT_RESOURCES_JSON_ARRAY);
		Assert.assertEquals(6, resources.getLength());
		Resources variables = resources.filter(ResourceKind.VARIABLE);
		Assert.assertEquals(1, variables.getLength());
		Resources containsTest = resources.filter(null, "test");
		Assert.assertEquals(5, containsTest.getLength());
	}
}