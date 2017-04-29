package org.floric.app;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.google.common.collect.Maps;
import org.floric.model.Mode;
import org.floric.model.modes.*;

import java.util.Map;

/**
 * Created by florian on 4/15/17.
 */
public class Game {

    public enum GameMode {
        START,
        PLAY,
        HELP,
        QUIT,
        RESTART;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public enum GameResponse {
        YES,
        NO,
        MAYBE,
        REPEAT,
        RESTART,
        HELP,
        STOP
    }

    private Map<GameMode, Mode> modes = Maps.newHashMap();
    private GameMode currentMode = GameMode.START;

    public Game() {
        modes.put(GameMode.HELP, new HelpMode());
        modes.put(GameMode.PLAY, new PlayMode());
        modes.put(GameMode.START, new StartMode());
        modes.put(GameMode.QUIT, new QuitMode());
        modes.put(GameMode.RESTART, new RestartMode());
    }

    public void setMode(GameMode mode) {
        currentMode = mode;
    }

    public Mode getMode() {
        if (!modes.containsKey(currentMode)) {
            throw new RuntimeException("Mode unknown!");
        }

        return modes.get(currentMode);
    }

    public static SpeechletResponse newAskResponse(String stringOutput, String repromptText, Card card) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        if (card == null) {
            return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
        }

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    public static SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        return newAskResponse(stringOutput, repromptText, null);
    }
}
