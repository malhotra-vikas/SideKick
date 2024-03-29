package main.java.com.mconsultants.alexa.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import main.java.com.mconsultants.alexa.utilities.PropertyReader;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class LaunchRequestHandler implements RequestHandler {
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();

    public Optional<Response> handle(HandlerInput input) {
        String speechText = "Welcome to Jack and Stack!! How can i help you today? If " +
                "you dont know what you want try saying what's up";
        // If the user either does not reply to the welcome message or says something that is not
        // understood, they will be prompted again with this text.
        String repromptText = "If you dont know what you want try saying what's up";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .withSimpleCard(propertyReader.getSkillName(), speechText)
                .build();
    }
}