package main.java.com.mconsultants.alexa.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.com.mconsultants.alexa.utilities.PropertyReader;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class CancelAndStopIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.CancelIntent").or(intentName("AMAZON.StopIntent")));
    }
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = propertyReader.getGoodBye();
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(propertyReader.getSkillName(), speechText)
                .build();
    }
}