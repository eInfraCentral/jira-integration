package eu.catris.jms;

import eu.catris.service.JiraService;
import eu.catris.service.SimpleJiraService;
import eu.catris.util.JiraUtils;
import eu.einfracentral.domain.ProviderBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderListener {

    private static final Logger logger = LogManager.getLogger(ProviderListener.class);

    private final JiraService jiraService;

    @Value("${jira.project.key:}")
    private String projectKey;

    @Value("${jira.assignee.id:}")
    private String assigneeId;

    @Value("#{'${jira.components.names:}'.split(',')}")
    private List<String> componentsNames;

    @Value("#{'${jira.labels:}'.split(',')}")
    private List<String> labels;

    @Autowired
    public ProviderListener(SimpleJiraService jiraService) {
        this.jiraService = jiraService;
        logger.debug("Creating Listener");
    }

    @JmsListener(destination = "provider.create")
    public void getNewProvider(ProviderBundle provider) {
        logger.debug("New provider: {}", provider);
        JSONObject json = JiraUtils.createProviderOnboardingIssue(provider.getProvider().getName(),
                provider.getProvider().getDescription(), projectKey, componentsNames, labels, assigneeId);
        jiraService.createIssue(json);
    }

    @JmsListener(destination = "provider.update")
    public void getUpdatedProvider(ProviderBundle provider) {
        logger.debug("Updated provider: {}", provider);
    }

    @JmsListener(destination = "provider.delete")
    public void getDeletedProvider(ProviderBundle provider) {
        logger.debug("Deleted provider: {}", provider);
    }
}
