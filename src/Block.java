import java.awt.*;

public class Block {
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
    Pacman pacman;

    Block(Image image, int x, int y, int width, int height, Pacman pacman) {
        this.image = image;
        this.previousImage = image;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.initialX = x;
        this.initialY = y;
        this.pacman = pacman;
    }

    void updateDirection (char direction) {
        char prevDirection = this.direction;
        this.direction = direction;
        updateVelocity();
        this.x += this.velocityX;
        this.y += this.velocityY;
        for (Block wall: pacman.walls) {
            if (pacman.collition(this, wall)) {
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
            this.velocityY = -pacman.tileSize/4;
        }
        if (this.direction == 'D') {
            this.velocityX = 0;
            this.velocityY = pacman.tileSize/4;
        }
        if (this.direction == 'R') {
            this.velocityX = pacman.tileSize/4;
            this.velocityY = 0;
        }
        if (this.direction == 'L') {
            this.velocityX = -pacman.tileSize/4;
            this.velocityY = 0;
        }
    }

    void reset() {
        this.x = this.initialX;
        this.y = this.initialY;
    }

    void setScaredImage() {
        this.image = pacman.scaredGhost;
    }

    void setNormalImage() {
        this.image = previousImage;
    }
}
