package net.conjur.api;

import net.conjur.util.Args;

import java.io.Serializable;
import java.net.URI;

/**
 * An <code>Endpoints</code> instance provides endpoint URIs for the various conjur services.
 */
public class Endpoints implements Serializable {
    private final URI authnUri;
    private final URI secretsUri;
    private final URI resourcesUri;
    private final URI batchSecretsUri;

    public Endpoints(final URI authnUri, final URI secretsUri, final URI resourcesUri, final URI batchSecretsUri){
        this.authnUri = Args.notNull(authnUri, "authnUri");
        this.secretsUri = Args.notNull(secretsUri, "secretsUri");
        this.resourcesUri = Args.notNull(resourcesUri, "resourcesUri");
        this.batchSecretsUri = Args.notNull(batchSecretsUri, "batchSecretsUri");
    }

    public Endpoints(String authnUri, String secretsUri, String resourcesUri, String batchSecretsUri){
        this(URI.create(authnUri), URI.create(secretsUri), URI.create(resourcesUri), URI.create(batchSecretsUri));
    }

    public URI getAuthnUri(){ 
        return authnUri; 
    }

    public URI getSecretsUri() {
        return secretsUri;
    }

    public URI getBatchSecretsUri() {
        return batchSecretsUri;
    }

    public URI getResourcesUri() {
        return resourcesUri;
    }

    public static Endpoints fromConfig(Config config){
        return new Endpoints(
                getAuthnServiceUri(config.getCredentials().getAuthnUrl(), config.getAccount()),
                getServiceUri(config.getApplianceUrl(), "secrets", config.getAccount(), "variable"),
                getServiceUri(config.getApplianceUrl(), "resources", config.getAccount()),
                URI.create(config.getApplianceUrl() + "/secrets")
        );
    }

    private static URI getAuthnServiceUri(String authnUrl, String accountName) {
        return URI.create(String.format("%s/%s", authnUrl, accountName));
    }

    private static URI getServiceUri(String applianceUrl, String service, String accountName){
        return URI.create(String.format("%s/%s/%s/%s", applianceUrl, service, accountName, ""));
    }

    private static URI getServiceUri(String applianceUrl, String service, String accountName, String path){
        return URI.create(String.format("%s/%s/%s/%s", applianceUrl, service, accountName, path));
    }

    @Override
    public String toString() {
        return "Endpoints{" +
                "authnUri=" + authnUri +
                "secretsUri=" + secretsUri +
                '}';
    }
}
