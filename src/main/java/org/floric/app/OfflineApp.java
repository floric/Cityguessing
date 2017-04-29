package org.floric.app;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;

import java.sql.Date;
import java.time.Instant;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by florian on 4/23/17.
 */
public class OfflineApp {

    private static Intent INTENT_YES = Intent.builder().withName("AMAZON.YesIntent").build();
    private static Intent INTENT_NO = Intent.builder().withName("AMAZON.NoIntent").build();
    private static Intent INTENT_MAYBE = Intent.builder().withName("MaybeIntent").build();
    private static Intent INTENT_REPEAT = Intent.builder().withName("AMAZON.RepeatIntent").build();
    private static Intent INTENT_START_OVER = Intent.builder().withName("AMAZON.StartOverIntent").build();
    private static Intent INTENT_STOP = Intent.builder().withName("AMAZON.StopIntent").build();
    private static Intent INTENT_HELP = Intent.builder().withName("AMAZON.HelpIntent").build();

    public static void main(String[] args) throws SpeechletException {

        App app = new App();

        Session session = Session.builder()
                .withSessionId("123")
                .withApplication(new Application("ABC"))
                .withIsNew(true)
                .withUser(
                        User.builder()
                                .build())
                .build();

        String requestId = "123";

        app.onSessionStarted(
                SessionStartedRequest.builder()
                        .withTimestamp(Date.from(Instant.now()))
                        .withLocale(Locale.GERMANY)
                        .withRequestId(requestId)
                        .build(),
                session);

        SpeechletResponse response = app.onLaunch(
                LaunchRequest.builder()
                        .withRequestId(requestId)
                        .withLocale(Locale.GERMANY)
                        .withTimestamp(Date.from(Instant.now()))
                        .build(),
                session);

        while (!response.getShouldEndSession()) {
            System.out.println(getContentFromOutputSpeech(response.getOutputSpeech()));
            System.out.println("Reprompt: " + getContentFromOutputSpeech(response.getReprompt().getOutputSpeech()));

            Scanner scanner = new Scanner(System.in);
            Intent nextIntent = mapStringToIntent(scanner.next());

            response = app.onIntent(IntentRequest.builder()
                            .withIntent(nextIntent)
                            .withRequestId(requestId)
                            .withLocale(Locale.GERMANY)
                            .withTimestamp(Date.from(Instant.now()))
                            .build()
                    , session);
        }

        app.onSessionEnded(SessionEndedRequest.builder()
                        .withRequestId(requestId)
                        .withLocale(Locale.GERMANY)
                        .withTimestamp(Date.from(Instant.now()))
                        .build()
                , session);
    }

    private static Intent mapStringToIntent(String answer) {
        if (answer.equals("y")) {
            return INTENT_YES;
        } else if (answer.equals("n")) {
            return INTENT_NO;
        } else if (answer.equals("m")) {
            return INTENT_MAYBE;
        } else if (answer.equals("r")) {
            return INTENT_REPEAT;
        } else if (answer.equals("s")) {
            return INTENT_START_OVER;
        } else if (answer.equals("q")) {
            return INTENT_STOP;
        } else if (answer.equals("h")) {
            return INTENT_HELP;
        } else {
            throw new IllegalArgumentException("Currently not supported!");
        }
    }

    private static String getContentFromOutputSpeech(OutputSpeech speech) {
        return speech instanceof PlainTextOutputSpeech ?
                ((PlainTextOutputSpeech) speech).getText() : "Unknown";
    }
}
