import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    Random random = new Random();
    final int boardWidth;
    final int boardHeight;
    //Images
    Image backgroundImg;
    Image birdImg;
    Image bottomPipeImg;
    Image topPipeImg;

    class Bird {
        int x = boardWidth/8;
        int y = boardHeight/2;
        int width = 34;
        int height = 24;

        boolean move = false;
        Image image;

        int velocity = 0;
        int gravity = 1;

        double score = 0;
        Rectangle hitBox = new Rectangle(x, y, width, height);

        Bird(Image image) {
            this.image = image;
        }

        void move() {
            velocity += gravity;
            y += velocity;
            y = Math.max(y, 0);
            hitBox.y = y;
        }

        void reset() {
            x = boardWidth/8;
            y = boardHeight/2;
            score = 0;
            velocity = 0;
        }
    }

    class Pipe {
        int x = boardWidth;
        int y = 0;
        int width = 64;
        int height = 512;

        Image image;
        int velocity = -4;

        Rectangle hitBox = new Rectangle(x, y, width, height);

        boolean passed = false;
        Pipe(Image image) {
            this.image = image;
        }

        void move() {
            x += velocity;
            hitBox.x = x;
            hitBox.y = y;
        }

        public void randomY() {
            y = (int)(y - height/4 - Math.random()*(height/2));
        }

    }
    Bird bird;
    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer renderPipes;
    boolean gameOver = false;
    public FlappyBird(int width, int height) {
        boardWidth = width;
        boardHeight = height;

        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);
        //Loading Images
        backgroundImg = new ImageIcon(getClass().getResource("./background.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottomPipe.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./topPipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //render Pipes

        renderPipes = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        renderPipes.start();
        //Game Loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {
        //top pipe
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.randomY();
        pipes.add(topPipe);

        //bottom pipe
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + topPipe.height + boardHeight/4;
        pipes.add(bottomPipe);
    }

    //Check for collision of bird with pipes
    public void checkCollision() {
        if(bird.y > boardHeight) gameOver = true;
        for (Pipe pipe: pipes) {
            if(pipe.hitBox.intersects(bird.hitBox )) gameOver = true;
        }
    }
    //Add to birds score
    public void checkAddScore() {
        for(Pipe pipe : pipes) {
            if(!pipe.passed && pipe.x < bird.x) {
                pipe.passed = true;
                bird.score += 0.5;
            }
        }
    }

   //drawing environment
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0,boardWidth, boardHeight, null);
        g.drawImage(birdImg, bird.x , bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }


        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver) {
            g.drawString("GAME OVER:" + String.valueOf((int)bird.score), 10, 35);
        } else {
            g.drawString(String.valueOf((int)bird.score), 10, 35);
        }
    }
    //Game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        for (Pipe pipe : pipes) {
            pipe.move();
        }

        if(bird.move) bird.move();

        checkCollision();
        checkAddScore();

        repaint();
        if(gameOver) {
            gameLoop.stop();
            renderPipes.stop();
        }
    }

    //Inputs
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(!bird.move) bird.move = true;
            bird.velocity += -12;
            if(gameOver) {
                pipes.clear();
                bird.reset();
                gameOver = false;
                gameLoop.start();
                renderPipes.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
