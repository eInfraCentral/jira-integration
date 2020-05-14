package eu.catris.util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            "                    %s\n" +
            "            ],\n" +
            "        \"assignee\": {\n" +
            "            \"accountId\": \"%s\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public static final String ISSUE_COMPONENTS_TEMPLATE =
            "    {\n" +
            "        \"name\": \"%s\"\n" +
            "    }\n";


    public static JSONObject createProviderOnboardingIssue(String providerName, String providerDesc, String projectKey, List<String> componentNames, String assigneeId) {
        String issue = String.format(JiraUtils.ONBOARDING_ISSUE_JSON_TEMPLATE, providerName, providerDesc, projectKey, createComponents(componentNames), assigneeId);
        return new JSONObject(issue);
    }

    public static String createComponents(List<String> names) {
        List<String> components = new ArrayList<>();
        for (String name : names) {
            components.add(String.format(ISSUE_COMPONENTS_TEMPLATE, name));
        }
        return String.join(",", components);
    }

    private JiraUtils() {
    }
}
