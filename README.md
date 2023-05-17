# VSM GitHub Broker

VSM GitHub Broker is used to establish the communication between VSM SaaS Application and GitHub Enterprise on premise
deployments that are not publicly accessible from the internet.

VSM GitHub Broker runs on customers' premises, connects to GitHub Enterprise deployments and transmits the necessary
data to VSM SaaS Application.


<h2 align="center">Table of Contents </h2>

1. [Usage](#usage)
   1. [Command-line arguments](#command-line-arguments)
   2. [*Optional:* Webhook Configuration](#optional-webhook-configuration)
2. [Troubleshooting](#troubleshooting)
   1. [Using a Proxy](#using-over-a-http-proxy-system)
   2. [SSL interception](#using-over-ssl-intercepting-proxy)
   3. [Using with M1 chips](#using-amd64-images-on-apple-m1)
3. [Release Process](#release-process)
4. [Broker Architecture](#broker-architecture) 

---


### VSM GitHub Broker Diagram

![github-broker-diagram](docs/VSM_GitHub_Broker.png)

## Usage

The VSM GitHub Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitHub Enterprise deployment, run `docker pull leanixacrpublic.azurecr.io/vsm-github-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`.
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights. 
- `GITHUB_TOKEN` - a [personal access token](#personal-access-token) with full `repo`, `read:org` and `admin:org_hook` scopes.
- `GITHUB_URL` - the hostname of your GitHub Enterprise deployment, such as `https://ghe.domain.com`. This must include the protocol (http vs https) of the GitHub Enterprise deployment.
- `BROKER_URL` - the full URL of the vsm client as it will be accessible by your GitHub Enterprise deployment webhooks, such as http://vsm.client:8080
- `VSM_WEBHOOK` - a boolean switch to turn off the webhook capability of the broker. When set to false, the broker won't place any webhook and will just run on a 1x day schedule. Default: `true`. 

> :bulb: We highly recommend to use the broker with webhooks for a much better & instant user experience, if you can.

### Personal Access Token
As part of the setup the vsm-broker requires a personal access token (PAT) with according rights to run effectively. For more details on how to create the PAT, see [GitHubs documentation](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token). 

The following scopes are required:
GitHub Scope  | VSM Usage
------------- | -------------
full `repo` including `repo:status`, `repo_deployment`, `public_repo`, `repo:invite`, `security_events`    | To read repository data for the [full scan](#broker-architecture)
`admin:org_hook`    | To register and manage the webhook on org-level


#### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
           -e GITHUB_TOKEN=<secret-github-token> \
           -e GITHUB_URL=<GitHub Ent URL(https://ghe.domain.com)> \
           -e BROKER_URL=<vsm-github-broker URL(http://my.vsm.broker.client:8080)> \
        leanixacrpublic.azurecr.io/vsm-github-broker
```

#### Optional: Webhook configuration
> This capability is available from version [*v1.1.0*](https://github.com/leanix/vsm-github-broker/releases/tag/v1.1.0) onwards.

The vsm broker exposes an endpoint to listen to GitHub webhooks. This enables an near-to-realtime update of information in VSM. The broker automatically registers and manages the webhook when the broker initializes. By default this capability is switched on.

> Note: Complete data is freshly pulled from GitHub enterprise **every day at 4A.M**. Hence webhook can be optionally setup for real-time updates. Or else new data is refreshed on daily basis.

```
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
           -e LEANIX_CONFIGURATION_SET_NAME=<config-set-name>\
           -e GITHUB_TOKEN=<secret-github-token> \
           -e GITHUB_URL=<GitHub Ent URL(https://ghe.domain.com)> \
           -e BROKER_URL=<vsm-github-broker URL(http://my.vsm.broker.client:8080)> \
        leanixacrpublic.azurecr.io/vsm-github-broker
```

> Note: Make sure to use a unique GitHub token for each Broker client instance. This ensures maximum security and prevents the Broker client from receiving events from other GitHub Enterprise deployments.

##### Registered Webhooks
GitHub Event  | VSM Usage
------------- | -------------
`pull_requests`  | Generate DORA metrics
`pushes`  | Generate DORA metrics
`repositories`  | Get updates from repositories to generate service metadata (e.g service name)
`repositories imports`  | Discover new repositories
`repo visibilty changes`  | Update the repos visibility (e.g. Active > Archived)


### Troubleshooting

#### Using over a http proxy system

Add the following properties on the command:

```console
docker run 
           ...
           -e JAVA_OPTS="-Dhttp.proxyHost=<HTTP_HOST> -Dhttp.proxyPort=<HTTP_PORT> -Dhttp.proxyUser=<PROXY_USER> -Dhttp.proxyPassword=<PROXY_PASS> -Dhttps.proxyHost=<HTTPS_HOST> -Dhttps.proxyPort=<HTTPS_PORT> -Dhttps.proxyUser=<PROXY_USER> -Dhttps.proxyPassword=<PROXY_PASS>" \
        leanixacrpublic.azurecr.io/vsm-github-broker
```

#### Using over SSL Intercepting proxy

Build your own docker image adding the certificate:

```console
FROM leanixacrpublic.azurecr.io/vsm-github-broker


USER root

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*
COPY YOUR-CERTIFICATE-HERE /usr/local/share/ca-certificates/YOUR-CERTIFICATE-HERE
RUN update-ca-certificates 
RUN keytool -import -trustcacerts -keystore $JAVA_HOME/lib/security/cacerts  -storepass changeit -noprompt -alias YOUR-CERTIFICATE-HERE -file /usr/local/share/ca-certificates/YOUR-CERTIFICATE-HERE

```

> Note: You should add an additional COPY and the final RUN for each certificate you need to insert into the image.

#### Using amd64 images on Apple M1

Just run the container by providing the following command:

```console

docker run --platform linux/amd64 \
           ...
        leanixacrpublic.azurecr.io/vsm-github-broker
```
## Release Process
In order to provide a excellent experience with the agent, we are using a three-pronged release process. Any change we undertake can be classified into one of the three categories:
1. **Major**
These are releases that change the brokers behavior fundamentally or are significant feature addition. Examples could be supporting a new domain API GitHub released. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v2.0.0`.
2. **Minor**
These are releases that add non-breaking feature increments. Examples could be: adding new API calls to fetch further data for use in VSM. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v1.1.0`.

3. **Patch**
These are releases that entail hotfixes, non-breaking updates to underlying libraries. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v1.0.1`.

With every new release you will find the details of what the release entails in the [releases tab](https://github.com/leanix/vsm-github-broker/releases). 

Should there be any open questions feel free to open an [issue](https://github.com/leanix/vsm-github-broker/issues) 📮

## Broker Architecture 
1. The integration (vsm-github-broker) is packaged as a docker container which shall be deployed on the customer premises

2. The container runs a live service, which runs continuously

3. On startup:
   - the service will reach out to VSM to fetch the configured GitHub organizations

   - the service will then call the GitHub Enterprise instance to fetch relevant GitHub data

   - the service will on startup place webhooks on the relevant GitHub organizations to listen to events emitted from in-scope GitHub organizations to update VSM (see [webhooks](#optional-webhook-configuration) )

   - the service ensures that there will always only be one webhook registered

4. At runtime:
   - as stated under 3) the service will listen to webhook events after the initial setup

   - to account for any intermittent interruptions (e.g. network issues, docker container failure etc.) between the agent and the GitHub instance, the service will do a full scan every week to ensure eventual consistency in VSM

5. After collecting the GitHub data (either by a full scan or by webhooks) the service relays the service data to the VSM workspace via REST API calls 


The docker container as well as the source code is scanned daily with snyk to check for known vulnerabilities.
