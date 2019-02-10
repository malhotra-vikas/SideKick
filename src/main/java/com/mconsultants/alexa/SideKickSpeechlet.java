package main.java.com.mconsultants.alexa;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.interfaces.system.SystemInterface;
import com.amazon.speech.speechlet.interfaces.system.SystemState;
import com.amazon.speech.speechlet.services.DirectiveEnvelope;
import com.amazon.speech.speechlet.services.DirectiveEnvelopeHeader;
import com.amazon.speech.speechlet.services.DirectiveService;
import com.amazon.speech.speechlet.services.SpeakDirective;
import com.amazon.speech.ui.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import main.java.com.mconsultants.alexa.utilities.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class SideKickSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SideKickSpeechlet.class);
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();

    /**
     * The key to get the item from the intent.
     */
    private static final String NAME_SLOT = "intentname";

    private StringBuilder responseBuilder = new StringBuilder();

    private String deviceId;
    private String apiAccessToken;
    private String apiEndpoint;

    private String userName;
    private String userEmail;

    /**
     * Service to send progressive response directives.
     */
    private DirectiveService directiveService;

    /**
     * Constructs an instance of {@link SideKickSpeechlet}.
     *
     * @param directiveService implementation of directive service
     */
    public SideKickSpeechlet(DirectiveService directiveService) {
        this.directiveService = directiveService;
    }


    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        SessionStartedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        responseBuilder = new StringBuilder();
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        LaunchRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        SystemState systemState = getSystemState(requestEnvelope.getContext());
        apiEndpoint = systemState.getApiEndpoint();
        deviceId = systemState.getDevice().getDeviceId();
        apiAccessToken = systemState.getApiAccessToken();

        //apiAccessToken = requestEnvelope.getSession().getUser().getAccessToken();
        responseBuilder = new StringBuilder();

        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        if (!propertyReader.isPropertyRead()) {
            SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>" + propertyReader.getFatalError() + "</speak>");
        }

/*        Permissions permissions = session.getUser().getPermissions();
        if (permissions == null) {
            log.info("Checked the Permissions in the User Session - The user hasn't authorized the skill. Sending a permissions card.");
            return getPermissionsResponse();
        }*/

        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        String nameRequestUrl = apiEndpoint + "/v2/accounts/~current/settings/Profile.name";
        String emailRequestUrl = apiEndpoint + "/v2/accounts/~current/settings/Profile.email";

        log.info("Request will be made to the following URL: {}", nameRequestUrl);
        log.info("Request will be made to the following URL: {}", emailRequestUrl);

//        HttpGet httpGetName = new HttpGet(nameRequestUrl);
        HttpGet httpGetEmail = new HttpGet(emailRequestUrl);

//        httpGetName.addHeader("Authorization", "Bearer " + apiAccessToken);
        httpGetEmail.addHeader("Authorization", "Bearer " + apiAccessToken);

