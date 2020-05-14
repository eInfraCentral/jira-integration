package eu.catris.jms;

import eu.catris.util.JiraUtils;
import eu.einfracentral.domain.ProviderBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

@Service
public class ProviderListener {

    private static final Logger logger = LogManager.getLogger(ProviderListener.class);

    private final RestTemplate restTemplate;

    @Value("${jira.host.url}")
    private String jiraHost;

    @Value("${jira.project.key}")
    private String projectKey;

    @Value("${jira.assignee.id}")
    private String assigneeId;

    @Value("#{'${jira.components.names}'.split(',')}")
    private List<String> componentsNames;

    @Autowired
    public ProviderListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        logger.debug("Creating Listener");
    }

    @JmsListener(destination = "provider.create")
    public void getNewProvider(ProviderBundle provider) {
        logger.debug("New provider: {}", provider);
        JSONObject json = JiraUtils.createProviderOnboardingIssue(provider.getProvider().getName(),
                provider.getProvider().getDescription(), projectKey, componentsNames, assigneeId);
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), createJsonHeaders());
        String issue = null;

        try {
            issue = restTemplate.postForObject(new URI(jiraHost + "/rest/api/2/issue").normalize(), entity, String.class);
        } catch (URISyntaxException e) {
            logger.error(e);
        }
        logger.info("\nCreated Jira issue:\n{}", issue);
    }

    @JmsListener(destination = "provider.update")
    public void getUpdatedProvider(ProviderBundle provider) {
        logger.debug("Updated provider: {}", provider);
    }

    @JmsListener(destination = "provider.delete")
    public void getDeletedProvider(ProviderBundle provider) {
        logger.debug("Deleted provider: {}", provider);
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
