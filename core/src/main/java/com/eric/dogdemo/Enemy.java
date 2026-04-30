package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Enemy extends Sprite {
    private float speed = 2f; // units per second
    private final FitViewport viewport;
    private Rectangle bounds; // for collision detection
    

    public Enemy(Texture texture, FitViewport viewport) {
        super(new Texture("enemy.png"));
        this.viewport = viewport;
        setSize(1f, 0.5f); // Set the size
        setPosition(viewport.getWorldWidth(), 0); // Start at the right edge
        bounds = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        setPosition(getX() - speed * delta, getY()); // move Sprite's actual position
        bounds.setPosition(getX(), getY());
    }

    public void render(SpriteBatch batch) {
        draw(batch); // Sprite's built-in draw
    }

    public boolean isOffScreen() {
        return getX() + getWidth() < 0;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    
}
