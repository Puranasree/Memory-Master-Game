import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;

class MemoryMasterApp {

    public static void main(String[] args) {
        // Modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Memory Master");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 550);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new LoginPanel(frame));
            frame.setVisible(true);
        });
    }

    // --- UserManager: Handles login/signup and current user ---
    static class UserManager {
        private static final Map<String, String> users = new HashMap<>();
        private static String currentUser = null;
        public static boolean signup(String username, String password) {
            if (users.containsKey(username) || username.isEmpty() || password.isEmpty())
                return false;
            users.put(username, password);
            return true;
        }
        public static boolean login(String username, String password) {
            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUser = username;
                return true;
            }
            return false;
        }
        public static String getCurrentUser() {
            return currentUser;
        }
        public static void logout() {
            currentUser = null;
        }
    }

    // --- ScoreManager: Manages per-user scores ---
    static class ScoreManager {
        private static final Map<String, List<Integer>> userScores = new HashMap<>();
        public static void addScore(String username, int score) {
            userScores.computeIfAbsent(username, k -> new ArrayList<>()).add(score);
        }
        public static List<Integer> getScores(String username) {
            return userScores.getOrDefault(username, new ArrayList<>());
        }
    }

    // --- Utility for consistent button style ---
    static class UIUtils {
        public static JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(41, 128, 185));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(70, 130, 180));
                }
            });
            return btn;
        }
        public static JLabel createTitleLabel(String text) {
            JLabel lbl = new JLabel(text, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
            lbl.setForeground(new Color(44, 62, 80));
            lbl.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));
            return lbl;
        }
        public static JLabel createSubtitleLabel(String text) {
            JLabel lbl = new JLabel(text, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lbl.setForeground(new Color(52, 73, 94));
            return lbl;
        }
    }

    // --- LoginPanel: Login and navigation to signup ---
    static class LoginPanel extends JPanel {
        public LoginPanel(JFrame frame) {
            setBackground(new Color(245, 247, 250));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel title = UIUtils.createTitleLabel("Memory Master");
            JLabel userLabel = UIUtils.createSubtitleLabel("Username:");
            JTextField userField = new JTextField(15);
            JLabel passLabel = UIUtils.createSubtitleLabel("Password:");
            JPasswordField passField = new JPasswordField(15);
            JButton loginBtn = UIUtils.createButton("Login");
            JButton signupBtn = UIUtils.createButton("Sign Up");

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(title, gbc);
            gbc.gridy++;
            add(userLabel, gbc);
            gbc.gridy++;
            add(userField, gbc);
            gbc.gridy++;
            add(passLabel, gbc);
            gbc.gridy++;
            add(passField, gbc);
            gbc.gridy++; gbc.gridwidth = 1;
            add(loginBtn, gbc);
            gbc.gridx = 1;
            add(signupBtn, gbc);

            loginBtn.addActionListener((ActionEvent e) -> {
                if (UserManager.login(userField.getText().trim(), new String(passField.getPassword()))) {
                    frame.setContentPane(new MainMenuPanel(frame));
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            signupBtn.addActionListener((ActionEvent e) -> {
                frame.setContentPane(new SignupPanel(frame));
                frame.revalidate();
            });
        }
    }

    // --- SignupPanel: Signup and navigation to login ---
    static class SignupPanel extends JPanel {
        public SignupPanel(JFrame frame) {
            setBackground(new Color(245, 247, 250));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel title = UIUtils.createTitleLabel("Sign Up");
            JLabel userLabel = UIUtils.createSubtitleLabel("Choose Username:");
            JTextField userField = new JTextField(15);
            JLabel passLabel = UIUtils.createSubtitleLabel("Choose Password:");
            JPasswordField passField = new JPasswordField(15);
            JButton signupBtn = UIUtils.createButton("Sign Up");
            JButton backBtn = UIUtils.createButton("Back to Login");

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(title, gbc);
            gbc.gridy++;
            add(userLabel, gbc);
            gbc.gridy++;
            add(userField, gbc);
            gbc.gridy++;
            add(passLabel, gbc);
            gbc.gridy++;
            add(passField, gbc);
            gbc.gridy++; gbc.gridwidth = 1;
            add(signupBtn, gbc);
            gbc.gridx = 1;
            add(backBtn, gbc);

            signupBtn.addActionListener((ActionEvent e) -> {
                if (UserManager.signup(userField.getText().trim(), new String(passField.getPassword()))) {
                    JOptionPane.showMessageDialog(this, "Signup successful! Please log in.");
                    frame.setContentPane(new LoginPanel(frame));
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed! Username may exist or fields are empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            backBtn.addActionListener((ActionEvent e) -> {
                frame.setContentPane(new LoginPanel(frame));
                frame.revalidate();
            });
        }
    }

    // --- MainMenuPanel: Main menu after login ---
    static class MainMenuPanel extends JPanel {
        public MainMenuPanel(JFrame frame) {
            setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(0, 30));

            JLabel welcomeLabel = UIUtils.createTitleLabel("Welcome, " + UserManager.getCurrentUser() + "!");
            add(welcomeLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

            JButton playBtn = createMenuButton("Play Game", "▶");
            JButton scoreBtn = createMenuButton("My Scores", "★");
            JButton logoutBtn = createMenuButton("Logout", "⎋");

            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(playBtn);
            buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
            buttonPanel.add(scoreBtn);
            buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
            buttonPanel.add(logoutBtn);
            buttonPanel.add(Box.createHorizontalGlue());

            add(buttonPanel, BorderLayout.CENTER);

            playBtn.addActionListener(e -> {
                frame.setContentPane(new GamePanel(frame));
                frame.revalidate();
            });
            scoreBtn.addActionListener(e -> {
                frame.setContentPane(new ScorePanel(frame));
                frame.revalidate();
            });
            logoutBtn.addActionListener(e -> {
                UserManager.logout();
                frame.setContentPane(new LoginPanel(frame));
                frame.revalidate();
            });
        }

        private JButton createMenuButton(String text, String iconText) {
            JButton btn = new JButton(iconText + "  " + text);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(52, 152, 219));
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true),
                    BorderFactory.createEmptyBorder(40, 30, 40, 30)
            ));

            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(41, 128, 185));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(52, 152, 219));
                }
            });

            return btn;
        }
    }

    // --- GamePanel: Level-based memory game ---
    static class GamePanel extends JPanel {
        private int level = 1;
        private int score = 0;
        private JFrame frame;
        private LevelGamePanel levelPanel;

        public GamePanel(JFrame frame) {
            this.frame = frame;
            setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(0, 10));
            startLevel();
        }

        private void startLevel() {
            removeAll();
            JLabel title = UIUtils.createTitleLabel("Memory Game - Level " + level);
            add(title, BorderLayout.NORTH);

            levelPanel = new LevelGamePanel(level, this::onLevelComplete);
            add(levelPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setOpaque(false);
            JButton backBtn = UIUtils.createButton("Back");
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);

            backBtn.addActionListener((ActionEvent e) -> {
                frame.setContentPane(new MainMenuPanel(frame));
                frame.revalidate();
            });

            revalidate();
            repaint();
        }

        private void onLevelComplete(int levelScore, int attempts) {
            score += levelScore;
            String username = UserManager.getCurrentUser();
            ScoreManager.addScore(username, levelScore);

            JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
            JLabel message = new JLabel(username + ", you have successfully completed level " + level + "!", SwingConstants.CENTER);
            message.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel scoreLabel = new JLabel("Your score: " + score + "   (Level score: " + levelScore + ")", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 15));
            JButton nextBtn = UIUtils.createButton("Play Next Level");

            panel.add(message);
            panel.add(scoreLabel);
            panel.add(nextBtn);

            JDialog dialog = new JDialog(frame, "Level Complete", true);
            dialog.getContentPane().add(panel);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(frame);

            nextBtn.addActionListener(e -> {
                dialog.dispose();
                level++;
                startLevel();
            });

            dialog.setVisible(true);
        }

        // --- LevelGamePanel: A single level of the memory game ---
        static class LevelGamePanel extends JPanel {
            private int gridSize;
            private JButton[][] buttons;
            private Integer[][] values;
            private boolean[][] matched;
            private JButton firstBtn, secondBtn;
            private int firstRow, firstCol, secondRow, secondCol;
            private int matchesFound = 0;
            private int attempts = 0;
            private LevelCompleteCallback callback;

            public LevelGamePanel(int level, LevelCompleteCallback callback) {
                this.callback = callback;
                this.gridSize = 2 + level; // Level 1: 3x3, Level 2: 4x4, etc.
                if (gridSize % 2 != 0) gridSize++; // Ensure even grid for pairs
                setLayout(new GridLayout(gridSize, gridSize, 8, 8));
                setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                setBackground(new Color(236, 240, 241));
                initBoard();
            }

            private void initBoard() {
                buttons = new JButton[gridSize][gridSize];
                values = new Integer[gridSize][gridSize];
                matched = new boolean[gridSize][gridSize];

                // Create pairs
                List<Integer> nums = new ArrayList<>();
                int pairs = (gridSize * gridSize) / 2;
                for (int i = 1; i <= pairs; i++) {
                    nums.add(i);
                    nums.add(i);
                }
                Collections.shuffle(nums);

                // Assign to grid
                Iterator<Integer> it = nums.iterator();
                for (int r = 0; r < gridSize; r++) {
                    for (int c = 0; c < gridSize; c++) {
                        values[r][c] = it.next();
                        JButton btn = new JButton();
                        btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
                        btn.setFocusPainted(false);
                        btn.setBackground(new Color(189, 195, 199));
                        btn.setForeground(new Color(44, 62, 80));
                        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btn.setBorder(BorderFactory.createLineBorder(new Color(149, 165, 166), 2, true));
                        int row = r, col = c;
                        btn.addActionListener(e -> handleClick(row, col));
                        buttons[r][c] = btn;
                        add(btn);
                    }
                }
            }

            private void handleClick(int r, int c) {
                if (matched[r][c] || buttons[r][c].getText().length() > 0) return;
                buttons[r][c].setText(String.valueOf(values[r][c]));
                buttons[r][c].setEnabled(false);
                buttons[r][c].setBackground(new Color(241, 196, 15));

                if (firstBtn == null) {
                    firstBtn = buttons[r][c];
                    firstRow = r;
                    firstCol = c;
                } else if (secondBtn == null && (r != firstRow || c != firstCol)) {
                    secondBtn = buttons[r][c];
                    secondRow = r;
                    secondCol = c;
                    attempts++;
                    new javax.swing.Timer(700, e -> checkMatch()).start();
                }
            }

            private void checkMatch() {
                if (values[firstRow][firstCol].equals(values[secondRow][secondCol])) {
                    matched[firstRow][firstCol] = true;
                    matched[secondRow][secondCol] = true;
                    firstBtn.setBackground(new Color(46, 204, 113));
                    secondBtn.setBackground(new Color(46, 204, 113));
                    matchesFound++;
                    if (matchesFound == (gridSize * gridSize) / 2) {
                        int levelScore = Math.max(100 - attempts * 2, 10);
                        SwingUtilities.invokeLater(() -> callback.onLevelComplete(levelScore, attempts));
                    }
                } else {
                    firstBtn.setText("");
                    secondBtn.setText("");
                    firstBtn.setEnabled(true);
                    secondBtn.setEnabled(true);
                    firstBtn.setBackground(new Color(189, 195, 199));
                    secondBtn.setBackground(new Color(189, 195, 199));
                }
                firstBtn = null;
                secondBtn = null;
            }

            interface LevelCompleteCallback {
                void onLevelComplete(int levelScore, int attempts);
            }
        }
    }

    // --- ScorePanel: Shows scores for the logged-in user ---
    static class ScorePanel extends JPanel {
        public ScorePanel(JFrame frame) {
            setBackground(new Color(245, 247, 250));
            setLayout(new BorderLayout(10, 10));
            String username = UserManager.getCurrentUser();
            JLabel title = UIUtils.createTitleLabel("Scores for " + username);

            // Score List
            JTextArea scoreArea = new JTextArea();
            scoreArea.setEditable(false);
            scoreArea.setFont(new Font("Consolas", Font.PLAIN, 16));
            scoreArea.setBackground(new Color(223, 230, 233));
            List<Integer> scores = ScoreManager.getScores(username);
            if (scores.isEmpty()) {
                scoreArea.setText("No scores yet. Play the game!");
            } else {
                int i = 1;
                for (Integer score : scores) {
                    scoreArea.append("Game " + (i++) + ": " + score + "\n");
                }
            }

            JScrollPane scroll = new JScrollPane(scoreArea);
            scroll.setPreferredSize(new Dimension(200, 300));

            // Navigation
            JButton backBtn = UIUtils.createButton("Back");
            backBtn.addActionListener(e -> {
                frame.setContentPane(new MainMenuPanel(frame));
                frame.revalidate();
            });

            add(title, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
            add(backBtn, BorderLayout.SOUTH);
        }
    }
}
