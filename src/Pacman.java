import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

public class Pacman extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        Image previousImage;
        int initialX;
        int initialY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;
        boolean isScared = false;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.previousImage = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.initialX = x;
            this.initialY = y;
        }

        void updateDirection (char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall: walls) {
                if (collition(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
            if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.initialX;
            this.y = this.initialY;
        }

        void setScaredImage() {
            this.image = scaredGhost;
        }

        void setNormalImage() {
            this.image = previousImage;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
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
    private Image scaredGhost;
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Block powerFood;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
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
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("./powerFood.png")).getImage();
        scaredGhost = new ImageIcon(getClass().getResource("./scaredGhost.png")).getImage();
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
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                //new feature: power food
                if (tileMapChar == 'F') {
                    powerFood = new Block(powerFoodImage,x, y, tileSize, tileSize);
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
            g.drawString("Game Over: " + score, tileSize/2, tileSize/2);
        } else {
            g.drawString("x" + lives + " Score: " + score, tileSize/2, tileSize/2);
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
        if (gameOver) {
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
