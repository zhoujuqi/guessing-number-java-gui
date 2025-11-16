package com.example.myapp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuessingNumberGUI {

    // Game Constants
    private static final int MAX_NUMBER = 100;
    private static final int MIN_NUMBER = 1;

    // UI Constants
    private static final Font FONT_TITLE = new Font("微软雅黑", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font FONT_INPUT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("微软雅黑", Font.BOLD, 14);
    private static final Font FONT_MESSAGE = new Font("微软雅黑", Font.PLAIN, 16);
    private static final Font FONT_ATTEMPTS = new Font("微软雅黑", Font.ITALIC, 12);
    private static final Font FONT_HISTORY = new Font("微软雅黑", Font.PLAIN, 12);

    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_GUESS_BUTTON = new Color(60, 179, 113);
    private static final Color COLOR_NEW_GAME_BUTTON = new Color(70, 130, 180);
    private static final Color COLOR_TEXT_SECONDARY = Color.GRAY;
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_MSG_WARNING = Color.ORANGE.darker();
    private static final Color COLOR_MSG_LOW = new Color(255, 165, 0); // Orange
    private static final Color COLOR_MSG_HIGH = new Color(220, 20, 60); // Crimson
    private static final Color COLOR_MSG_SUCCESS = new Color(34, 139, 34); // ForestGreen
    private static final Color COLOR_MSG_ERROR = Color.RED;
    private static final Color COLOR_MSG_DEFAULT = Color.BLACK;

    // UI Components
    private JFrame frame;
    private JTextField guessField;
    private JLabel messageLabel;
    private JLabel attemptsLabel;
    private JButton guessButton;
    private JButton newGameButton;
    private JTextArea historyArea;

    // Game State
    private int secretNumber;
    private int attempts;
    private List<String> guessHistory;

    public GuessingNumberGUI() {
        initUI();
        startNewGame();
    }

    private void initUI() {
        setNimbusLookAndFeel();

        frame = new JFrame("猜数字游戏");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(450, 350);

        JPanel mainPanel = createMainPanel();
        frame.add(mainPanel, BorderLayout.CENTER);

        setupActionListeners();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // Log error if L&F setting fails
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(COLOR_BACKGROUND);

        mainPanel.add(createTitleLabel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(createInputPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createStatusPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createHistoryScrollPane());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createNewGameButton());

        return mainPanel;
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("猜一个1-100之间的数字");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titleLabel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputPanel.setOpaque(false);

        JLabel guessLabel = new JLabel("你的猜测:");
        guessLabel.setFont(FONT_LABEL);

        guessField = new JTextField(10);
        guessField.setFont(FONT_INPUT);

        guessButton = new JButton("猜!");
        guessButton.setFont(FONT_BUTTON);
        guessButton.setBackground(COLOR_GUESS_BUTTON);
        guessButton.setForeground(COLOR_WHITE);

        inputPanel.add(guessLabel);
        inputPanel.add(guessField);
        inputPanel.add(guessButton);
        return inputPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(FONT_MESSAGE);
        statusPanel.add(messageLabel);

        attemptsLabel = new JLabel(" ", SwingConstants.CENTER);
        attemptsLabel.setFont(FONT_ATTEMPTS);
        attemptsLabel.setForeground(COLOR_TEXT_SECONDARY);
        statusPanel.add(attemptsLabel);
        return statusPanel;
    }

    private JScrollPane createHistoryScrollPane() {
        guessHistory = new ArrayList<>();
        historyArea = new JTextArea();
        historyArea.setFont(FONT_HISTORY);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JScrollPane historyScrollPane = new JScrollPane(historyArea);
        historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        historyScrollPane.setPreferredSize(new Dimension(150, 80));
        return historyScrollPane;
    }

    private JButton createNewGameButton() {
        newGameButton = new JButton("开始新游戏");
        newGameButton.setFont(FONT_BUTTON);
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setBackground(COLOR_NEW_GAME_BUTTON);
        newGameButton.setForeground(COLOR_WHITE);
        newGameButton.setVisible(false);
        return newGameButton;
    }

    private void setupActionListeners() {
        guessButton.addActionListener(e -> checkGuess());
        guessField.addActionListener(e -> checkGuess()); // Allow pressing Enter
        newGameButton.addActionListener(e -> startNewGame());
    }

    private void startNewGame() {
        Random rand = new Random();
        secretNumber = rand.nextInt(MAX_NUMBER) + MIN_NUMBER;
        attempts = 0;
        guessHistory.clear();
        updateMessage("游戏开始！请输入你的猜测。", COLOR_MSG_DEFAULT);
        attemptsLabel.setText(" ");
        historyArea.setText("");
        guessField.setText("");
        guessField.setEditable(true);
        guessButton.setEnabled(true);
        newGameButton.setVisible(false);
        frame.getRootPane().setDefaultButton(guessButton);
    }

    private void checkGuess() {
        resetInputFocus(); // Reset focus at the beginning

        // 1. Parse Input
        String guessText = guessField.getText();
        int guess;
        try {
            guess = Integer.parseInt(guessText);
        } catch (NumberFormatException e) {
            updateMessage("无效输入，请输入一个数字。", COLOR_MSG_ERROR);
            return;
        }

        // 2. Validate Input
        if (guess < MIN_NUMBER || guess > MAX_NUMBER) {
            updateMessage("请输入1-100之间的数字!", COLOR_MSG_WARNING);
            return;
        }

        // 3. Process Valid Guess
        processValidGuess(guess);
    }

    private void processValidGuess(int guess) {
        attempts++;
        String result;

        if (guess < secretNumber) {
            updateMessage("太小了，再试试！", COLOR_MSG_LOW);
            result = " (太小)";
        } else if (guess > secretNumber) {
            updateMessage("太大了，再试试！", COLOR_MSG_HIGH);
            result = " (太大)";
        } else {
            updateMessage("恭喜你，猜对了！", COLOR_MSG_SUCCESS);
            result = " (正确!)";
            endGame();
        }

        attemptsLabel.setText("尝试次数: " + attempts);
        guessHistory.add(guess + result);
        updateHistory();
    }

    private void resetInputFocus() {
        guessField.requestFocus();
        guessField.selectAll();
    }

    private void endGame() {
        guessField.setEditable(false);
        guessButton.setEnabled(false);
        newGameButton.setVisible(true);
        frame.getRootPane().setDefaultButton(newGameButton);
    }

    private void updateHistory() {
        historyArea.setText(String.join("\n", guessHistory));
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private void updateMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuessingNumberGUI::new);
    }
}
