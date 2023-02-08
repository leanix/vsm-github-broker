# VSM GitHub Broker

VSM GitHub Broker is used to establish the communication between VSM SaaS Application and GitHub Enterprise on premise
deployments that are not publicly accessible from the internet.

VSM GitHub Broker runs on customers' premises, connects to GitHub Enterprise deployments and transmits the necessary
data to VSM SaaS Application.

### VSM GitHub Broker Diagram

![github-broker-diagram](docs/VSM_GitHub_Broker.png)

## Usage

> ⚠️ This integration is currently in early access mode. See the details on what this means [here](https://docs-vsm.leanix.net/docs/release-stages). Feel free to open an issue should you hit a problem.


The VSM GitHub Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitHub Enterprise deployment, run `docker pull acr-public/vsm-github-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`.
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights. 
- `LEANIX_CONFIGURATION_NAME` - the LeanIX configuration name. ❔in the current stage, you need to provide your GitHub organisation names to us, for us to create these configurations. We are working on a self-setup UI 🖥️
- `GITHUB_TOKEN` - a personal access token with full `repo`, `read:org` and `admin:org_hook` scopes.
- `GITHUB_URL` - the hostname of your GitHub Enterprise deployment, such as `ghe.domain.com`.
- `BROKER_URL` - the full URL of the vsm client as it will be accessible by your GitHub Enterprise deployment webhooks, such as http://vsm.client:8080
- `VSM_WEBHOOK` - a boolean switch to turn off the webhook capability of the broker. In this case, the broker wont place any webhook and will just run on a 1x day schedule. Default: `true`. 

> :bulb: We highly recommend to use the broker with webhooks for a much better & instant user experience, if you can.

#### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
           -e LEANIX_CONFIGURATION_NAME=<config-name>\
           -e GITHUB_TOKEN=<secret-github-token> \
           -e GITHUB_URL=<GitHub Ent URL(ghe.domain.com)> \
           -e BROKER_URL=http://my.vsm.broker.client:8080 \
        leanixacrpublic.azurecr.io/vsm-github-broker
```

#### Optional: Webhook configuration
> This capability is available from version [*v1.1.0*](https://github.com/leanix/vsm-github-broker/releases/tag/v1.1.0) onwards.

The vsm broker exposes an endpoint to listen to GitHub webhooks. This enables an near-to-realtime update of information in VSM. The broker automatically registers and manages the webhook when the broker initializes. By default this capability is switched on.

```
docker run --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
           -e LEANIX_CONFIGURATION_NAME=<config-name>\
           -e GITHUB_TOKEN=<secret-github-token> \
           -e GITHUB_URL=<GitHub Ent URL(ghe.domain.com)> \
           -e BROKER_URL=http://my.vsm.broker.client:8080 \
           -e VSM_WEBHOOK=false \
        leanixacrpublic.azurecr.io/vsm-github-broker
```

> Note: Make sure to use a unique GitHub token for each Broker client instance. This ensures maximum security and prevents the Broker client from receiving events from other GitHub Enterprise deployments.

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

```


#### Using amd64 Images on Apple M1

Just run the container by providing the following command:

```console

docker run --platform linux/amd64 \
           ...
        leanixacrpublic.azurecr.io/vsm-github-broker
```
