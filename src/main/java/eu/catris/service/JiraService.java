package eu.catris.service;

import org.json.JSONObject;

public interface JiraService {

    String createIssue(JSONObject issue);
}
