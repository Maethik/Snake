package A00_Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {

    public static void main(String[] args) {
        new SnakeGame();
    }

    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Définir la taille de la fenêtre pour correspondre à la taille du jeu
        setSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

        // Ajouter le menu principal
        showMainMenu();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showMainMenu() {
        // Créer un panel pour le menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Bouton pour démarrer le jeu
        JButton startButton = new JButton("Commencer le jeu");
        startButton.addActionListener(e -> {
            getContentPane().removeAll(); // Supprimer le menu
            add(new GamePanel()); // Ajouter le jeu
            revalidate();
            repaint();
        });

        // Bouton pour quitter le jeu
        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));

        // Ajouter les boutons au panel
        menuPanel.add(startButton);
        menuPanel.add(quitButton);

        // Ajouter le menu au frame
        add(menuPanel);
    }

    // Constantes pour la taille de la grille et des tuiles
    private static final int TILE_SIZE = 25;
    private static final int GRID_WIDTH = 25; // Largeur en nombre de tuiles
    private static final int GRID_HEIGHT = 20; // Hauteur en nombre de tuiles
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 25;
    private static final int GRID_WIDTH = 25; // Largeur en nombre de tuiles
    private static final int GRID_HEIGHT = 20; // Hauteur en nombre de tuiles
    private static final int GAME_SPEED = 150;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private int dx = 1, dy = 0;
    private int score = 0;
    private boolean gameOver = false;
    private final Timer timer = new Timer(GAME_SPEED, this);

    public GamePanel() {
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Initialisation du serpent
        snake.add(new Point(5, 5));
        spawnFood();

        timer.start();
    }

    private void spawnFood() {
        Random rand = new Random();
        while (true) {
            // La nourriture doit être dans les limites de la grille
            food = new Point(rand.nextInt(GRID_WIDTH), rand.nextInt(GRID_HEIGHT));
            // Vérifier que la nourriture n'est pas sur le serpent
            if (!snake.contains(food)) break;
        }
    }

    private void move() {
        if (gameOver) return;

        // Nouvelle tête du serpent
        Point head = snake.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);

        // Collision avec les murs
        if (newHead.x < 0 || newHead.x >= GRID_WIDTH ||
                newHead.y < 0 || newHead.y >= GRID_HEIGHT) {
            gameOver = true;
            return;
        }

        // Collision avec le corps
        if (snake.contains(newHead)) {
            gameOver = true;
            return;
        }

        snake.add(0, newHead);

        // Vérifier si le serpent mange la nourriture
        if (newHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            // Retirer la queue si pas de nourriture mangée
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            timer.stop();
            int choice = JOptionPane.showConfirmDialog(this,
                    "Game Over! Score: " + score + "\nVoulez-vous rejouer?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        spawnFood();
        dx = 1;
        dy = 0;
        score = 0;
        gameOver = false;
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessiner le serpent
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            g.setColor(i == 0 ? Color.GREEN : Color.YELLOW);
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        // Dessiner la nourriture
        g.setColor(Color.RED);
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);

        // Afficher le score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Changer la direction selon la touche pressée
        if (key == KeyEvent.VK_LEFT && dx != 1) {
            dx = -1;
            dy = 0;
        } else if (key == KeyEvent.VK_RIGHT && dx != -1) {
            dx = 1;
            dy = 0;
        } else if (key == KeyEvent.VK_UP && dy != 1) {
            dx = 0;
            dy = -1;
        } else if (key == KeyEvent.VK_DOWN && dy != -1) {
            dx = 0;
            dy = 1;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}