/*        try {
            HttpResponse addressResponse = closeableHttpClient.execute(httpGetName);
            int statusCode = addressResponse.getStatusLine().getStatusCode();

            log.info("The Device Address API responded with a status code of {}", statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = addressResponse.getEntity();
                userName = EntityUtils.toString(httpEntity);

                log.debug("The user name is == " + userName);

            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                log.info("Failed to authorize with a status code of {}", statusCode);
                return  getPermissionsResponse();
            } else {
                String errorMessage = "Device Address API query failed with status code of " + statusCode;
                log.info(errorMessage);
            }
        }  catch (IOException e) {
        } finally {
            log.info("Request to Address Device API completed.");
        }*/

        try {
            HttpResponse addressResponse = closeableHttpClient.execute(httpGetEmail);
            int statusCode = addressResponse.getStatusLine().getStatusCode();

            log.info("The Device Address API responded with a status code of {}", statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = addressResponse.getEntity();
                userEmail = EntityUtils.toString(httpEntity);

                log.debug("The user email is == " + userEmail);

            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                log.info("Failed to authorize with a status code of {}", statusCode);
//                return  getPermissionsResponse();
            } else {
                String errorMessage = "Device Address API query failed with status code of " + statusCode;
                log.info(errorMessage);
            }
        }  catch (IOException e) {
        } finally {
            log.info("Request to Address Device API completed.");
        }

        //ToDo needs to be removed soon. HACK

        userName = "Vikas";
        userEmail = "malhotra.vikas@gmail.com";

        session.setAttribute("User_Name", userName);
        session.setAttribute("User_Email", userEmail);

        if (userEmail != null && userName != null) {
            log.debug("Saving stuff");
            SideKickContact contact = new SideKickContact(userEmail, userName);

            AmazonDynamoDB dynamoDBClient = new AmazonDynamoDBClient();
            DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
//            mapper.save(contact);
            log.debug("Saved stuff");
        }


        return getWelcomeResponse();

    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        log.info("onIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());

        String intentName = requestEnvelope.getRequest().getIntent().getName();

        log.debug("Entering intent name == " + intentName);

        if ("AboutUsIntent".equals(intentName)) {
            return handleAboutUsIntent(requestEnvelope);
        } else if ("ConsumerIntent".equals(intentName)) {
            return handleConsumerIntent(requestEnvelope);
        } else if ("BusinessIntent".equals(intentName)) {
            return handleBusinessIntent(requestEnvelope);
        } else if ("ContactUsIntent".equals(intentName)) {
            return handleContactUsIntent(requestEnvelope);
        } else if ("YesIntent".equals(intentName)) {
            return handleYesIntent(requestEnvelope);
        } else if ("NoIntent".equals(intentName)) {
            return handleNoIntent(requestEnvelope);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return  newAskResponse(propertyReader.getSpeechHelp(), false, propertyReader.getSpeechHelp(), false);
        } else if ("AMAZON.FallbackIntent".equals(intentName)) {
            return handleNoMatchingIntent(requestEnvelope);
        }else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText(propertyReader.getGoodBye());
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText(propertyReader.getGoodBye());
            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            String outputSpeech = propertyReader.getSpeechSorry();
            String repromptText = propertyReader.getSpeechReprompt();

            return newAskResponse(outputSpeech, true, repromptText, true);
        }
    }

    private SpeechletResponse handleNoMatchingIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        return  newAskResponse(propertyReader.getSpeechDidNotUnderstand(), false, propertyReader.getSpeechDidNotUnderstand(), false);
    }

    private SpeechletResponse handleNoIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SimpleCard card = new SimpleCard();
        card.setTitle(propertyReader.getSkillName());

        log.debug("In handleNoIntent");
        String noResponse = "No problem!! Is there anything else we can help you with today? " + propertyReader.getSpeechReprompt();
        SpeechletResponse response = newAskResponse(noResponse, false, propertyReader.getSpeechReprompt(), false);
        response.setCard(card);

        return response;

    }

    private SpeechletResponse handleYesIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        String stage = (String) session.getAttribute("Stage");
        if (stage == null) {
            stage = "AboutUsIntent";
        }

        log.debug("Coming into Yes Intent from " + stage);

        if (stage.equalsIgnoreCase("BusinessIntent")) {
            return handleContactUsIntent(requestEnvelope);
        } else if (stage.equalsIgnoreCase("ConsumerIntent")) {
            return handleContactUsIntent(requestEnvelope);
        } else if (stage.equalsIgnoreCase("AboutUsIntent")) {
            return handleConsumerIntent(requestEnvelope);
        }

        return handleNoMatchingIntent(requestEnvelope);
    }

    private SpeechletResponse handleContactUsIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();
        session.setAttribute("Stage", intent.getName());
        String name = (String) session.getAttribute("User_Name");
        String email = (String) session.getAttribute("User_Email");

        try {
            // sent a thank you email to the customer
            AWSeMailService.sendEmail("vikas@uttrdev.co", email, propertyReader.getEmailSubject(), propertyReader.getEmailMessage(), "Uttr Dev");

            String message = "We have a new customer contact us from : " + name;
            message = message + " . The contact email address is : " + email;

            // Sent a heads up email to Moe
            AWSeMailService.sendEmail("vikas@uttrdev.co", "moe@uttrdev.co", "Heads up new customer", message, "Uttr Dev Contact Us");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.removeAttribute("Stage");

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(propertyReader.getEmailMessage());
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private SpeechletResponse handleBusinessIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SimpleCard card = new SimpleCard();
        card.setTitle(propertyReader.getSkillName());

        log.debug("In handleBusinessIntent");
        SpeechletResponse response = newAskResponse(propertyReader.getSpeechBusiness(), false, propertyReader.getSpeechReprompt(), false);
        response.setCard(card);

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();
        session.setAttribute("Stage", "BusinessIntent");

        return response;
    }

    private SpeechletResponse handleConsumerIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SimpleCard card = new SimpleCard();
        card.setTitle(propertyReader.getSkillName());

        log.debug("In handleConsumerIntent");
        SpeechletResponse response = newAskResponse(propertyReader.getSpeechConsumer(), false, propertyReader.getSpeechReprompt(), false);
        response.setCard(card);

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();
        session.setAttribute("Stage", "ConsumerIntent");

        return response;
    }

    private SpeechletResponse handleAboutUsIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SimpleCard card = new SimpleCard();
        card.setTitle(propertyReader.getSkillName());

        log.debug("In handleAboutUsIntent");
        SpeechletResponse response = newAskResponse(propertyReader.getSpeechAbout(), false, propertyReader.getSpeechReprompt(), false);
        response.setCard(card);

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();
        session.setAttribute("Stage", "AboutUsIntent");

        return response;
    }


    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        SessionEndedRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();

        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        responseBuilder = new StringBuilder();
        // any session cleanup logic would go here
    }

    /**
     * Function to handle the onLaunch skill behavior.
     *
     * @return SpeechletResponse object with voice/card response to return to the user
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechOutput = propertyReader.getWelcomeMessage();
        // If the user either does not reply to the welcome message or says something that is not
        // understood, they will be prompted again with this text.
        String repromptText = propertyReader.getSpeechReprompt();

        return newAskResponse(speechOutput, false, repromptText, false);
    }

    /**
     * Wrapper for creating the Ask response from the input strings.
     *
     * @param stringOutput
     *            the output to be spoken
     * @param isOutputSsml
     *            whether the output text is of type SSML
     * @param repromptText
     *            the reprompt for if the user doesn't reply or is misunderstood.
     * @param isRepromptSsml
     *            whether the reprompt text is of type SSML
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
            String repromptText, boolean isRepromptSsml) {
        OutputSpeech outputSpeech, repromptOutputSpeech;
        if (isOutputSsml) {
            outputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
        } else {
            outputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
        }

        if (isRepromptSsml) {
            repromptOutputSpeech = new SsmlOutputSpeech();
            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
        } else {
            repromptOutputSpeech = new PlainTextOutputSpeech();
            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
        }
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }

    /**
     * Dispatches a progressive response.
     *
     * @param requestId
     *            the unique request identifier
     * @param text
     *            the text of the progressive response to send
     * @param systemState
     *            the SystemState object
     * @param apiEndpoint
     *            the Alexa API endpoint
     */
    private void dispatchProgressiveResponse(String requestId, String text, SystemState systemState, String apiEndpoint) {
        DirectiveEnvelopeHeader header = DirectiveEnvelopeHeader.builder().withRequestId(requestId).build();
        SpeakDirective directive = SpeakDirective.builder().withSpeech(text).build();
        DirectiveEnvelope directiveEnvelope = DirectiveEnvelope.builder()
                .withHeader(header).withDirective(directive).build();

        if(systemState.getApiAccessToken() != null && !systemState.getApiAccessToken().isEmpty()) {
            String token = systemState.getApiAccessToken();
            try {
                directiveService.enqueue(directiveEnvelope, apiEndpoint, token);
            } catch (Exception e) {
                log.error("FAtal error  - Failed to dispatch a progressive response", e);
            }
        }
    }

    /**
     * Helper method that retrieves the system state from the request context.
     * @param context request context.
     * @return SystemState the systemState
     */
    private SystemState getSystemState(Context context) {
        return context.getState(SystemInterface.class, SystemState.class);
    }

    private SpeechletResponse getPermissionsResponse() {
        String speechText = propertyReader.getSpeechPermission();

        log.debug("Missing PErmissions");
        // Create the permission card content.
        // The differences between a permissions card and a simple card is that the
        // permissions card includes additional indicators for a user to enable permissions if needed.
        AskForPermissionsConsentCard card = new AskForPermissionsConsentCard();
        card.setTitle("Authorize access for name and email");

        Set<String> permissions = new HashSet<>();
        //permissions.add("alexa::profile:name:read");
        permissions.add("alexa::profile:email:read");

        card.setPermissions(permissions);
        log.debug("PErmissions requested for email and name read");

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(speechText);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setCard(card);

        log.debug("PErmissions card sent");

        return response;
    }



}
