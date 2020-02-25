package net.conjur.api;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VariableTest {
	private static final String DEFAULT_ACCOUNT = "conjur";
	private static final ResourceKind DEFAULT_RESOURCE_KIND = ResourceKind.VARIABLE;
	private static final String DEFAULT_ID = "secret1";
	private static final String DEFAULT_FULL_ID = DEFAULT_ACCOUNT + ":" + DEFAULT_RESOURCE_KIND + ":" + DEFAULT_ID;


	public VariableTest() {
	}

	@Test
	public void testVariableValidFromResource() {
		Resource resource = new Resource(DEFAULT_ACCOUNT, DEFAULT_RESOURCE_KIND, DEFAULT_ID);
		Variable variable = Variable.fromResource(resource);

		Assert.assertNotNull(variable);
		Assert.assertEquals(DEFAULT_ACCOUNT, variable.getAccount());
		Assert.assertEquals(DEFAULT_RESOURCE_KIND, variable.getKind());
		Assert.assertEquals(DEFAULT_ID, variable.getId());
		Assert.assertEquals(DEFAULT_FULL_ID, variable.getFullId());
	}

	@Test
	public void testVariableInvalidKindFromResource() {
		expectedException.expect(ClassCastException.class);
		expectedException.expectMessage("Must be of kind 'variable'");

		Resource resource = new Resource(DEFAULT_ACCOUNT, ResourceKind.GROUP, DEFAULT_ID);
		Variable.fromResource(resource);
	}

	@Rule
    public ExpectedException expectedException = ExpectedException.none();
}