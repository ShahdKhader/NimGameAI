package com.example.ai1;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import javax.swing.*;

public class donateController {

    @FXML
    private Button e, m, h;

    @FXML
    private Button eat1, eat2, eat3;

    @FXML
    private ImageView i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20, i21;

    private int level;
    private boolean playerTurn;
    private SimpleIntegerProperty remainingSticksProperty;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private void startGame(int level) {
        System.out.println(level);
        switch (level) {
            case 1:
                easy();
                break;
            case 2:
                medium();
                break;
            case 3:
                hard();
                break;
            default:
                System.out.println("Invalid level selected.");
        }
    }

    private void easy() {
        remainingSticksProperty = new SimpleIntegerProperty(21);
        playerTurn = true;
        Platform.runLater(() -> System.out.println("Remaining sticks: " + remainingSticksProperty.get()));
        Platform.runLater(() -> System.out.println("Your turn! Choose 1, 2, or 3 sticks."));
        remainingSticksProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                Platform.runLater(() -> {
                    System.out.println("Player wins! Congratulations!");
                    showResultDialog("Congratulations! You won!");
                });
            } else {
                Platform.runLater(() -> hideImages(21 - newValue.intValue()));
            }
        });
        playTurn();
    }

    private void medium() {
        remainingSticksProperty = new SimpleIntegerProperty(21);
        playerTurn = true;
        Platform.runLater(() -> System.out.println("Remaining sticks: " + remainingSticksProperty.get()));
        Platform.runLater(() -> System.out.println("Your turn! Choose 1, 2, or 3 sticks. (Harder level)"));
        remainingSticksProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                Platform.runLater(() -> {
                    System.out.println("Player wins! Congratulations! (Harder level)");
                    showResultDialog("Congratulations! You won! (Harder level)");
                });
            } else {
                Platform.runLater(() -> hideImages(21 - newValue.intValue()));
            }
        });

        playTurn();
    }

    private void hard() {
        remainingSticksProperty = new SimpleIntegerProperty(21);
        playerTurn = true;
        Platform.runLater(() -> System.out.println("Remaining sticks: " + remainingSticksProperty.get()));
        Platform.runLater(() -> System.out.println("Your turn! Choose 1, 2, or 3 sticks. (Hardest level)"));

        remainingSticksProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                Platform.runLater(() -> {
                    System.out.println("Player wins! Congratulations! (Hardest level)");
                    showResultDialog("Congratulations! You won! (Hardest level)");
                });
            } else {
                Platform.runLater(() -> hideImages(21 - newValue.intValue()));
            }
        });
        playTurn();
    }

    private void hideImages(int numToHide) {
        for (int i = 1; i <= numToHide; i++) {
            String imageViewId = "i" + i;
            Platform.runLater(() -> {
                try {
                    ImageView imageView = (ImageView) getClass().getDeclaredField(imageViewId).get(this);
                    imageView.setVisible(false);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    void eat1Clicked() {
        if (playerTurn) {
            processPlayerMove(1);
        }
    }

    @FXML
    void eat2Clicked() {
        if (playerTurn) {
            processPlayerMove(2);
        }
    }

    @FXML
    void eat3Clicked() {
        if (playerTurn) {
            processPlayerMove(3);
        }
    }

    private void processPlayerMove(int playerMove) {
        synchronized (remainingSticksProperty) {
            remainingSticksProperty.set(remainingSticksProperty.get() - playerMove);
            Platform.runLater(() -> {
                System.out.println("Player removes " + playerMove + " sticks.");
                System.out.println("Remaining sticks: " + remainingSticksProperty.get());
            });
        }

        if (remainingSticksProperty.get() > 0) {
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(event -> playTurn());
            pause.play();
        }
    }

    private void playTurn() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            Task<Void> turnTask = new Task<>() {
                @Override
                protected Void call() {
                    final int[] computerMove = {0};
                    int computerMoveE = Math.min(3, remainingSticksProperty.get());
                    int computerMoveM = (remainingSticksProperty.get() % 4 == 0) ? 3 : remainingSticksProperty.get() % 4;

                    synchronized (remainingSticksProperty) {
                        if (getLevel() == 1) computerMove[0] = computerMoveE;
                        else if (getLevel() == 2) computerMove[0] = computerMoveM;
                        else if (getLevel() == 3) {
                            int alpha = Integer.MIN_VALUE;
                            int beta = Integer.MAX_VALUE;

                            for (int move = 1; move <= 3; move++) {
                                int newSticks = remainingSticksProperty.get() - move;
                                int score = -minimax(newSticks, false, 3, -beta, -alpha);

                                if (score > alpha) {
                                    computerMove[0] = move;
                                    alpha = score;
                                }
                            }
                            if ((remainingSticksProperty.get() - computerMove[0]) % 4 != 0) {
                                computerMove[0] = Math.min(3, remainingSticksProperty.get() % 4);
                            }
                        }
                        remainingSticksProperty.set(remainingSticksProperty.get() - computerMove[0]);
                    }

                    Platform.runLater(() -> {
                        System.out.println("Computer removes " + computerMove[0] + " sticks.");
                        System.out.println("Remaining sticks: " + remainingSticksProperty.get());
                    });

                    return null;
                }
            };

            turnTask.setOnSucceeded(e -> handleTurnCompletion());

            new Thread(turnTask).start();
        });

        pause.play();
    }

    private void handleTurnCompletion() {
        // Check for game end state after the computer's move is completed
        if (remainingSticksProperty.get() <= 0) {
            Platform.runLater(() -> {
                System.out.println("Computer wins! Better luck next time.");
                showResultDialog("Computer wins! Better luck next time.");
            });
        } else {
            // Set player turn
            playerTurn = true;
            // Notify the player to make a move
            Platform.runLater(() -> System.out.println("Your turn! Choose 1, 2, or 3 sticks."));
        }
    }

    @FXML
    void eClicked() {
        m.setStyle("-fx-background-color: #cecddf;");
        m.setCursor(Cursor.DEFAULT);
        h.setStyle("-fx-background-color: #cecddf;");
        h.setCursor(Cursor.DEFAULT);
        setLevel(1);
        startGame(getLevel());
    }

    @FXML
    void mClicked() {
        e.setStyle("-fx-background-color: #cecddf;");
        e.setCursor(Cursor.DEFAULT);
        h.setStyle("-fx-background-color: #cecddf;");
        h.setCursor(Cursor.DEFAULT);
        setLevel(2);
        startGame(getLevel());
    }

    @FXML
    void hClicked() {
        e.setStyle("-fx-background-color: #cecddf;");
        e.setCursor(Cursor.DEFAULT);
        m.setStyle("-fx-background-color: #cecddf;");
        m.setCursor(Cursor.DEFAULT);
        setLevel(3);
        startGame(getLevel());
    }

    private void showResultDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static int minimax(int remainingSticks, boolean isMaximizing, int depth, int alpha, int beta) {
        if (remainingSticks <= 0 || depth == 0) {
            return (isMaximizing) ? -evaluate(remainingSticks) : evaluate(remainingSticks);
        }

        int bestScore = (isMaximizing) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int move = 1; move <= 3; move++) {
            int newSticks = remainingSticks - move;
            int score = -minimax(newSticks, !isMaximizing, depth - 1, -beta, -alpha);

            if (isMaximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);
            }

            if (alpha >= beta) {
                break;
            }
        }

        return bestScore;
    }

    public static int evaluate(int remainingSticks) {
        return remainingSticks;
    }
}