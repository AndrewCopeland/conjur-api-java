package net.conjur.api;

public enum ResourceKind
{
	POLICY,
	WEBSERVICE,
	VARIABLE,
	GROUP,
	LAYER,
	USER,
	HOST;

	@Override
    public String toString() {
        return name().toLowerCase();
    }
} 