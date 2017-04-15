package org.floric.app;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import org.floric.guesser.Guesser;

import java.io.FileNotFoundException;

/**
 * Created by florian on 4/13/17.
 */
public class App implements Speechlet {

    private Guesser guesser;

    public static void main(String[] args) throws FileNotFoundException {

    }

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        try {
            guesser = new Guesser();
        } catch (FileNotFoundException e) {
            throw new SpeechletException("Leider findet mein Gehirn keine Städte!");
        }

        guesser.start();
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        String welcomeMessage = "Hallo, ich möchte gerne nach Städten raten. Bitte überlege dir eine Stadt." +
                "Ich werde dann Fragen stellen, die du mit \"ja\", \"nein\" oder \"ich weiß nicht\" beantworten kannst.";
        String helpMessage = "Alles klar?";

        return newAskResponse(welcomeMessage, helpMessage);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("AMAZON.NoIntent".equals(intentName)) {
            return null;
        } else if ("AMAZON.YesIntent".equals(intentName)) {
            return getHelp();
        } else if ("MaybeIntent".equals(intentName)) {
            return getHelp();
        } else if ("AMAZON.RepeatIntent".equals(intentName)) {
            return getHelp();
        } else if ("AMAZON.StartOverIntent".equals(intentName)) {
            return getHelp();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelp();
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }

    private SpeechletResponse getHelp() {
        String helpText = "Hier kann ich dir mehr über den Skill erzählen.";
        String repromptText = "Noch fragen?";

        return newAskResponse(helpText, repromptText);
    }

    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }
}
