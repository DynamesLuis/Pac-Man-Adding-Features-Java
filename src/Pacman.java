import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class Pacman extends JPanel implements ActionListener, KeyListener {
    private int rowCount = 21;
    private int columnCount = 19;
    int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;
    private Image powerFoodImage;
    Image scaredGhost;
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Block powerFood;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int highScore = 0;
    int lives = 3;
    boolean gameOver = false;
    long powerStartTime;
    int powerDuration = 5000;
    boolean isPaused = false;
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X    F            X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    Pacman() {
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        wallImage = new ImageIcon(getClass().getResource("./images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./images/redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./images/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./images/pacmanDown.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./images/pacmanRight.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./images/pacmanLeft.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./images/powerFood.png")).getImage();
        scaredGhost = new ImageIcon(getClass().getResource("./images/scaredGhost.png")).getImage();
        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize, this);
                    walls.add(wall);
                }
                if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize, this);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize, this);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize, this);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize, this);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize, this);
                }
                if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4, this);
                    foods.add(food);
                }
                //new feature: power food
                if (tileMapChar == 'F') {
                    powerFood = new Block(powerFoodImage,x, y, tileSize, tileSize, this);
                }
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image,pacman.x, pacman.y, pacman.width, pacman.height, null);
        if (powerFood != null) {
            g.drawImage(powerFood.image, powerFood.x, powerFood.y, powerFood.width, powerFood.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.red);
            g.setFont(new Font("ink Free", Font.BOLD, 75));
            FontMetrics fm1 = g.getFontMetrics();
            String text = "GAME OVER";
            int x = (getWidth() - fm1.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g.drawString(text, x, y);
            g.setFont(new Font("ink Free", Font.BOLD, 50));
            FontMetrics fm2 = g.getFontMetrics();
            int x2 = (getWidth() - fm2.stringWidth(text)) / 2;
            int y2 = getHeight() / 2;
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, x2, y2 + 80);
            g.setFont(new Font("ink Free", Font.BOLD, 75));

            g.setFont(new Font("ink Free", Font.BOLD, 30));
            FontMetrics fm3 = g.getFontMetrics();
            String text3 = "Press Enter to restart!";
            int x3 = (getWidth() - fm3.stringWidth(text3)) / 2;
            int y3 = getHeight() / 2;
            g.setColor(Color.WHITE);
            g.drawString(text3, x3, y3 + 160);
        } else {
            g.drawString("x" + lives + " Score: " + score, tileSize/2, tileSize/2);
            g.drawString("High Score: " + highScore, tileSize/2, tileSize);
        }

        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g.getFontMetrics();
            String text = "GAME PAUSED";
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g.drawString(text, x, y);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        //new feature: wrap-tunnel
         if (pacman.x <= 0) {
             pacman.x = boardWidth - tileSize;
         } else if (pacman.x + tileSize >= boardWidth) {
            pacman.x = 0;
         }
        //
        //new feature: power food
        if (powerFood != null) {
            if (collition(pacman, powerFood)) {
                for (Block ghost: ghosts) {
                    powerStartTime = System.currentTimeMillis();
                    ghost.isScared = true;
                    ghost.setScaredImage();
                }
                powerFood = null;
            }
        }

        for (Block wall : walls) {
            if (collition(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
        Block ghostEaten = null;
        for (Block ghost : ghosts) {
            if (collition(ghost, pacman)) {
                if (ghost.isScared) {
                    ghostEaten = ghost;
                    continue;//error
                } else {
                    lives -= 1;
                }

                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collition(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                    break;
                }
            }
        }
        ghosts.remove(ghostEaten);

        Block foodEaten = null;
        for (Block food : foods) {
            if (collition(pacman, food)) {
                score += 10;
                checkNewHighScore();
                foodEaten = food;
            }
        }
        foods.remove(foodEaten);
        if (foods.isEmpty()) {
            gameOver = true;
        }
    }

    public boolean collition(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.width &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityY = 0;
        pacman.velocityX = 0;
        for (Block ghost: ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    public void setNotScare() {
        for (Block ghost: ghosts) {
            ghost.isScared = false;
            ghost.setNormalImage();
        }
    }

    public void checkNewHighScore() {
        if (highScore == 0) highScore = score;
        highScore = score > highScore ? score : highScore;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        move();
        repaint();
        if (gameOver) {

            gameLoop.stop();
        }
        if (isPaused) {
            gameLoop.stop();
        }

        long elapsed = System.currentTimeMillis() - powerStartTime;
        if (elapsed >= powerDuration) {
            setNotScare();
        }
    }
    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (gameOver &&
            keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            isPaused = !isPaused;
            if (!isPaused) {
                gameLoop.start();
            }
        }

        if (pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        if (pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        if (pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        if (pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }
    }
}
