package org.floric.model.modes;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SimpleCard;
import org.floric.app.Game;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/29/17.
 */
public class PlayMode implements Mode {

    private Guesser.Response lastResponse = null;
    private boolean wasRepeat = false;

    @Override
    public SpeechletResponse getQuestion(Guesser guesser) {
        if (!wasRepeat) {
            lastResponse = guesser.getNextQuestion();
        } else {
            wasRepeat = false;
        }

        if (lastResponse.isShowCard()) {
            SimpleCard card = new SimpleCard();
            card.setTitle(lastResponse.getHeadline());
            card.setContent(lastResponse.getText());

            return Game.newAskResponse(lastResponse.getText(), "Ich wiederhole: " + lastResponse.getText(), card);
        }

        return Game.newAskResponse(lastResponse.getText(), "Ich wiederhole: " + lastResponse.getText());
    }

    @Override
    public void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser) {
        if (lastResponse.getState() == Guesser.GuesserState.MULTIPLE_CITIES_LEFT) {
            switch (response) {
                case YES:
                case NO:
                case MAYBE:
                    guesser.receiveResponse(response);
                    break;
                case REPEAT:
                    wasRepeat = true;
                    break;
                case RESTART:
                    game.setMode(Game.GameMode.RESTART);
                    break;
                case HELP:
                    game.setMode(Game.GameMode.HELP);
                    break;
                case STOP:
                    game.setMode(Game.GameMode.QUIT);
                    break;
            }
        } else if (lastResponse.getState() == Guesser.GuesserState.NO_CITY_LEFT ||
                lastResponse.getState() == Guesser.GuesserState.ONE_CITY_LEFT) {
            switch (response) {
                case YES:
                case RESTART:
                case MAYBE:
                    game.setMode(Game.GameMode.RESTART);
                    break;
                case REPEAT:
                    wasRepeat = true;
                    break;
                case HELP:
                    guesser.init();
                    game.setMode(Game.GameMode.HELP);
                    break;
                case STOP:
                case NO:
                    game.setMode(Game.GameMode.QUIT);
                    break;
            }
        }
    }
}
