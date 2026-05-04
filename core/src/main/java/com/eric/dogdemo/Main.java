package com.eric.dogdemo;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private Texture backgroundImage;
    private SpriteBatch batch;
    private Player dog;
    private Sprite background;
    private FitViewport viewport;
    private Ball ball; 
    private ScreenViewport uiViewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private int score = 0;
    private Sound bark;
    private Sound growl;
    private Sound whine;
    private Texture enemyTexture;
    private Texture fallingEnemyTexture;
    private Array<Enemy> enemies;
    private float spawnInterval;
    private float spawnTimer;
    private FlashText flashText;
    private int level = 1;
    private float fallingSpawnTimer = 0f;
    private float fallingSpawnInterval = 3f;
    private float ballSpeed = 2f; // whatever your current default is
    private Platform platform;
    private boolean isPaused = false;
    private boolean gameOver = false;

    @Override
    public void create() {
        backgroundImage = new Texture("background-clouds.jpg");
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 4.2f);
        dog = new Player(viewport);
        ball = new Ball(viewport);
        background = new Sprite(backgroundImage);
        dog.setSize(0.75f, 0.75f); // set in 1/2 unit block in 8x5 viewport
        dog.setPosition(1, 0); // default (0,0) bottom left!
        background.setPosition(0, 0);
        background.setSize(8, 4.2f); // leave room for bar at bottom
        uiViewport = new ScreenViewport();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        flashText = new FlashText(font);
        bark =  Gdx.audio.newSound(Gdx.files.internal("dog-bark.mp3"));
        growl = Gdx.audio.newSound(Gdx.files.internal("ball-drop.mp3"));
        whine = Gdx.audio.newSound(Gdx.files.internal("growl.mp3"));
        enemyTexture = new Texture("enemy.png");
        fallingEnemyTexture = new Texture("falling-enemy.png");
        spawnInterval = 2f; // Initial spawn interval in seconds
        spawnTimer = 0f;
        enemies = new Array<>();
        enemies.add(new RunningEnemy(enemyTexture, viewport));
        platform = new Platform(3f, 1.6f, 2f, 0.2f); // jump height-centered-ish in 8x5 world
    }

    @Override
    public void resize(int width, int height) {

        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render() { // Draw game world here. The render method is called continuously in a loop.
        // Exit game if ESC is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        // Toggle pause if P is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }
        // Pause screen
        if (isPaused) {
            ScreenUtils.clear(Color.BLACK);
            uiViewport.apply();
            batch.setProjectionMatrix(uiViewport.getCamera().combined);
            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "The game is on",
                uiViewport.getScreenWidth() / 2f - 100,
                uiViewport.getScreenHeight() / 2f + 120);
            font.getData().setScale(6f);
            font.draw(batch, "PAWS!",
                uiViewport.getScreenWidth() / 2f - 100,
                uiViewport.getScreenHeight() / 2f + 40);
            font.getData().setScale(2f);
            font.draw(batch, "Press P to resume",
                uiViewport.getScreenWidth() / 2f - 100,
                uiViewport.getScreenHeight() / 2f - 60);
            batch.end();
            return; // Skip update and render logic when paused
        }

        // Build world 
        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined); // world coords not UI
        batch.setProjectionMatrix(viewport.getCamera().combined);

        dog.input();
        dog.landOnPlatform(platform.getBounds());
        ScreenUtils.clear(Color.BLACK);

        batch.begin();
        background.draw(batch);
        dog.draw(batch);
        ball.draw(batch);
        platform.render(batch);
        batch.end();

        onCollision();
        checkLevel();
        
        dog.move();
        ball.update();


        /////////////////ENEMIES////////////////
        // Spawn running enemies at random intervals
        spawnTimer += Gdx.graphics.getDeltaTime();
        if (spawnTimer >= spawnInterval) {
            enemies.add(new RunningEnemy(enemyTexture, viewport));
            spawnTimer = 0f;
            spawnInterval = 2f + (float)(Math.random() * 3f); // 2–4 seconds between spawns
        }
        // Spawn falling enemies starting at level 2
        if (level >= 2) {
            fallingSpawnTimer += Gdx.graphics.getDeltaTime();
            if (fallingSpawnTimer >= fallingSpawnInterval) {
                enemies.add(new FallingEnemy(fallingEnemyTexture, viewport));
                fallingSpawnTimer = 0f;
                fallingSpawnInterval = 3f + (float)(Math.random() * 2f); // 3–5 seconds between spawns
            }
        }
        // --- Update ---
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        Iterator<Enemy> iteration = enemies.iterator();
        while (iteration.hasNext()) {
            if (iteration.next().isOffScreen()) {
                iteration.remove();
            }
        }
        // --- Render ---
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        batch.end();
        
        
        //////////SCORE BAR//////////
        // Draw UI (score bar)
        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, uiViewport.getScreenHeight() - 100, uiViewport.getScreenWidth(), 100);        
        shapeRenderer.end();

        // Draw score text on the bar
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(4.0f); // .05 is smallest
        font.draw(batch, "Score: " + score, 210, uiViewport.getScreenHeight() - 10);
        font.draw(batch, "Level: " + level, uiViewport.getScreenWidth() - 400, uiViewport.getScreenHeight() - 15);
        batch.end();

        // Draw flash text if active
        flashText.update(Gdx.graphics.getDeltaTime());
        batch.begin();
        font.getData().setScale(6f); // big and bold
        flashText.render(batch, uiViewport);
        batch.end();

        // Draw instructions bar across the bottom
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, uiViewport.getScreenWidth(), 100);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        float bottomY = 50;
        font.draw(batch, "SPACE: jump", 
            uiViewport.getScreenWidth() * 0.08f, bottomY);
        font.draw(batch, "LEFT/RIGHT: move", 
            uiViewport.getScreenWidth() * 0.30f, bottomY);
        font.draw(batch, "P: paws", 
            uiViewport.getScreenWidth() * 0.58f, bottomY);
        font.draw(batch, "ESC: exit", 
            uiViewport.getScreenWidth() * 0.78f, bottomY);
        batch.end();

        //////////GAME OVER//////////
        if (gameOver) {
            ScreenUtils.clear(Color.BLACK);
            uiViewport.apply();
            batch.setProjectionMatrix(uiViewport.getCamera().combined);
            batch.begin();
            font.setColor(Color.RED);
            font.getData().setScale(6f);
            font.draw(batch, "GAME OVER",
                uiViewport.getScreenWidth() / 2f - 150,
                uiViewport.getScreenHeight() / 2f + 40);
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Press ENTER to restart", 
                uiViewport.getScreenWidth() / 2f - 120,
                uiViewport.getScreenHeight() / 2f - 40);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                restart();
            }
        }
    } // end of render method

    public boolean onCollision() {
        Rectangle dogHitbox = dog.getBoundingRectangle();
        Rectangle ballHitbox = ball.getBoundingRectangle();
        Rectangle enemyHitbox;
        if (gameOver) return false;
        // if dog catches bone
        if (dogHitbox.overlaps(ballHitbox)) {
                score++; // Increment score on collision
                bark.play(); // Play bark sound
                flashText.show("CATCH! +1", 1f, Color.GREEN);
                System.out.println("Catch!"); // Collision detected, print message to console
                ball = new Ball(viewport); // Reset bone to a new random position at the top
                ball.setSpeed(ballSpeed); // Reset ball speed to current default
                return true;
        }
        // if dog misses bone
        if (ballHitbox.getY() < 0) {
            if (level > 1) {
                score -= 1; // Decrement score if ball goes below the screen after initial trial level
            }
            growl.play(); // Play growl sound
            flashText.show("MISSED! -1", 1f, Color.RED);
            System.out.println("Miss!"); // bone missed, print message to console
            ball = new Ball(viewport); // Reset bone to a new random position at the top
            ball.setSpeed(ballSpeed); // Reset ball speed to current default
            return true;
        }
        // if dog hits enemy
        for (Enemy enemy : enemies) {
            enemyHitbox = enemy.getBounds();
            if (dogHitbox.overlaps(enemyHitbox) && !dog.isSlowed()) {
                dog.applySlow(); // Apply slow effect to the player
                whine.play(); // Play whine sound
                flashText.show("SLOWED!", 3f, Color.YELLOW);
                System.out.println("Hit by enemy!"); // Collision detected, print message to console
                return true;
            }
        }    
        return false;
    } // end of onCollision method

    private void checkLevel() {
        if (score <= 0 && level >= 2 && !gameOver) {
            gameOver = true;
            bark.stop();
            growl.stop();
            whine.stop();
            return;
        }
        int newLevel = (score / 5) + 1; // Level up every 5 points
        if (newLevel > level) {
            level = newLevel;
            onLevelUp();
        }
    }

    public void onLevelUp() {
        flashText.show("LEVEL " + level + "!", 4f, Color.PINK);
        System.out.println("Level Up! Now at level " + level);
        if (level >= 2) {
            // Start spawning falling enemies in render method
            background.setColor(Color.BLUE); // Change background color as a visual cue for level up  
        }
        
        if (level >= 3) {
            ballSpeed += 1f; // Increase ball speed
            background.setColor(Color.YELLOW); // Change background color as a visual cue for level up
        }

        if (level >= 4) {
            ballSpeed += 1f; // Increase ball speed again
            dog.setSpeed(dog.getSpeed() + 1f); // Increase dog speed
            background.setColor(Color.RED); // Change background color again for next level up
        }

        if (level >= 5) {
            dog.setSpeed(dog.getSpeed() + 1f); // Increase dog speed again
            background.setColor(Color.PURPLE); // Final background color change for level 5
        }
    }

    private void restart() {
        score = 0;
        level = 1;
        gameOver = false;
        ballSpeed = 2f;
        spawnInterval = 2f;
        spawnTimer = 0f;
        fallingSpawnTimer = 0f;
        fallingSpawnInterval = 3f;
        enemies.clear();
        enemies.add(new RunningEnemy(enemyTexture, viewport));
        ball = new Ball(viewport);
        ball.setSpeed(ballSpeed);
        dog.setPosition(1, 0);
        dog.reset();
        flashText.show("NEW GAME!", 4f, Color.GREEN);
        background.setColor(Color.WHITE);
    } 

    @Override
    public void pause() {
        isPaused = true;
        bark.stop();
        growl.stop();
        whine.stop();
    }

    @Override
    public void resume() {
        isPaused = false;    
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        enemyTexture.dispose();
        batch.dispose();
    }
}

