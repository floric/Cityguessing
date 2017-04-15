package org.floric.app;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;

/**
 * Created by florian on 4/13/17.
 */
public class App implements Speechlet {

    private Game game;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        game = new Game();
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        String welcomeMessage = "Hallo, ich kann nach Städten raten. Bitte überlege dir eine Stadt. " +
                "Ich werde dann Fragen stellen, die du mit \"ja\", \"nein\" oder \"ich weiß nicht\" beantworten kannst. " +
                "Bist du bereit?";

        return Game.newAskResponseWithoutReprompt(welcomeMessage);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        Game.GameResponse response = Game.GameResponse.HELP;

        if ("AMAZON.NoIntent".equals(intentName)) {
            response = Game.GameResponse.NO;
        } else if ("AMAZON.YesIntent".equals(intentName)) {
            response = Game.GameResponse.YES;
        } else if ("MaybeIntent".equals(intentName)) {
            response = Game.GameResponse.MAYBE;
        } else if ("AMAZON.RepeatIntent".equals(intentName) || "AMAZON.StartOverIntent".equals(intentName)) {
            response = Game.GameResponse.REPEAT;
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            response = Game.GameResponse.HELP;
        } else if ("AMAZON.StopIntent".equals(intentName) || "AMAZON.CancelIntent".equals(intentName)) {
            response = Game.GameResponse.STOP;
        }

        return game.reactToAnswer(response);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
