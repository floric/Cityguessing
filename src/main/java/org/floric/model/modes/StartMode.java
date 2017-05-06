package org.floric.model.modes;

import com.amazon.speech.speechlet.SpeechletResponse;
import org.floric.app.Game;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/29/17.
 */
public class StartMode implements Mode {

    @Override
    public SpeechletResponse getQuestion(Guesser guesser) {
        return Game.newAskResponse("Hallo, ich kann nach Städten raten. Bitte überlege dir eine Stadt. " +
                "Ich werde dann Fragen stellen, die du mit \"ja\", \"nein\" oder \"ich weiß nicht\" beantworten kannst. Alles klar?",
                "Bist du bereit?");
    }

    @Override
    public void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser) {
        switch (response) {
            case YES:
                game.setMode(Game.GameMode.PLAY);
                break;
            case NO:
            case HELP:
            case MAYBE:
                game.setMode(Game.GameMode.HELP);
                break;
            case REPEAT:
                break;
            case RESTART:
                game.setMode(Game.GameMode.RESTART);
                break;
            case STOP:
                game.setMode(Game.GameMode.QUIT);
                break;
        }
    }
}
