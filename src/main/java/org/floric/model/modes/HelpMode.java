package org.floric.model.modes;

import com.amazon.speech.speechlet.SpeechletResponse;
import org.floric.app.Game;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/29/17.
 */
public class HelpMode implements Mode {

    @Override
    public SpeechletResponse getQuestion(Guesser guesser) {
        String helpMessage = "In dem Spiel geht es darum, dass ich deine erdachte Stadt ermittle. " +
                "Mittels \"Ja\", \"Nein\" oder \"Weiß nicht\" antwortest du dazu auf meine Fragen. " +
                "Mit \"Neustarten\" können wir ein neues Spiel beginnen und mit \"Stop\" kannst du das Spiel jederzeit beenden. Alles klar?";

        return Game.newAskResponse(helpMessage, "Alles klar?");
    }

    @Override
    public void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser) {
        switch (response) {
            case YES:
                game.setMode(Game.GameMode.PLAY);
                break;
            case NO:
            case MAYBE:
            case HELP:
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
