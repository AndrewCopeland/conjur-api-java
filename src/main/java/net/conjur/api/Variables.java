package net.conjur.api;

import java.util.ArrayList;
import java.util.List;

public class Variables extends Resources {	
	private ArrayList<Variable> variables;

	public Variables() {
		super();
    }

    public Variables(ArrayList<Variable> variables) {
		super((ArrayList<Resource>)(Object)variables);
		this.variables = variables;
	}

	public static Variables fromResources(Resources resources) {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		for (Resource resource : resources.asArrayList()) {
			variables.add(Variable.fromResource(resource));
		}
		return new Variables(variables);
	}

	/**
     * Return all variables as an array list
     * @return The resources in an array list
     */
    public List<Variable> asList() {
        return variables.subList(0, variables.size()-1);
    }

	@Override
	public Variable get(String fullId) {
		// This seems weird, I feel like I could do it another way
		// but the get method is returning Resources type
		return (Variable)super.get(fullId);
	}
}