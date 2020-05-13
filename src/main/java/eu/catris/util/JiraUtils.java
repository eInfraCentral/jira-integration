package eu.catris.util;

import org.json.JSONObject;

public class JiraUtils {

    public static final String ONBOARDING_ISSUE_JSON_TEMPLATE =
            "{\n" +
            "    \"fields\": {\n" +
            "        \"summary\": \"[%s]\",\n" +
            "        \"description\": \"%s\",\n" +
            "        \"issuetype\": {\n" +
            "            \"id\": \"10008\"\n" +
            "        },\n" +
            "        \"project\": {\n" +
            "            \"key\": \"%s\"\n" +
            "        },\n" +
            "        \"components\": [\n" +
            "                {\n" +
            "                    \"name\": \"Service Provider\"\n" +
            "                }\n" +
            "            ],\n" +
            "        \"assignee\": {\n" +
            "            \"emailAddress\": \"%s\"\n" +
            "        }\n" +
            "    }\n" +
            "}";


    public static JSONObject createProviderOnboardingIssue(String providerName, String providerDesc, String projectKey, String assigneeEmail) {
        String issue = String.format(JiraUtils.ONBOARDING_ISSUE_JSON_TEMPLATE, providerName, providerDesc, projectKey, assigneeEmail);
        return new JSONObject(issue);
    }

    private JiraUtils() {
    }
}
