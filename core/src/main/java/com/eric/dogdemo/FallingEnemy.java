package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FallingEnemy extends Enemy {
    private final float speed = 2f; // units per second

    public FallingEnemy(Texture texture, FitViewport viewport) {
        super(texture);
        setSize(0.75f, 0.5f); // Set the size
        // Start at a random X position at the top of the screen
        setPosition((float)(Math.random() * (viewport.getWorldWidth() - getWidth())), 
            viewport.getWorldHeight());
        bounds = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        setPosition(getX(), getY() - speed * delta); // Move down
        bounds.setPosition(getX(), getY());
    }

    @Override
    public boolean isOffScreen() {
        return getY() + getHeight() < 0; // Check if it has fallen below the screen
    }
}
