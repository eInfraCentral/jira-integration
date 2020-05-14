package eu.catris.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Service
public class SimpleJiraService implements JiraService {

    private static final Logger logger = LogManager.getLogger(SimpleJiraService.class);

    private final RestTemplate restTemplate;
    private final String jiraHost;
    private final boolean active;

    @Autowired
    SimpleJiraService(RestTemplate restTemplate, @Value("${jira.host.url:}") String host) {
        this.restTemplate = restTemplate;
        this.jiraHost = host;
        active = host != null && !host.equals("");
    }

    @Override
    public String createIssue(JSONObject issue) {
        String response = null;

        if (active) {
            try {
                HttpEntity<String> entity = new HttpEntity<>(issue.toString(), createJsonHeaders());
                response = restTemplate.postForObject(new URI(jiraHost + "/rest/api/2/issue").normalize(), entity, String.class);
            } catch (URISyntaxException e) {
                logger.error(e);
            }
            logger.info("\nCreated Jira issue:\n{}", response);
        }
        return response;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
