package net.conjur.util;

/**
 * Properties helpers
 */
public class Properties {

    public static String getMandatoryProperty(String name) {
        return getMandatoryProperty(name, null);
    }

	/**
	 * Retrieve properties for system properties if not 
	 * found then retrieve from environment variables.
	 * If still not found and default value is null
	 * throw IllegalArgumentException
	 */
    public static String getMandatoryProperty(String name, String def) {
        String value = getProperty(name, def);
		if (value == null) {
		    throw new IllegalArgumentException(String.format("Conjur config property '%s' was not provided", name));
		}
		return value;
	}

	public static String getProperty(String name) {
		return getProperty(name, null);
	}
	
	public static String getProperty(String name, String def) {
		String value = System.getenv(name);
		// not in environment variables look for it in system properties
		if(value == null) {
			value = System.getProperty(name);
		}
		// not found in env or system then set to default
		if(value == null) { 
            value = def; 
        }
		return value;
	}

}

