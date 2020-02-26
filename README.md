Conjur API for Java
===================
This Java SDK allows developers to build new apps in Java that communicate with Conjur by invoking our Conjur API to perform operations on stored data (add, retrieve, etc).

## Summary
The java application created with this library should be able to communicate to the Conjur appliance using HTTPS. Set environment variables to configure the conjur client. Below is how to configure the Conjur client depending on the applications authentication type:
- [api key authentication](#api-key)
- [authn-iam authentication](#authn-iam)
- [authn-k8s authentication](#authn-k8s)

If the conjur service has been configured with a self-signed certificate then you must add the conjur public certificate to the java certificate keystore.
- [set self-signed certificate java keystore](#setup-trust-between-app-and-conjur)

An example of retrieving all the secrets the application has access tro can be found here
- [fetching all secrets from conjur](#usage)

## Setup
You can grab the library's dependencies from the source by using Maven **or** locally by generating a JAR file and adding it to the project manually. 
 
To do so from the source using Maven, following the setup steps belows: 

1. Create new maven project using an IDE of your choice
2. If you are using Maven to manage your project's dependencies, include the following Conjur API dependency in your `pom.xml`: 

```xml
<dependency>
  <groupId>net.conjur.api</groupId>
  <artifactId>conjur-api</artifactId>
  <version>2.1.0</version>
</dependency>
```

_NOTE:_ Depending on what version of the Java compiler you have, you may need to update the version. At this time, the `{version}` most compatible is `1.8`:

```xml
<properties>
    <maven.compiler.source>{version}</maven.compiler.source>
    <maven.compiler.target>{version}</maven.compiler.target>
</properties>
```

3. Run `mvn install` to install packages and their dependencies locally.

If generating a JAR is preferred, you can build the library locally and add the dependency to the project manually by following the setup steps below:

1. Clone the Conjur Java API repo locally: `git clone {repo}`
2. `cd conjur-api-java`
3. Run `mvn package -DskipTests` to generate a JAR file. These files should be in the `target` directory of the repo
    
    _NOTE:_ we ran `mvn package` without running the integration tests, since these require access to a Conjur instance. You can run the integration tests with mvn package once you finished with the configuration. For more information on how to run the tests, take a look at our [Contributing](https://github.com/cyberark/conjur-api-java/blob/master/CONTRIBUTING.md) guide.

4. Follow the steps outlined [here](https://www.jetbrains.com/help/idea/library.html) for information on how to add JAR files into the new app's project files using Intellij and [here](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.wst.webtools.doc.user%2Ftopics%2Ftwplib.html) for Eclipse

## Configuration
The following environment variables need to be included in the apps runtime environment in order use the Conjur API:
```bash
# The conjur appliance url.
CONJUR_APPLIANCE_URL=https://conjur-master
# The conjur organization account. Can be retrieved from 'curl $CONJUR_APPLIANCE_URL/info'.
CONJUR_ACCOUNT=myorg
# The conjur username associated with this application.
CONJUR_AUTHN_LOGIN=host/java/app1
# The api key associated with this application
CONJUR_AUTHN_API_KEY=eyJhbGciOiJjb25qdXIub3JnL3Nsb3NpbG8vdjIiLCJraWQiOiJhNGU5ND
```

If using a non-generic authentication type
```bash
CONJUR_AUTHN_URL=https://conjur-master/authn-iam/prod
```

If using authn-k8s or other type of authentication where session token is provided
```bash
CONJUR_AUTHN_TOKEN_FILE=/etc/conjur/session/token
or
CONJUR_AUTHN_TOKEN='{"protected": "..", "payload": "...", "signature": "..."}'
```


## Authentication types
### Api Key
```sh
# Additionally set the following environment variables:
export CONJUR_APPLIANCE_URL=https://conjur-master.local
export CONJUR_ACCOUNT=myOrg
export CONJUR_AUTHN_LOGIN=host/app1
export CONJUR_AUTHN_API_KEY=ajd87agddkisoi72bsks82nbdr2
```
```java
Conjur conjur = new Conjur();
```

### authn-iam
```sh
# CONJUR_AUTHN_URL must be provided for authn-iam
export CONJUR_APPLIANCE_URL=https://conjur-master.local
export CONJUR_AUTHN_URL=https://conjur-master.local/authn-iam/prod
export CONJUR_ACCOUNT=myOrg
# CONJUR_AUTHN_LOGIN must end with the format <AWS account number>/<AWS IAM role name>
export CONJUR_AUTHN_LOGIN=host/team1/2728383626382/iam-role-name-example
export CONJUR_AUTHN_API_KEY=fillerApiKey

# these environment variables are set automatically for lambda functions
# to set these in EC2 you must call the metadata endpoint
export AWS_ACCESS_KEY=32423423ed2d234
export AWS_SECRET_KEY=sdfsdf8s9sfhf9s9s8hdf
export AWS_SESSION_TOKEN=8u98shd9f98sdhfsdf/sd8fhs9d8fhsdf/98hs9duhf9sdhf9sgdfsd9fugsd9ugs9df
```
```java
// construct the api key used when using authn-iam
String awsAccessKey = System.getenv("AWS_ACCESS_KEY");
String awsSecretKey = System.getenv("AWS_SECRET_KEY");
String awsToken = System.getenv("AWS_SESSION_TOKEN");
String awsConjurApiKey = ConjurUtilities.getIamApiKey(awsAccessKey, awsSecretKey, awsToken);

// get configuration object using environment variables
Config config = new Config.fromEnvironment();
// override the api key with the constructed conjur aws api key
config.setPassword(awsConjurApiKey);
Conjur conjur = new Conjur(config);
```

### authn-k8s
```sh
# username and password is not required for authn-k8s 
# when using the side car authenticator
export CONJUR_APPLIANCE_URL=https://conjur-master.local
export CONJUR_ACCOUNT=myOrg
export CONJUR_AUTHN_TOKEN_FILE=path/to/conjur/authentication/token.json
```
```java
Conjur conjur = new Conjur();
```

### Usage
```java
 // set environment variables as mentioned above
 Conjur conjur = new Conjur();

 // Get a list of all the variables I have read access to
 Variables variables = conjur.getVariables();

 // retrieve each secret from conjur variable, one by one
 for (Variable variable : variables.asList()) {
 String value = conjur.retrieveSecret(variable);
 System.out.println(
         String.format("%s secret value is %s", variable.getId(), value));
 }

 // retrieve all secrets in batch
 variables = conjur.retrieveBatchSecrets(variables);
 for (Variable variable : variables.asList()) {
     System.out.println(
         String.format("%s secret value is %s", variable.getId(), variable.getSecret()));
 }

```

## Setup Trust Between App and Conjur
By default, the Conjur appliance generates and uses self-signed SSL certificates (Java-specific certificates known as cacerts). 
You'll need to configure your app to trust them. You can accomplish this by loading the Conjur certificate into Java's CA keystore that holds the list of all the allowed certificates for https connections.

Retrieve the self signed public certificate and load into the java keystore. Make sure to replace conjur_hostname with your actual conjur instance hostname.
```bash
conjur_hostname=conjur-master
openssl s_client -showcerts -connect $conjur_hostname:443 < /dev/null 2> /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > conjur.pem
keytool -import -trustcacerts -alias conjur -keystore "$JRE_HOME/lib/security/cacerts" -file conjur.pem
```

## JAX-RS Implementations

The Conjur API client uses the JAX-RS standard to make requests to the Conjur web services.  In the future we plan to
remove this dependency, but for the time being you may need to change the JAX-RS implementation to conform to your
environment and application dependencies.  For example, in a JBoss server environment, you should use the RESTlet
implementation.  The Conjur API uses Apache CXF by default.  You can replace that dependency in `pom.xml` to use an
alternative implementation.

## Contributing
For instructions on how to contribute, please see our [Contributing](https://github.com/cyberark/conjur-api-java/blob/master/CONTRIBUTING.md) guide.

## License

This repository is licensed under Apache License 2.0 - see [`LICENSE`](LICENSE) for more details.
