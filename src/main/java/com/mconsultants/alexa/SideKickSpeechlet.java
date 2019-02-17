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
import java.util.*;


public class SideKickSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(SideKickSpeechlet.class);
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();

    /**
     * The key to get the item from the intent.
     */
    private static final String NAME_SLOT = "intentname";

    private static final String ESCALATION = "ESCALATION";

    private StringBuilder responseBuilder = new StringBuilder();

    private String deviceId;
    private String apiAccessToken;
    private String apiEndpoint;

    private String userName;
    private String userEmail;

    private HashMap<String, String> MENU_GROUP;
    private HashMap<String, String> MENU_ITEM_NAME;
    private HashMap<String, String[]> MENU_ITEMS_BY_ID;
    private HashMap<String, ArrayList> MENU_ITEMS_BY_GROUP;
    private ArrayList MENU_ITEMS_GROUPED;

    private String[] MENU_ITEM;


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

        responseBuilder = new StringBuilder();

        readMenu();

        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        if (!propertyReader.isPropertyRead()) {
            SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>" + propertyReader.getFatalError() + "</speak>");
        }

        return getWelcomeResponse();

    }

    private void readMenu() {
        MENU_GROUP = new HashMap<>();
        MENU_ITEM_NAME = new HashMap<>();
        MENU_ITEMS_BY_GROUP = new HashMap<>();
        MENU_ITEMS_BY_ID = new HashMap<>();
        MENU_ITEMS_GROUPED = new ArrayList();


        HashMap map = new HashMap();


        MENU_GROUP.put("drinks", "Drinks");
        MENU_GROUP.put("lunch", "Lunch");
        MENU_GROUP.put( "sharable", "Sharable");
        MENU_GROUP.put("steaks", "Steaks");
        MENU_GROUP.put("dessert", "Dessert");
        MENU_GROUP.put("steak", "Steaks");
        MENU_GROUP.put( "sharables", "Sharable");
        MENU_GROUP.put("drink", "Drinks");

        MENU_ITEM_NAME.put("draft, tap", "draft, tap");
        MENU_ITEM_NAME.put("muskoka", "Muskoka");
        MENU_ITEM_NAME.put("muskoka half", "muskoka half");
        MENU_ITEM_NAME.put("padavena", "Padavena");
        MENU_ITEM_NAME.put("paulaner", "Paulaner");
        MENU_ITEM_NAME.put("beer", "Beer");

        MENU_ITEM = new String[10];
        MENU_ITEM[0] = "5100";
        MENU_ITEM[1] = "Drinks";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Draft Beer";
        MENU_ITEM[4] = "Draft Beer";
        MENU_ITEM[5] = "Draft Beer";
        MENU_ITEM[6] = "5";
        MENU_ITEM[7] = "7% Alcohol by volume";
        MENU_ITEM[8] = "beer";
        MENU_ITEM[9] = "Light";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_GROUPED.add(MENU_ITEM);
        log.debug("Addedd for " + MENU_ITEM[3]);

        MENU_ITEM = new String[10];
        MENU_ITEM[0] = "5101";
        MENU_ITEM[1] = "Drinks";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Muskoka";
        MENU_ITEM[4] = "Muskoka Beer";
        MENU_ITEM[5] = "Muskoka Beer";
        MENU_ITEM[6] = "8";
        MENU_ITEM[7] = "5% Alcohol by volume";
        MENU_ITEM[8] = "beer";
        MENU_ITEM[9] = "Muskoka";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_GROUPED.add(MENU_ITEM);
        log.debug("Addedd for " + MENU_ITEM[3]);

        MENU_ITEM = new String[10];
        MENU_ITEM[0] = "5102";
        MENU_ITEM[1] = "Drinks";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Muskoka Half";
        MENU_ITEM[4] = "Muskoka Half";
        MENU_ITEM[5] = "Muskoka Half";
        MENU_ITEM[6] = "5";
        MENU_ITEM[7] = "5% Alcohol by volume";
        MENU_ITEM[8] = "Muskoka";
        MENU_ITEM[9] = "Muskoka Half";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_GROUPED.add(MENU_ITEM);
        log.debug("Addedd for " + MENU_ITEM[3]);

        MENU_ITEMS_BY_GROUP.put(MENU_ITEM[1].toLowerCase(), MENU_ITEMS_GROUPED);

/*
        MENU_ITEM[0] = "5103";
        MENU_ITEM[1] = "Lunch";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Soup and Salad";
        MENU_ITEM[4] = "Soup, Salad & Garlic Pan Bread";
        MENU_ITEM[5] = "Soup, Salad & Garlic Pan Bread";
        MENU_ITEM[6] = "11.97";
        MENU_ITEM[7] = " Soup of the day, house or Caesar salad, with a lunch-size Jack’s Garlic Pan Bread.";
        MENU_ITEM[8] = "Macro";
        MENU_ITEM[9] = "Vegan";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_BY_GROUP.put(MENU_ITEM[1], MENU_ITEM);

        MENU_ITEM[0] = "5104";
        MENU_ITEM[1] = "Lunch";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Chicken Quesadilla & Salad";
        MENU_ITEM[4] = "Chicken Quesadilla & Salad";
        MENU_ITEM[5] = "Chicken Quesadilla & Salad";
        MENU_ITEM[6] = "12.25";
        MENU_ITEM[7] = "Grilled chicken, pico de gallo lots of melted Monterey cheese in toasted flour tortillas. Served with house salsa";
        MENU_ITEM[8] = "Macro";
        MENU_ITEM[9] = "Veggie";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_BY_GROUP.put(MENU_ITEM[1], MENU_ITEM);

        MENU_ITEM[0] = "5105";
        MENU_ITEM[1] = "Lunch";
        MENU_ITEM[2] = "";
        MENU_ITEM[3] = "Nachos and Side";
        MENU_ITEM[4] = "Nachos and Side";
        MENU_ITEM[5] = "Nachos and Side";
        MENU_ITEM[6] = "10.97";
        MENU_ITEM[7] = "Corn tortilla chips, tomatoes, peppers, onions, jalapeños, three cheeses and a side";
        MENU_ITEM[8] = "Macro";
        MENU_ITEM[9] = "Veggie";
        MENU_ITEMS_BY_ID.put(MENU_ITEM[0], MENU_ITEM);
        MENU_ITEMS_BY_GROUP.put(MENU_ITEM[1], MENU_ITEM);*/
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        log.info("onIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        String intentName = requestEnvelope.getRequest().getIntent().getName();
        SystemState systemState = getSystemState(requestEnvelope.getContext());

        log.debug("Entering intent name == " + intentName);

        if ("PrimaryIntent".equals(intentName)) {
            return handlePrimaryIntent(requestEnvelope);
        } else if ("PlaceOrderIntent".equals(intentName)) {
            return handlePlaceOrderIntent(requestEnvelope);
        } else if ("YesIntent".equals(intentName)) {
            return handleYesIntent(requestEnvelope);
        } else if ("NoIntent".equals(intentName)) {
            return handleNoIntent(requestEnvelope);
        } else if ("ContactHumanIntent".equals(intentName)) {
            return handleContactHumanIntent(requestEnvelope);
        } else if ("TellMeAboutIntent".equals(intentName)) {
            return handleTellMeAboutIntent(requestEnvelope);
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

    private SpeechletResponse handleTellMeAboutIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Intent intent = requestEnvelope.getRequest().getIntent();
        String itemOfInterest = intent.getSlot("SELECTED_CHOICE").getValue();

        return newAskResponse("TBD. Here i will tell you more about the selected item", true, "TBD. Here i will tell you more about the selected item", true);

    }

    private SpeechletResponse handlePlaceOrderIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {

        Intent intent = requestEnvelope.getRequest().getIntent();
        String itemOfInterest = intent.getSlot("FOOD_ITEM").getValue();
        StringBuilder itemName;
        String[] menuItem;

        log.debug("Within handlePlaceOrderIntent Captured the food item as = " + itemOfInterest);

        if (itemOfInterest != null) {
            Iterator keys = MENU_GROUP.keySet().iterator();
            String key;
            while (keys.hasNext()) {
                key = (String) keys.next();
                log.debug("The next key is -" + key);
                log.debug("Item from the MENU GROUP for this key is -" + MENU_GROUP.get(key.toLowerCase()));
            }
            ArrayList menuItemsList = MENU_ITEMS_BY_GROUP.get(itemOfInterest.toLowerCase());

            if (menuItemsList != null) {
                log.debug("Number of Menu Items found is - " + menuItemsList.size());
                itemName = new StringBuilder();

                for (int i=0; i<menuItemsList.size(); i++) {
                    menuItem = (String[]) menuItemsList.get(i);
                    log.debug("Item from the MENU GROUP for this key is -" + menuItem);
                    itemName.append(menuItem[3]);

                    if (i<menuItemsList.size()) {
                        itemName.append(", ");
                    }
                }

                return newAskResponse("Great choice!! I have " + menuItemsList.size() + " matching items. These are " + itemName.toString() + " . Which one would you like to order?", false, propertyReader.getSpeechHelp(), false);

            } else {
                String menuItemName = MENU_ITEM_NAME.get(itemOfInterest.toLowerCase());
                if (menuItemName == null) {
                    return handleNoMatchingIntent(requestEnvelope);
                } else {
                    log.debug("The selected item is = " + menuItemName);
                    return newAskResponse("Placing an order for " + menuItemName + ". Would you like to order something else as well?", false, propertyReader.getSpeechHelp(), false);
                }
            }

        } else {
            return newAskResponse("What would you like to order today?", false, propertyReader.getSpeechHelp(), false);
        }
    }

    private SpeechletResponse handlePrimaryIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Session session = requestEnvelope.getSession();

        session.setAttribute("PHASE", "SPECIALS");

        String message = "I can help you select and order food. Just tell me what are you looking for in a few words. " +
                "For example, you can say Order Chicken, Get Beer, Order steaks. Would you prefer to know about today's specials?";
        return  newAskResponse(message, false, propertyReader.getSpeechHelp(), false);

    }

    private SpeechletResponse handleNoMatchingIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SystemState systemState = getSystemState(requestEnvelope.getContext());
        apiEndpoint = systemState.getApiEndpoint();

        Session session = requestEnvelope.getSession();
        IntentRequest request = requestEnvelope.getRequest();

        Integer Iescalation = (Integer) session.getAttribute(ESCALATION);
        int escalation = 0;
        if (Iescalation != null) {
            escalation = Iescalation.intValue();
            escalation = escalation+1;

            Iescalation = new Integer(escalation);
            session.setAttribute(ESCALATION, Iescalation);
        } else {
            Iescalation = new Integer(escalation);
            session.setAttribute(ESCALATION, Iescalation);
        }

        if (escalation >= 2) {
            // escalate to human
            session.removeAttribute(ESCALATION);

            String message = "I am sorry!! It appears i am not able to help you. Let me connect you to someone who can";
            String apiEndpoint = systemState.getApiEndpoint();
            // Dispatch a progressive response to engage the user while fetching events
            dispatchProgressiveResponse(request.getRequestId(), message, systemState, apiEndpoint);

            // todo Call Skype API to place a skype call

        } else {
            return newAskResponse(propertyReader.getSpeechSorry(), false, propertyReader.getSpeechReprompt(), false);

        }

        return newAskResponse("Connecting the call...", false, "Connecting the call...", false);
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
        Session session = requestEnvelope.getSession();

        String stage = (String) session.getAttribute("PHASE");
        String menuID = (String) session.getAttribute("MENU_ID");

        log.debug("Coming into Yes Intent for " + stage);

        if (stage != null && stage.equalsIgnoreCase("SPECIALS")) {
            return handleSpecial(requestEnvelope);
        } else if (stage != null && stage.equalsIgnoreCase("PLACE_ORDER")) {
            log.debug("Recording the order for - " + menuID);
        }

        return handleNoMatchingIntent(requestEnvelope);
    }

    private SpeechletResponse handleContactHumanIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {

        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Contacting the call center for you. Please stay tuned. ");
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private SpeechletResponse handleSpecial(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Session session = requestEnvelope.getSession();

        SimpleCard card = new SimpleCard();
        card.setTitle(propertyReader.getSkillName());

        log.debug("In handleSpecial");
        String message = "We have Soup, Salad & Garlic Pan Bread for the specials. You will relish the Soup of the day, house or Caesar salad, with a lunch-size Jack’s Garlic Pan Bread. " +
                "This is our healthy vegan dish. Would you like to place an order? ";

        session.setAttribute("PHASE", "PLACE_ORDER");
        session.setAttribute("MENU_ID", "6100");
        SpeechletResponse response = newAskResponse(message, false, propertyReader.getSpeechHelp(), false);
        response.setCard(card);

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
        String speechOutput = "Welcome to Jack and Stack!! How can i help you today? If " +
                "you dont know what you want try saying what's up";
        // If the user either does not reply to the welcome message or says something that is not
        // understood, they will be prompted again with this text.
        String repromptText = "If you dont know what you want try saying what's up";

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
