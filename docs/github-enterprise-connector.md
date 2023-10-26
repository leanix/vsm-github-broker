---
title: GitHub Enterprise
excerpt: Out-of-the-box Source Code Repository Integration
category: 62b439a44f5a3e003566e740
---

## Introduction

> :bulb:**Early Access **
>
> This integration is in early access. To find more information about the release stages of our integrations, see [Release Stages](https://docs-vsm.leanix.net/docs/release-stages).

The LeanIX VSM GitHub Repository integration offers an easy way to auto-discover all your services from your on-premise GitHub Enterprise instance. Based on this VSM's mapping inbox allows you to easily sift through all the stale information from GitHub to decide, which services are really useful to your organization and hence should be part of your service catalog. This will help you to maintain a high standard of data quality when you subsequently map your services to their individual teams to create clear team ownership.

![](https://files.readme.io/539f1a5-image.png)

##### Integrate with GitHub Enterprise to:

- Automatically discover your services to build your company-wide service catalog
- Map team ownership to have clear software governance in place
- Automatically get Change & Release events for your DORA metrics

## Setup

The integration runs as a dockerized agent to continuously fetch your GitHub data and pass it into VSM. See the technical details on the [project's page.](https://github.com/leanix/vsm-github-brokerGitHub).


[block:embed]
{
"html": false,
"url": "https://github.com/leanix/vsm-github-broker",
"title": "VSM GitHub Broker",
"favicon": "https://github.githubassets.com/favicons/favicon.png",
"image": "https://avatars.githubusercontent.com/u/29029855?s=280&v=4",
"provider": "github.com",
"href": "https://github.com/leanix/vsm-github-broker"
}
[/block]




> 🚧 Support of GitHub Enterprise versions
>
> TBD

### Configuration in GitHub & VSM

To set up the integration follow the below steps:

1. Set up a service account in your GitHub instance, and make it a member of the GitHub organizations that need to be scanned as part of this service account. We recommend using service accounts to manage permissions more easily.
2. Create a Personal Access Token with the following scopes (see the [GitHub Enterprise documentation](https://docs.github.com/en/enterprise-server@2.22/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)):
    1. `repo`
    2. `admin:org_hook`  
       ![](https://files.readme.io/9e02d54-image.png)

> 📘 Authorizing for Single Sign On
>
> If your GitHub Organization requires Single-Sign-On, then you will also need to authorize the token for single-sign-on ( see below) or follow the detailed instructions via [**GitHub documentation**](https://docs.github.com/en/enterprise-cloud@latest/authentication/authenticating-with-saml-single-sign-on/authorizing-a-personal-access-token-for-use-with-saml-single-sign-on).

[block:image]
{
"images": [
{
"image": [
"https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/sso.png?raw=true",
"sso.png",
839
],
"align": "center",
"caption": "Authorizing organizations with SSO"
}
]
}
[/block]

3. Go to the admin panel in VSM > Ìntegrations and start the setup flow for GitHub Enterprise. You'll need the following information at your disposal:
    1. The PAT token from #2
    2. The URL for your GitHub Enterprise instance. In most cases, it will be something like: `https://ghe.domain.com`. Note: don't forget the `https` prefix :stuck-out-tongue-winking-eye:
    3. The URL where you host the VSM broker (including the port). It will most likely look something like this: `http://vsm.client:8080`
4. After saving, the setup wizard will output a docker command along the lines of:

```dockerfile
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
           -e GITHUB_TOKEN=<secret-github-token> \
           -e GITHUB_URL=<GitHub Ent URL(https://ghe.domain.com)> \
           -e BROKER_URL=<vsm-github-broker URL(http://my.vsm.broker.client:8080)> \
        leanixacrpublic.azurecr.io/vsm-github-broker
```



5. Deploy the docker container in your preferred deployment mode (e.g. via K8s, via virtual machine ...)
6. Shortly after initialization the agent will connect with VSM and you should see logs appearing in the log section of the integration panel in VSM.

#### Limitations on Running Multiple Instances of GitHub Broker

We only support a single broker instance to be run at any given time. Any additional instance trying to communicate with VSM will be rejected to prevent race conditions. Please ensure a single instance is running at any time for optimal performance.

#### Multi-org support

VSM allows you to scan multiple GitHub organizations with the VSM GitHub broker. There are two scenarios:

##### Scenario I: I want to scan multiple GitHub organizations with one service account (=one PAT)

This case applies if you want to bundle all or some GitHub organizations under the same PAT. Commonly, this occurs if you have one GitHub admin that oversees all GitHub organizations. You can then create one VSM config set containing all GitHub organizations, managed by a single service account and PAT. You will receive & need to run the docker container, as outlined above. The configuration set will appear in the VSM admin panel and will receive the logs for all GitHub organizations in the config set.

##### Scenario II: I want to scan multiple GitHub organizations with different service accounts (=multiple PATs)

This case applies if you want to manage your GitHub organizations in different cohorts (i.e. configuration sets). This commonly occurs if you have a GitHub admin that oversees some GitHub organizations (= business units) but not all. This then helps to parallelize onboarding by allowing each GitHub admin to manage their VSM config separately. This setup results in multiple VSM config sets containing one or many GitHub organizations that will be scanned. You will receive & need to run one docker container per VSM configuration set. Each configuration set will appear in the VSM admin panel as one entity, for which it receives logs.

#### GitHub API Rate Limits

GitHub Enterprise comes with API rate limits by default.<br>
The default rate limit is 60 requests per hour for unauthenticated requests and 5000 requests per hour for authenticated requests using a personal access token (PAT).<br>
If face rate limit errors on the broker, you should consider increase the rate limit on your GitHub Enterprise instance configuration.<br>
Please refer to [rate limit documentation](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#rate-limiting) for more information.


> 👍 Scheduling
>
> Schedule the integration once a day if your organization has (approx.) >2000 repositories and/or >200 teams

## Map discovered repositories

After the integration successfully loaded all your repositories into the VSM mapping inbox you can now start to sift through and decide which of these repos are actually worthy to maintain in the service catalog. To do so:

1. Head over to **Technology > Mapping**

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/vsm-catalog.png?raw=true "vsm-catalog.png")

2. Click on Create and either map it to another - existing service (if these are duplicates) - or create a new VSM service that will end up in the service catalog.

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/vsm-create.png?raw=true "vsm-mapping-create.png")

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/vsm-create-ok.png?raw=true "vsm-mapping-ok.png")

3. Congrats! you have now created your first VSM service - neat & tidy. Go ahead and explore what it has to offer and map it to  
   your teams.

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/vsm-object.png?raw=true "vsm-object.png")

## DORA Metrics

Software Delivery Metrics are the best way for teams to track and monitor team productivity. The GitHub repository integration can automatically fetch information for two of the most important DORA metrics:

1. Deployment frequency: Number of deployments to production for a Team.
2. Lead time for changes: Time from committing a change to code successfully running in production.

Find below the mechanism by which the change and release events are registered.

#### Registering Release Event

All closed or merged pull requests in our public repository [ps-scripts](https://github.com/leanix-public/ps-scripts/pulls?q=is%3Apr+is%3Aclosed)

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/prs.png?raw=true "vsm-object.png")

Pull requests which are merged, and are less than 30 days old are valid. There is only a valid pull request among the 4 closed pull requests.

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/valid-pr.png?raw=true "vsm-object.png")

This will be registered as `release` event along with the commits change Ids from below.

#### Registering Change Events

The [pull request](https://github.com/leanix-public/scripts/pull/6) has 3 commits.

![](https://github.com/leanix/vsm-dev-integrations-docs-storage/blob/main/github-repository-connector/commits.png?raw=true "vsm-object.png")

The 3 commits are registered as `change` events along with author details.

## Imported data

The integration retrieves the following pieces of information from your GitHub Enterprise instance:

1. Metadata, such as repository name, description, URL, etc.
2. Repository status e.g. `archived`, `active`
3. Repository code composition e.g. `typescript`
4. Repository topics e.g. `frontend`.
5. Repository visibility e.g. `private`
6. Top 3 contributors to the repository e.g. `rockstarprogrammer@mycompany.com`

## FAQs

>  Does the integration run in real-time or schedule-based?

Both are possible. Per default, the VSM GitHub broker will listen to  GitHub webhook events to provide a near-real-time user experience in VSM. We encourage you to stick with this default. Scheduled runs are used to recuperate from potential intermittent issues (such as network failures etc.).

If your organization doesn't allow for this mode, you can toggle the webhook functionality off (see the [details here](https://github.com/leanix/vsm-github-broker#optional-webhook-configuration)). The VSM GitHub broker will then only run on a once-per-day schedule.

> What are the required PAT scopes needed for?

For the PAT token the scopes are needed to perform the operations mentioned [here.](https://github.com/leanix/vsm-github-broker#personal-access-token)

The VSM GitHub broker also registers the following events in order to perform the following actions: see [here.](https://github.com/leanix/vsm-github-broker#registered-webhooks)