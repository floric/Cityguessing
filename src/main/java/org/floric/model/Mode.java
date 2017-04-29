package org.floric.model;

import com.amazon.speech.speechlet.SpeechletResponse;
import org.floric.app.Game;
import org.floric.guesser.Guesser;

/**
 * Created by florian on 4/29/17.
 */
public interface Mode {
    SpeechletResponse getQuestion(Guesser guesser);
    void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser);
}
