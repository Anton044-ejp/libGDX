package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Ball extends Sprite {
    private float speed = 1f; // units per second
    private final FitViewport viewport;

    public Ball(FitViewport viewport) {
        super(new Texture("ball.png"));
        this.viewport = viewport;
        setSize(1f, 0.5f); // Set the size
        float randomX = MathUtils.random(0, 
            viewport.getWorldWidth() - getWidth()); // Random X position within the viewport, accounting for ball width
        setPosition(randomX, viewport.getWorldHeight() - getHeight()); // Start at the top with a random X position
    }

    public void update() {
        if (this.getY() < 0) {
            // Hit bottom - reset to top with random X position
            float randomX = MathUtils.random(0, viewport.getWorldWidth() - getWidth());
            setPosition(randomX, viewport.getWorldHeight() - getHeight());
            speed = 1f; // Reset speed
        } else if (this.getY() > viewport.getWorldHeight() - this.getHeight()) {
            // Hit top - bounce
            speed = -speed;
        }

        translateY(-speed * Gdx.graphics.getDeltaTime());
    }
}
