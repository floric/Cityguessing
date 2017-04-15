package org.floric.app;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import org.floric.guesser.Guesser;

/**
 * Created by florian on 4/15/17.
 */
public class Game {

    public enum GameMode {
        READY,
        PLAY,
        HELP,
        RESTART,
        QUIT
    }

    public enum GameResponse {
        YES,
        NO,
        MAYBE,
        REPEAT,
        HELP,
        STOP
    }

    private Guesser guesser;
    private GameMode currentMode = GameMode.READY;
    private String currentQuestion = "";

    public Game() {
        guesser = new Guesser();
    }

    public SpeechletResponse reactToAnswer(GameResponse response) throws SpeechletException {
        if (currentMode.equals(GameMode.READY) || currentMode.equals(GameMode.RESTART)) {
            if (response.equals(GameResponse.YES)) {
                setMode(GameMode.PLAY);
            } else {
                setMode(GameMode.HELP);
            }
        } else if (currentMode.equals(GameMode.PLAY)) {
            if (guesser.isGuessingFinished()) {
                if (response.equals(GameResponse.YES)) {
                    setMode(GameMode.RESTART);
                } else {
                    setMode(GameMode.QUIT);
                }
            } else {
                if (response.equals(GameResponse.YES) || response.equals(GameResponse.NO) || response.equals(GameResponse.MAYBE)) {
                    guesser.receiveResponse(response);
                } else if (response.equals(GameResponse.STOP)) {
                    setMode(GameMode.QUIT);
                } else if (response.equals(GameResponse.HELP)) {
                    setMode(GameMode.HELP);
                }
            }
        } else if (currentMode.equals(GameMode.HELP)) {
            if (response.equals(GameResponse.YES) || response.equals(GameResponse.MAYBE)) {
                setMode(GameMode.PLAY);
            } else if (response.equals(GameResponse.STOP)) {
                setMode(GameMode.QUIT);
            } else if (response.equals(GameResponse.HELP)) {
                setMode(GameMode.HELP);
            }
        } else if (currentMode.equals(GameMode.RESTART)) {
            if (response.equals(GameResponse.YES) || response.equals(GameResponse.MAYBE)) {
                setMode(GameMode.PLAY);
            } else {
                setMode(GameMode.HELP);
            }
        }

        // return current modes answer
        if (currentMode.equals(GameMode.HELP)) {
            return getHelp();
        } else if (currentMode.equals(GameMode.READY)) {
            return getStart();
        } else if (currentMode.equals(GameMode.PLAY)) {
            currentQuestion = guesser.getNextQuestion();
            return newAskResponse(currentQuestion, "Ich wiederhole: " + currentQuestion);
        } else if (currentMode.equals(GameMode.RESTART)) {
            guesser.restart();
            return getRestart();
        } else if (currentMode.equals(GameMode.QUIT)) {
            return getQuitMessage();
        } else {
            throw new SpeechletException("Unknown gamemode");
        }
    }

    public void setMode(GameMode newMode) {
        this.currentMode = newMode;
    }

    private SpeechletResponse getHelp() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("In dem Spiel geht es darum, dass ich deine erdachte Stadt ermittle. " +
                "Mittels \"Ja\", \"Nein\" oder \"Weiß nicht\" antwortest du dazu auf meine Fragen. " +
                "Mit \"Neustarten\" können wir ein neues Spiel beginnen und mit \"Stop\" kannst du das Spiel jederzeit beenden. Alles klar?");

        return SpeechletResponse.newAskResponse(outputSpeech, new Reprompt());
    }

    private SpeechletResponse getStart() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Hey, los gehts! Kann es losgehen?");

        return SpeechletResponse.newAskResponse(outputSpeech, new Reprompt());
    }

    private SpeechletResponse getQuitMessage() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Schade, bis zum nächsten Mal!");

        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private SpeechletResponse getRestart() {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Ok, wir beginnen von vorne. Bist du bereit?");

        return SpeechletResponse.newAskResponse(outputSpeech, new Reprompt());
    }

    public static SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }

    public static SpeechletResponse newAskResponseWithoutReprompt(String stringOutput) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }
}
