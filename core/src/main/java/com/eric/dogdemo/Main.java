package com.eric.dogdemo;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
    private Array<Enemy> enemies;
    private float spawnInterval;
    private float spawnTimer;
    private FlashText flashText;

    @Override
    public void create() {
        backgroundImage = new Texture("background-clouds.jpg");
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        dog = new Player(viewport);
        ball = new Ball(viewport);
        background = new Sprite(backgroundImage);
        dog.setSize(0.75f, 0.75f); // set in 1/2 unit block in 8x5 viewport
        dog.setPosition(1, 0); // default (0,0) bottom left!
        background.setPosition(0, 0);
        background.setSize(8, 5); // fill the whole viewport
        uiViewport = new ScreenViewport();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        flashText = new FlashText(font);
        bark =  Gdx.audio.newSound(Gdx.files.internal("dog-bark.mp3"));
        growl = Gdx.audio.newSound(Gdx.files.internal("growl.mp3"));
        whine = Gdx.audio.newSound(Gdx.files.internal("dog-whine.mp3"));
        enemyTexture = new Texture("enemy.png");
        spawnInterval = 2f; // Initial spawn interval in seconds
        spawnTimer = 0f;
        enemies = new Array<>();
    }

    @Override
    public void resize(int width, int height) {

        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Draw game world here. The render method is called continuously in a loop.
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        dog.input();
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        background.draw(batch);
        dog.draw(batch);
        onCollision();
        ball.draw(batch);
        batch.end();
        dog.move();
        ball.update();

        /////////////////ENEMIES////////////////
        // Spawn enemies at random intervals
        spawnTimer += Gdx.graphics.getDeltaTime();
        if (spawnTimer >= spawnInterval) {
            enemies.add(new Enemy(enemyTexture, viewport));
            spawnTimer = 0f;
            spawnInterval = 2f + (float)(Math.random() * 3f); // 2–4 seconds between spawns
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
        viewport.apply();
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
        shapeRenderer.rect(0, uiViewport.getScreenHeight() - 40, uiViewport.getScreenWidth(), 40);        
        shapeRenderer.end();

        // Draw score text on the bar
        batch.begin();
        font.getData().setScale(2.0f); // .05 is smallest
        font.draw(batch, "Score: " + score, 20, uiViewport.getScreenHeight() - 10);
        batch.end();

        ///////////FLASH TEXT//////////
        flashText.update(Gdx.graphics.getDeltaTime());
        batch.begin();
        font.getData().setScale(8f); // big and bold
        flashText.render(batch, uiViewport);
        font.setColor(Color.WHITE); // reset color for score
        font.getData().setScale(2f); // reset for score text
        font.draw(batch, "Score: " + score, 20, uiViewport.getScreenHeight() - 10);
        batch.end();
    }

    public boolean onCollision() {
        Rectangle dogHitbox = dog.getBoundingRectangle();
        Rectangle ballHitbox = ball.getBoundingRectangle();
        Rectangle enemyHitbox;
        // if dog catches bone
        if (dogHitbox.overlaps(ballHitbox)) {
                score++; // Increment score on collision
                bark.play(); // Play bark sound
                System.out.println("Catch!"); // Collision detected, print message to console
                ball = new Ball(viewport); // Reset bone to a new random position at the top
                return true;
        }
        // if dog misses bone
        if (ballHitbox.getY() < 0) {
            score -= 1; // Decrement score if ball goes below the screen
            growl.play(); // Play growl sound
            flashText.show("MISSED!", 3f, Color.RED);
            System.out.println("Miss!"); // bone missed, print message to console
            ball = new Ball(viewport); // Reset bone to a new random position at the top
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
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        enemyTexture.dispose();
        batch.dispose();

    }
}

