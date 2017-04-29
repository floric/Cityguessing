package org.floric.app;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.SimpleCard;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/13/17.
 */
public class App implements Speechlet {

    private Game game;
    private Guesser guesser;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        game = new Game();
        guesser = new Guesser();
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        return game.getMode().getQuestion(guesser);
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
        } else if ("AMAZON.RepeatIntent".equals(intentName)) {
            response = Game.GameResponse.REPEAT;
        } else if ("AMAZON.StartOverIntent".equals(intentName)) {
            response = Game.GameResponse.RESTART;
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            response = Game.GameResponse.HELP;
        } else if ("AMAZON.StopIntent".equals(intentName) || "AMAZON.CancelIntent".equals(intentName)) {
            response = Game.GameResponse.STOP;
        }

        Mode currentmMode = game.getMode();
        currentmMode.reactToAnswer(response, game, guesser);

        Mode newMode = game.getMode();
        return newMode.getQuestion(guesser);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {

    }
}
