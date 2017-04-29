package org.floric.model.modes;

import com.amazon.speech.speechlet.SpeechletResponse;
import org.floric.app.Game;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/29/17.
 */
public class RestartMode implements Mode {

    @Override
    public SpeechletResponse getQuestion(Guesser guesser) {
        return Game.newAskResponse("Ok, wir beginnen von vorne. Bist du bereit?", "Bereit?");
    }

    @Override
    public void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser) {
        switch (response) {
            case YES:
                guesser.init();
                game.setMode(Game.GameMode.PLAY);
                break;
            case NO:
            case MAYBE:
            case HELP:
                game.setMode(Game.GameMode.HELP);
                break;
            case RESTART:
            case REPEAT:
                break;
            case STOP:
                game.setMode(Game.GameMode.QUIT);
                break;
        }
    }
}
