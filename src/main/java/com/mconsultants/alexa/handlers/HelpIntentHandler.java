package main.java.com.mconsultants.alexa.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import main.java.com.mconsultants.alexa.utilities.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HelpIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    private static final Logger log = LoggerFactory.getLogger(HelpIntentHandler.class);
    private static PropertyReader propertyReader = PropertyReader.getPropertyReader();

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = propertyReader.getSpeechHelp();
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(speechText)
                .withSimpleCard(propertyReader.getSkillName(), speechText)
                .build();
    }
}