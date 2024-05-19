package com.example.ai1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SticksController {
    private static final int EASY_DEPTH = 3;
    private static final int MEDIUM_DEPTH = 5;
    private static final int HARD_DEPTH = 7;
    private int level;
    @FXML
    private TextField numberOfGroup, numberOfSticksA, numberOfSticksB, numberOfSticksC;
    @FXML
    private TextField PlayInGroup, index, number1, number2;
    @FXML
    private Button divide, play;
    @FXML
    private Button e, m, h;
    @FXML
    private Label lA1, lA2, lA3, lA4, lA5, lA6, lA7, lA8, lA9, lA10, lA11, lA13, lA14;
    @FXML
    private Label lB1, lB2, lB3, lB4, lB5, lB6, lB7, lB8, lB9, lB10, lB11, lB12, lB13, lB14;
    @FXML
    private Label lc1, lc2, lc3, lc4, lc5, lc6, lc7, lc8, lc9, lc10, lc11, lc12, lc13, lc14;

    private ArrayList<ArrayList<Integer>> groups;
    private int depth = 0;
    private void showResultDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public ArrayList<ArrayList<Integer>> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<ArrayList<Integer>> groups) {
        this.groups = groups;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private boolean isGameEnd(ArrayList<ArrayList<Integer>> groups) {
        for (ArrayList<Integer> group : groups) {
            for (int num : group) {
                if (num > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<ArrayList<Integer>> initializeGroups(Scanner scanner) {
        System.out.println("Enter the number of starting groups (between 1 and 3):");
        int numberOfGroups = Integer.parseInt(numberOfGroup.getText());

        while (numberOfGroups < 1 || numberOfGroups > 3) {
            System.out.println("Please enter a number between 1 and 3:");
            JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 3:");
            numberOfGroups = Integer.parseInt(numberOfGroup.getText());
        }

        ArrayList<ArrayList<Integer>> groups = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            ArrayList<Integer> group = new ArrayList<>();
            System.out.println("Enter element " + " for group " + (i + 1) + ":");
            int number = 0;
            if (i == 0) number = Integer.parseInt(numberOfSticksA.getText());
            if (i == 1) number = Integer.parseInt(numberOfSticksB.getText());
            if (i == 2) number = Integer.parseInt(numberOfSticksC.getText());

            while (number < 3 || number > 15) {
                System.out.println("Please enter a number between 3 and 15:");
                if (i == 0) number = Integer.parseInt(numberOfSticksA.getText());
                if (i == 1) number = Integer.parseInt(numberOfSticksB.getText());
                if (i == 2) number = Integer.parseInt(numberOfSticksC.getText());
            }
            group.add(number);
            groups.add(group);
        }
        return groups;
    }

    private void playerMove(ArrayList<ArrayList<Integer>> groups) {
        while (true) {
            System.out.println("Player's turn. Enter the index of the group to divide:");
            int groupIndex = Integer.parseInt(PlayInGroup.getText());

            if (groupIndex >= 0 && groupIndex < groups.size() && !groups.get(groupIndex).isEmpty()) {
                System.out.println("Enter the index of the number to divide in group " + groupIndex + ":");
                int numberIndex = Integer.parseInt(index.getText());

                if (numberIndex >= 0 && numberIndex < groups.get(groupIndex).size() && groups.get(groupIndex).get(numberIndex) > 2) {
                    int chosenNumber = groups.get(groupIndex).get(numberIndex);

                    System.out.println("Enter two values to divide " + chosenNumber + " (space-separated):");
                    int value1 = Integer.parseInt(number1.getText());
                    int value2 = Integer.parseInt(number2.getText());

                    if (value1 + value2 == chosenNumber && (value1 != 1 || value2 != 1)) {
                        groups.get(groupIndex).remove(numberIndex);
                        groups.get(groupIndex).add(value1);
                        groups.get(groupIndex).add(value2);

                        System.out.println("Current groups: " + groups);
                        updateLabels();
                        if (isGameEnd(groups)) {
                            System.out.println("Player wins! The computer cannot divide further.");
                            showResultDialog("Congratulations! You won!");

                        }
                        break;
                    } else {
                        System.out.println("Invalid values. The sum must be equal to " + chosenNumber +
                                " and the values must be unequal. Enter again:");
                        JOptionPane.showMessageDialog(null, "Invalid values. The sum must be equal to " + chosenNumber +
                                " and the values must be unequal. Enter again:");
                    }
                } else {
                    System.out.println("Invalid number index or the chosen number is 2. Please enter a valid index:");
                    JOptionPane.showMessageDialog(null, "Invalid number index or the chosen number is 2. Please enter a valid index:");
                }
            } else {
                System.out.println("Invalid group index or the group is empty. Please enter a valid index:");
                JOptionPane.showMessageDialog(null, "Invalid group index or the group is empty. Please enter a valid index:");
            }
        }
    }

    private int[] minimax(ArrayList<ArrayList<Integer>> groups, int depth, int alpha, int beta, boolean isMaximizing) {
        int[] result = {-1, -1};

        if (depth == 0 || isGameEnd(groups) || !hasValidMove(groups)) {
            return result;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;

            for (int i = 0; i < groups.size(); i++) {
                ArrayList<Integer> group = groups.get(i);

                for (int j = 0; j < group.size(); j++) {
                    int chosenNumber = group.get(j);

                    if (chosenNumber > 1 && chosenNumber != 2) {
                        int value1 = 1;
                        int value2 = chosenNumber - 1;

                        group.remove(j);
                        group.add(value1);
                        group.add(value2);

                        int eval = minimax(groups, depth - 1, alpha, beta, false)[0];

                        group.remove(group.size() - 1);
                        group.remove(group.size() - 1);
                        group.add(j, chosenNumber);

                        if (eval > maxEval) {
                            maxEval = eval;
                            result[0] = i;
                            result[1] = j;
                        }

                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
        } else {
            int minEval = Integer.MAX_VALUE;

            for (int i = 0; i < groups.size(); i++) {
                ArrayList<Integer> group = groups.get(i);

                for (int j = 0; j < group.size(); j++) {
                    int chosenNumber = group.get(j);

                    if (chosenNumber > 1 && chosenNumber != 2) {
                        int value1 = 1;
                        int value2 = chosenNumber - 1;

                        group.remove(j);
                        group.add(value1);
                        group.add(value2);

                        int eval = minimax(groups, depth - 1, alpha, beta, true)[0];

                        group.remove(group.size() - 1);
                        group.remove(group.size() - 1);
                        group.add(j, chosenNumber);

                        if (eval < minEval) {
                            minEval = eval;
                            result[0] = i;
                            result[1] = j;
                        }

                        beta = Math.min(beta, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static boolean hasValidMove(ArrayList<ArrayList<Integer>> groups) {
        for (ArrayList<Integer> group : groups) {
            for (int num : group) {
                if (num > 1 && num != 2) {
                    return true;
                }
            }
        }
        return false;
    }

    @FXML
    void divideClicked(ActionEvent event) {
        System.out.println("Current groups: " + groups);

        if (isGameEnd(groups)) {
            System.out.println("Player wins! The computer cannot divide further.");
            showResultDialog("Congratulations! You won!");
            return;
        }

        playerMove(groups);

        if (isGameEnd(groups)) {
            System.out.println("Player wins! The computer cannot divide further.");
            showResultDialog("Congratulations! You won!");

            return;
        }

        System.out.println("Computer's turn.");

        int[] computerMove = minimax(groups, getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        if (computerMove[0] == -1) {
            System.out.println("Computer cannot make a valid move. Player wins!");
            showResultDialog("Congratulations! You won!");
            return;
        }

        int groupIndex = computerMove[0];
        int numberIndex = computerMove[1];
        int chosenNumber = groups.get(groupIndex).get(numberIndex);
        int value1 = 1;
        int value2 = chosenNumber - 1;

        groups.get(groupIndex).remove(numberIndex);
        groups.get(groupIndex).add(value1);
        groups.get(groupIndex).add(value2);

        System.out.println("Current groups: " + groups);
        updateLabels();

        if (isGameEnd(groups)) {
            System.out.println("Computer wins! The player cannot divide further.");
            showResultDialog("Computer wins! Better luck next time.");
            return;
        }

        if (!hasValidMove(groups)) {
            System.out.println("Player cannot make a valid move. Computer wins!");
            showResultDialog("Computer wins! Better luck next time.");
            return;
        }
    }


    @FXML
    void playClicked(ActionEvent event) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Number Game!");
        System.out.println("Choose difficulty level: 1 (Easy), 2 (Medium), 3 (Hard):");
        int difficultyLevel = getLevel();
        System.out.println(getLevel());
        switch (difficultyLevel) {
            case 1:
                setDepth(EASY_DEPTH);
                break;
            case 2:
                setDepth(MEDIUM_DEPTH);
                break;
            case 3:
                setDepth(HARD_DEPTH);
                break;
            default:
                System.out.println("Invalid difficulty level. Using default (Medium).");
                depth = MEDIUM_DEPTH;
        }
        setGroups(initializeGroups(scanner));
    }

    @FXML
    void eClicked() {
        m.setStyle("-fx-background-color: #a5a173;");
        m.setCursor(Cursor.DEFAULT);
        h.setStyle("-fx-background-color: #a5a173;");
        h.setCursor(Cursor.DEFAULT);
        setLevel(1);
    }

    @FXML
    void mClicked() {
        e.setStyle("-fx-background-color: #a5a173;");
        e.setCursor(Cursor.DEFAULT);
        h.setStyle("-fx-background-color: #a5a173;");
        h.setCursor(Cursor.DEFAULT);
        setLevel(2);
    }

    @FXML
    void hClicked() {
        e.setStyle("-fx-background-color: #a5a173;");
        e.setCursor(Cursor.DEFAULT);
        m.setStyle("-fx-background-color: #a5a173;");
        m.setCursor(Cursor.DEFAULT);
        setLevel(3);
    }

    private void updateLabels() {
        Platform.runLater(() -> {
            updateLabelsForGroup(groups.get(0), "lA", 1);
            updateLabelsForGroup(groups.get(1), "lB", 2);
            updateLabelsForGroup(groups.get(2), "lc", 3);
        });
    }

    private void updateLabelsForGroup(ArrayList<Integer> group, String labelPrefix, int groupNumber) {
        for (int i = 0; i < group.size(); i++) {
            String labelName = labelPrefix + (i + 1);
            String labelText = String.valueOf(group.get(i));
            updateLabel(labelName, labelText);
        }
    }

    private void updateLabel(String labelName, String labelText) {
        switch (labelName) {
            case "A1l":
                lA1.setText(labelText);
                break;
            case "lA2":
                lA2.setText(labelText);
                break;
            case "lA3":
                lA3.setText(labelText);
                break;
            case "lA4":
                lA4.setText(labelText);
                break;
            case "lA5":
                lA5.setText(labelText);
                break;
            case "lA6":
                lA6.setText(labelText);
                break;
            case "lA7":
                lA7.setText(labelText);
                break;
            case "lA8":
                lA8.setText(labelText);
                break;
            case "lA9":
                lA9.setText(labelText);
                break;
            case "lA10":
                lA10.setText(labelText);
                break;
            case "lA11":
                lA11.setText(labelText);
                break;
            case "lA13":
                lA13.setText(labelText);
                break;
            case "lA14":
                lA14.setText(labelText);
                break;

            case "lB1":
                lB1.setText(labelText);
                break;
            case "lB2":
                lB2.setText(labelText);
                break;
            case "lB3":
                lB3.setText(labelText);
                break;
            case "lB4":
                lB4.setText(labelText);
                break;
            case "lB5":
                lB5.setText(labelText);
                break;
            case "lB6":
                lB6.setText(labelText);
                break;
            case "lB7":
                lB7.setText(labelText);
                break;
            case "lB8":
                lB8.setText(labelText);
                break;
            case "lB9":
                lB9.setText(labelText);
                break;
            case "lB10":
                lB10.setText(labelText);
                break;
            case "lB11":
                lB11.setText(labelText);
                break;
            case "lB12":
                lB12.setText(labelText);
                break;
            case "lB13":
                lB13.setText(labelText);
                break;
            case "lB14":
                lB14.setText(labelText);
                break;

            case "lc1":
                lc1.setText(labelText);
                break;
            case "lc2":
                lc2.setText(labelText);
                break;
            case "lc3":
                lc3.setText(labelText);
                break;
            case "lc4":
                lc4.setText(labelText);
                break;
            case "lc5":
                lc5.setText(labelText);
                break;
            case "lc6":
                lc6.setText(labelText);
                break;
            case "lc7":
                lc7.setText(labelText);
                break;
            case "lc8":
                lc8.setText(labelText);
                break;
            case "lc9":
                lc9.setText(labelText);
                break;
            case "lc10":
                lc10.setText(labelText);
                break;
            case "lc11":
                lc11.setText(labelText);
                break;
            case "lc12":
                lc12.setText(labelText);
                break;
            case "lc13":
                lc13.setText(labelText);
                break;
            case "lc14":
                lc14.setText(labelText);
                break;

            // Add more cases for each label in your GUI
        }
    }


    private String getLabelText(int groupIndex, int numberIndex) {
        return String.valueOf(groups.get(groupIndex).get(numberIndex));
    }
}