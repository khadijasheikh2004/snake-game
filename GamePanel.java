import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private String playerName;
    private HighScoreManager highScoreManager;

    public void promptForName() {
        playerName = JOptionPane.showInputDialog("Enter your name:");
    }

    private static final int SCREEN_WIDTH = 1300;
    private static final int SCREEN_HEIGHT = 750;
    private static final int UNIT_SIZE = 25;
    private static final int DELAY = 170;

    private LinkedList<Point> snake;
    private Queue<Point> snakeMovement;
    private LinkedList<Point> obstacles;

    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    public GamePanel() {
        random = new Random();
        highScoreManager = new HighScoreManager();
        initializeUI();
        initializeSnake();
        initializeObstacles();
        startGame();
    }

    private void initializeUI() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }

    private void initializeSnake() {
        snake = new LinkedList<>();
        for (int i = 0; i < bodyParts; i++) {
            snake.add(new Point(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
        }
        snakeMovement = new LinkedList<>();
    }

    private void initializeObstacles() {
        obstacles = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            addObstacle();
        }
    }

    public void startGame() {
        promptForName();
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        highScoreManager.addHighScore(playerName, 0);
    }

    private void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void addObstacle() {
        int obstacleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        int obstacleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        Point obstacle = new Point(obstacleX, obstacleY);
        while (snake.contains(obstacle) || obstacle.equals(new Point(appleX, appleY)) || obstacles.contains(obstacle)) {
            obstacleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            obstacleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            obstacle = new Point(obstacleX, obstacleY);
        }

        obstacles.add(obstacle);
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = getNewHeadPosition(head);

        if (checkCollision(newHead)) {
            handleCollision();
            return;
        }

        if (newHead.equals(new Point(appleX, appleY))) {
            handleAppleCollision();
            addObstacle();
        }

        snake.addFirst(newHead);

        if (snake.size() > bodyParts) {
            snake.removeLast();
        }
    }

    private Point getNewHeadPosition(Point head) {
        switch (direction) {
            case 'U':
                return new Point(head.x, head.y - UNIT_SIZE);
            case 'D':
                return new Point(head.x, head.y + UNIT_SIZE);
            case 'L':
                return new Point(head.x - UNIT_SIZE, head.y);
            case 'R':
                return new Point(head.x + UNIT_SIZE, head.y);
            default:
                return head;
        }
    }

    private boolean checkCollision(Point newHead) {
        return snake.contains(newHead) ||
                newHead.x < 0 || newHead.y < 0 ||
                newHead.x >= SCREEN_WIDTH || newHead.y >= SCREEN_HEIGHT ||
                obstacles.contains(newHead);
    }

    private void handleCollision() {
        running = false;
        timer.stop();
        highScoreManager.displayHighScores();
    }

    private void handleAppleCollision() {
        bodyParts++;
        applesEaten++;
        newApple();
        highScoreManager.addHighScore(playerName, applesEaten);
    }

    private void checkCollisions() {
        Point head = snake.getFirst();

        if (snake.subList(1, snake.size()).contains(head) ||
                head.x < 0 || head.x >= SCREEN_WIDTH || head.y < 0 || head.y >= SCREEN_HEIGHT) {
            handleCollision();
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            drawGrid(g);
            drawObstacles(g);
            drawApple(g);
            drawSnake(g);
            drawScore(g);
        } else {
            System.out.println("Game Over");
            gameOver(g);
        }
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawObstacles(Graphics g) {
        g.setColor(Color.yellow);
        for (Point obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawApple(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                g.setColor(Color.green);
            } else {
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            }
            Point point = snake.get(i);
            g.fillRect(point.x, point.y, UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.blue);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}