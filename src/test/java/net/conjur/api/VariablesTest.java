package net.conjur.api;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VariablesTest {
	private static final String DEFAULT_ACCOUNT = "conjur";
	private static final ResourceKind DEFAULT_RESOURCE_KIND = ResourceKind.VARIABLE;
	private static final String DEFAULT_ID = "secret1";

	public VariablesTest() {
	}

	@Test
	public void testVariablesValidFromResource() {
		ArrayList<Resource> resourcesList = new ArrayList<Resource>();
		resourcesList.add(new Resource(DEFAULT_ACCOUNT, DEFAULT_RESOURCE_KIND, DEFAULT_ID));
		Resources resources = new Resources(resourcesList);
		Variables variables = Variables.fromResources(resources);

		Assert.assertEquals(1, variables.getLength());
	}

	@Test
	public void testVariablesInvalidKindFromResource() {
		expectedException.expect(ClassCastException.class);
		expectedException.expectMessage("Must be of kind 'variable'");

		ArrayList<Resource> resourcesList = new ArrayList<Resource>();
		resourcesList.add(new Resource(DEFAULT_ACCOUNT, DEFAULT_RESOURCE_KIND, DEFAULT_ID));
		resourcesList.add(new Resource(DEFAULT_ACCOUNT, ResourceKind.GROUP, DEFAULT_ID));
		Resources resources = new Resources(resourcesList);
		Variables.fromResources(resources);
	}

	@Rule
    public ExpectedException expectedException = ExpectedException.none();
}