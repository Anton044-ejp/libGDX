package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RunningEnemy extends Enemy {
    private float speed = 2f; // units per second

    

    

    public RunningEnemy(Texture texture, FitViewport viewport) {
        super(texture);
        setSize(0.75f, 0.5f); // Set the size
        // Start at the right edge with a random Y position
        setPosition(viewport.getWorldWidth(), 0); // Start at the right edge, ground level
        bounds = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }


    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
            return "Enemy[x=" + getX() + ", y=" + getY() + ", speed=" + speed + ", offScreen=" + isOffScreen() + "]";
    }

    @Override
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        setPosition(getX() - speed * delta, getY()); // move Sprite's actual position
        bounds.setPosition(getX(), getY());
    }

    @Override
    public void render(SpriteBatch batch) {
        draw(batch); // Sprite's built-in draw
    }
    
    @Override
    public boolean isOffScreen() {
        return getX() + getWidth() < 0;
    }
    
}
