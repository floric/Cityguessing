package org.floric.app;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
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
    private boolean receivedNewAnswer = true;

    public Game() {
        guesser = new Guesser();
    }

    public SpeechletResponse reactToAnswer(GameResponse response) throws SpeechletException {
        Card card = null;

        if (response.equals(GameResponse.STOP)) {
            setMode(GameMode.QUIT);
        } else if (currentMode.equals(GameMode.READY) || currentMode.equals(GameMode.RESTART)) {
            if (response.equals(GameResponse.YES)) {
                setMode(GameMode.PLAY);
                receivedNewAnswer = true;
            } else {
                setMode(GameMode.HELP);
            }
        } else if (currentMode.equals(GameMode.PLAY)) {
            if (guesser.isGuessingFinished()) {
                if (guesser.wasGuessingSuccessfull()) {
                    SimpleCard simpleCard = new SimpleCard();
                    simpleCard.setTitle(guesser.getFoundCityName() + " erraten");
                    simpleCard.setContent("Ich habe " + guesser.getFoundCityName() + " erraten. " +
                            "Dabei habe ich dir " + guesser.getAskedQuestionsCount() + " Fragen gestellt, von denen " +
                            "Du " + guesser.getAnsweredQuestionsCount() + " Fragen beantworten konntest."
                    );
                    card = simpleCard;
                } else {
                    SimpleCard simpleCard = new SimpleCard();
                    simpleCard.setTitle("Stadt nicht erraten");
                    simpleCard.setContent("Ich konnte deine erdachte Stadt leider nicht erraten. Aber vielleicht beim nächsten Versuch.");
                    card = simpleCard;
                }

                if (response.equals(GameResponse.YES) || response.equals(GameResponse.REPEAT)) {
                    setMode(GameMode.RESTART);
                } else {
                    setMode(GameMode.QUIT);
                }
            } else {
                if (response.equals(GameResponse.YES) || response.equals(GameResponse.NO) || response.equals(GameResponse.MAYBE)) {
                    guesser.receiveResponse(response);
                    receivedNewAnswer = true;
                } else if (response.equals(GameResponse.REPEAT)) {
                    // TODO REPEAT MODE
                } else if (response.equals(GameResponse.HELP)) {
                    setMode(GameMode.HELP);
                }
            }
        } else if (currentMode.equals(GameMode.HELP)) {
            if (response.equals(GameResponse.YES) || response.equals(GameResponse.MAYBE)) {
                setMode(GameMode.PLAY);
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
            return getHelp(card);
        } else if (currentMode.equals(GameMode.READY)) {
            return getStart(card);
        } else if (currentMode.equals(GameMode.PLAY)) {
            if (receivedNewAnswer) {
                currentQuestion = guesser.getNextQuestion();
            }
            receivedNewAnswer = false;
            return newAskResponse(currentQuestion, "Ich wiederhole: " + currentQuestion);
        } else if (currentMode.equals(GameMode.RESTART)) {
            guesser.restart();
            return getRestart(card);
        } else if (currentMode.equals(GameMode.QUIT)) {
            return getQuitMessage(card);
        } else {
            throw new SpeechletException("Unknown gamemode");
        }
    }

    public void setMode(GameMode newMode) {
        this.currentMode = newMode;
    }

    private SpeechletResponse getHelp(Card card) {
        String helpMessage = "In dem Spiel geht es darum, dass ich deine erdachte Stadt ermittle. " +
                "Mittels \"Ja\", \"Nein\" oder \"Weiß nicht\" antwortest du dazu auf meine Fragen. " +
                "Mit \"Neustarten\" können wir ein neues Spiel beginnen und mit \"Stop\" kannst du das Spiel jederzeit beenden. Alles klar?";

        return newAskResponse(helpMessage, "Alles klar?", card);
    }

    private SpeechletResponse getStart(Card card) {
        return newAskResponse("Hey, los gehts! Kann es losgehen?", "Kann es losgehen?", card);
    }

    private SpeechletResponse getQuitMessage(Card card) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Schade, bis zum nächsten Mal!");

        if (card != null) {
            return SpeechletResponse.newTellResponse(outputSpeech, card);
        }

        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private SpeechletResponse getRestart(Card card) {
        return newAskResponse("Ok, wir beginnen von vorne. Bist du bereit?", "Bereit?", card);
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
