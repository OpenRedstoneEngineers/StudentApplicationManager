package org.stonecipher;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class Application {

    private String applicationName;
    private String availableToGroup;
    private String promoteToGroup;
    private String congratulations;
    private String ohnoyousuck;
    private ArrayList<Question> questions = new ArrayList<Question>();

    public Application(String applicationName, String availableToGroup, String promoteToGroup, String congratulations, String ohnoyousuck, ArrayList<Question> questions) {
        this.applicationName = applicationName;
        this.availableToGroup = availableToGroup;
        this.promoteToGroup = promoteToGroup;
        this.congratulations = congratulations;
        this.ohnoyousuck = ohnoyousuck;
        this.questions = questions;
    }

    public Application(String name, ConfigurationSection config) {
        this.applicationName = name;
        this.availableToGroup = config.getString("available_to_group");
        this.promoteToGroup = config.getString("promote_to_group");
        this.congratulations = config.getString("congratulations");
        this.ohnoyousuck = config.getString("ohnoyousuck");
        loadQuestions(config);
    }

    private void loadQuestions(ConfigurationSection config) {
        String basePath = "questions";
        for (int i = 0; i<config.getConfigurationSection(basePath).getKeys(false).size(); i++) {
            String presentPath = basePath + "." + (i+1);
            Question tmpQuestion = new Question(config.getString(presentPath + ".answer"), config.getString(presentPath + ".question"));
            questions.add(tmpQuestion);
        }
    }

    public String getAvailableToGroup() {
        return availableToGroup;
    }

    public String toString() {
        return "Application(name: " + this.applicationName + ",available_to: " + this.availableToGroup   + ",promote_to: " + this.promoteToGroup + ",congratulations: " + this.congratulations + ",ohnoyousuck: " + this.ohnoyousuck + ",questions: " + this.questions.toString() + ")";
    }

    public String getPromoteToGroup() {
        return promoteToGroup;
    }

    public String getCongratulations() {
        return congratulations;
    }

    public String getOhnoyousuck() {
        return ohnoyousuck;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
