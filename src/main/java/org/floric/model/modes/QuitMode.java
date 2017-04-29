package org.floric.model.modes;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import org.floric.app.Game;
import org.floric.guesser.Guesser;
import org.floric.model.Mode;

/**
 * Created by florian on 4/29/17.
 */
public class QuitMode implements Mode {

    @Override
    public SpeechletResponse getQuestion(Guesser guesser) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Schade, bis zum nächsten Mal!");

        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    @Override
    public void reactToAnswer(Game.GameResponse response, Game game, Guesser guesser) {
    }
}
