# VSM GitHub Broker

VSM GitHub Broker is used to establish the communication between VSM SaaS Application and GitHub Enterprise on premise
deployments that are not publicly accessible from the internet.

VSM GitHub Broker runs on customers' premises, connects to GitHub Enterprise deployments and transmits the necessary 
data to VSM SaaS Application.

### VSM GitHub Broker Diagram

![github-broker-diagram](docs/VSM_GitHub_Broker.png)

## Usage

The VSM GitHub Broker is published as a Docker image. The configuration is performed with environment variables as 
described below.

To use the Broker client with a GitHub Enterprise deployment, run `docker pull acr-public/vsm-github-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_REGION` - the LeanIX region, obtained from your LeanIX settings view (leanix.net).
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your LeanIX settings view (leanix.net).
- `LEANIX_USER_TOKEN` - the LeanIX USER, obtained from your LeanIX settings view (leanix.net).
- `LEANIX_CONFIGURATION_NAME` - the LeanIX configuration, obtained from your LeanIX settings view (leanix.net).
- `GITHUB_TOKEN` - a personal access token with full `repo`, `read:org` and `admin:repo_hook` scopes.
- `GITHUB_URL` - the hostname of your GitHub Enterprise deployment, such as `ghe.domain.com`.
- `CLIENT_URL` - the full URL of the Broker client as it will be accessible by your GitHub Enterprise deployment webhooks, such as `http://vsm-broker.client:8080`

#### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --restart=always \
           -p 8080:8080 \
           -e LEANIX_REGION=us \
           -e LEANIX_API_TOKEN=leanix-token \
           -e LEANIX_USER_TOKEN=leanix-user \
           -e LEANIX_CONFIGURATION_NAME=configuration-name \
           -e GITHUB_TOKEN=secret-github-token \
           -e GITHUB_URL=ghe.domain.com \
           -e CLIENT_URL=http://vsm-broker.client:8080 \
       acr-public/vsm-github-broker