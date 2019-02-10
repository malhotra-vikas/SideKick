package main.java.com.mconsultants.alexa.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private static final Logger log = LoggerFactory.getLogger(PropertyReader.class);

    private static PropertyReader propertyReader;
    private String skillName = "";
    private String skillResponseEmailTo= "";
    private String skillResponseEmailFrom= "";
    private String skillResponseEmailFromName= "";

    private boolean propertyRead = false;
    private String fatalError = "";
    private String welcomeMessage = "";
    private String speechHelp = "";
    private String goodBye = "";
    private String speechReprompt= "";
    private String speechSorry = "";
    private String skillId = "";
    private String speechAbout = "";
    private String speechConsumer = "";
    private String speechBusiness = "";
    private String speechPermission = "";
    private String speechDidNotUnderstand = "";
    private String emailMessage = "";
    private String emailSubject = "";


    private PropertyReader() {
        Properties skillProperties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("skill.properties");
            // load a properties file

            skillProperties.load(input);
            skillName = skillProperties.getProperty("skill");
            fatalError = skillProperties.getProperty("speech-fatal-error");
            welcomeMessage = skillProperties.getProperty("speech-welcome");
            speechAbout = skillProperties.getProperty("speech-about");
            speechConsumer = skillProperties.getProperty("speech-consumer");
            speechBusiness = skillProperties.getProperty("speech-business");
            speechHelp = skillProperties.getProperty("speech-help");
            goodBye = skillProperties.getProperty("speech-goodbye");
            speechSorry = skillProperties.getProperty("speech-sorry");
            speechReprompt = skillProperties.getProperty("speech-reprompt");
            skillId = skillProperties.getProperty("skill-id");
            speechPermission = skillProperties.getProperty("speech-permission-text");
            speechDidNotUnderstand = skillProperties.getProperty("speech-did-not-understand");
            emailMessage = skillProperties.getProperty("email-message");
            emailSubject = skillProperties.getProperty("email-subject");

            skillResponseEmailTo= skillProperties.getProperty("skill-response-email-to");
            skillResponseEmailFrom= skillProperties.getProperty("skill-response-email-from");
            skillResponseEmailFromName= skillProperties.getProperty("skill-response-email-from-name");

            propertyRead = true;

            log.info("Coming from LOG 4 J - The skill name is :- " + skillName);

        } catch (IOException ioException) {
             propertyRead = false;
            log.error("Coming from LOG 4 J - Skill Property file not loaded");
        }

    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getSkillResponseEmailTo() {
        return skillResponseEmailTo;
    }

    public String getSkillResponseEmailFrom() {
        return skillResponseEmailFrom;
    }

    public String getSkillResponseEmailFromName() {
        return skillResponseEmailFromName;
    }

    public boolean isPropertyRead() {
        return propertyRead;
    }

    public String getFatalError() {
        return fatalError;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public String getSpeechHelp() {
        return speechHelp;
    }

    public String getGoodBye() {
        return goodBye;
    }

    public String getSpeechReprompt() {
        return speechReprompt;
    }

    public String getSpeechSorry() {
        return speechSorry;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getSpeechAbout() {
        return speechAbout;
    }

    public String getSpeechConsumer() {
        return speechConsumer;
    }

    public String getSpeechBusiness() {
        return speechBusiness;
    }

    public String getSpeechPermission() {
        return speechPermission;
    }

    public String getSpeechDidNotUnderstand() {
        return speechDidNotUnderstand;
    }

    public static PropertyReader getPropertyReader () {
        if (propertyReader == null) {
            propertyReader = new PropertyReader();
        }
        return propertyReader;
    }

}
