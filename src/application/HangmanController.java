package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class HangmanController {

    @FXML
    private Label score;
    @FXML
    private Label rounds;
    @FXML
    private Label wordDisplay;
    @FXML
    private Label times;
    @FXML
    private GridPane letterGrid;
    @FXML
    private Button startOver;

    @FXML
    private Circle head;
    @FXML
    private Line body, leftHand, rightHand, leftFoot, rightFoot;

    private String chosenWord;
    private char[] displayedWord;
    private int wrongGuesses = 0;
    private int points = 0;
    private List<String> words;
    private Timeline timer;
    private int timeLeft;

    @FXML
    public void initialize() {
        try {
            words = Files.readAllLines(new File("resources/animes.txt").toPath());
        } catch (IOException e) {
            e.printStackTrace();
            words = Arrays.asList("Naruto", "One Piece", "Bleach");
        }

        startOver.setOnAction(e -> startGame());
        startGame();
    }

    private void startGame() {
        Random rand = new Random();
        chosenWord = words.get(rand.nextInt(words.size())).toUpperCase();

        displayedWord = new char[chosenWord.length()];
        for (int i = 0; i < chosenWord.length(); i++) {
            if (chosenWord.charAt(i) == ' ') {
                displayedWord[i] = ' ';
            } else if (rand.nextDouble() < 0.3) { // reveal ~30% letters
                displayedWord[i] = chosenWord.charAt(i);
            } else {
                displayedWord[i] = '_';
            }
        }

        updateWordDisplay();
        wrongGuesses = 0;
        hideAllParts();
        createLetterButtons();
        startTimer();
    }

    private void createLetterButtons() {
        letterGrid.getChildren().clear();
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int col = 0, row = 0;
        for (char c : letters.toCharArray()) {
            Button btn = new Button(String.valueOf(c));
            btn.setPrefWidth(40);
            btn.setOnAction(e -> guessLetter(c, btn));
            letterGrid.add(btn, col, row);
            col++;
            if (col == 9) {
                col = 0;
                row++;
            }
        }
    }

    private void guessLetter(char letter, Button btn) {
        btn.setDisable(true);
        boolean found = false;
        for (int i = 0; i < chosenWord.length(); i++) {
            if (chosenWord.charAt(i) == letter) {
                displayedWord[i] = letter;
                found = true;
            }
        }

        if (!found) {
            wrongGuesses++;
            showNextBodyPart();
            if (wrongGuesses >= 6) {
                gameOver();
            }
        } else {
            updateWordDisplay();
            if (String.valueOf(displayedWord).equals(chosenWord)) {
                points += 10;
                score.setText(String.valueOf(points));
                rounds.setText(String.valueOf(Integer.parseInt(rounds.getText()) + 1));
                stopTimer();
            }
        }
    }

    private void updateWordDisplay() {
        wordDisplay.setText(String.valueOf(displayedWord).replace("", " ").trim());
    }

    private void hideAllParts() {
        head.setVisible(false);
        body.setVisible(false);
        leftHand.setVisible(false);
        rightHand.setVisible(false);
        leftFoot.setVisible(false);
        rightFoot.setVisible(false);
    }

    private void showNextBodyPart() {
        switch (wrongGuesses) {
            case 1 -> head.setVisible(true);
            case 2 -> body.setVisible(true);
            case 3 -> leftHand.setVisible(true);
            case 4 -> rightHand.setVisible(true);
            case 5 -> leftFoot.setVisible(true);
            case 6 -> rightFoot.setVisible(true);
        }
    }

    private void startTimer() {
        if (timer != null)
            timer.stop();
        timeLeft = 60;
        times.setText(String.valueOf(timeLeft));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            times.setText(String.valueOf(timeLeft));
            if (timeLeft <= 0) {
                wrongGuesses = 6;
                showNextBodyPart();
                gameOver();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null)
            timer.stop();
    }

    private void revealWord() {
        for (int i = 0; i < chosenWord.length(); i++) {
            if (displayedWord[i] == '_') {
                displayedWord[i] = chosenWord.charAt(i);
            }
        }
        updateWordDisplay();
        resetLetterButtons();
    }

    private void resetLetterButtons() {
        for (var node : letterGrid.getChildren()) {
            if (node instanceof Button btn) {
                btn.setDisable(false);
            }
        }
    }

    // === NEW METHOD: GAME OVER ===
    private void gameOver() {
        stopTimer();
        revealWord();
        points = 0;
        score.setText("0");
        rounds.setText("0");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You lost! Your poor friend died.");
        alert.showAndWait();
    }
}
