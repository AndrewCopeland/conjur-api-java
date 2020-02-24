Conjur API for Java
===================
]This Java SDK allows developers to build new apps in Java that communicate with Conjur by invoking our Conjur API to perform operations on stored data (add, retrieve, etc).

## Table of Contents
- [Prequisites](#prerequisites)
- [Setup](#setup)
- [Configuration](#configuration)
- [Setup Trust Between App and Conjur](#setup-trust-between-app-and-conjur)
- [Authorization Examples](#authorization-examples)
- [Conjur Services Operation Examples](#conjur-services-operation-examples)
- [JAX-RS Implementations](#jax-rs-implementations)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites
1. Your java application must be able to communicate to the Conjur appliance using HTTPS.
2. Have a the needed environment variables mentioned in the Configuration section.

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

# If using a non-generic authentication type
CONJUR_AUTHN_URL=https://conjur-master/authn-iam/prod

# If using authn-k8s or other type of authentication where session token is provided
CONJUR_AUTHN_TOKEN_FILE=/etc/conjur/session/token
or
CONJUR_AUTHN_TOKEN='{"protected": "..", "payload": "...", "signature": "..."}'
```



### Environment Variables (the standard way)
```sh
# Additionally set the following environment variables:
export CONJUR_APPLIANCE_URL=https://conjur-master.local
export CONJUR_ACCOUNT=myOrg
export CONJUR_AUTHN_LOGIN=host/app1
export CONJUR_AUTHN_API_KEYajd87agddkisoi72bsks82nbdr2
```
```java
Conjur conjur = new Conjur();
```

### Authorization Token
```sh
# Additionally set the following environment variables:
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
Variables variables = conjur.getVariables()

// retrieve each secret from conjur variable, one by one
for (Variable variable : variables.asList()) {
    String value = conjur.retrieveSecret(variable);
    System.out.println(
        String.format("%s secret value is $s", variable.getId(), value)
    );
}

// retrieve all secrets in batch
variables = conjur.retrieveBatchSecrets(variables)
for (Variable variable : variables) {
    System.out.println("%s secret value is $s", variable.getId(), variable.getSecret());
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
