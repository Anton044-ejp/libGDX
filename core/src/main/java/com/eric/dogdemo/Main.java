package com.eric.dogdemo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private Texture backgroundImage;
    private SpriteBatch batch;
    private Player dog;
    private Sprite background;
    private FitViewport viewport;
    private Ball ball;
    private boolean flag = false;

    @Override
    public void create() {
        backgroundImage = new Texture("background-clouds.jpg");
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        dog = new Player(viewport);
        ball = new Ball(viewport);
        background = new Sprite(backgroundImage);
        dog.setSize(1, 1); // set in 1 unit block in 8x5 viewport
        dog.setPosition(1, 1); // default (0,0) bottom left!
        background.setPosition(0, 0);
        background.setSize(8, 5); // fill the whole viewport
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);

        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        dog.input();
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        background.draw(batch);
        dog.draw(batch);
        if (onCollision()){
            flag = true;
        }
        ball.draw(batch);
        batch.end();
        dog.move();
        ball.update();
    }

    public boolean onCollision() {
        Rectangle dogHitbox = dog.getBoundingRectangle();
        Rectangle ballHitbox = ball.getBoundingRectangle();
        if (dogHitbox.overlaps(ballHitbox)) {
                System.out.println("Catch!"); // Collision detected, print message to console
                ball = new Ball(viewport); // Reset ball to a new random position at the top
                return true;
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
        // Destroy application's resources here.
    }
}