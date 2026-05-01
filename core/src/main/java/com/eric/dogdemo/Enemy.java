package com.eric.dogdemo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Enemy extends Sprite {
    protected Rectangle bounds; // for collision detection

    public Enemy(Texture texture) {
        super(texture);
    }

    public abstract void update();
    public abstract boolean isOffScreen();

    public Rectangle getBounds() {
        return bounds;
    }
    public void render(SpriteBatch batch) {
        draw(batch); // Sprite's built-in draw
    }  
}
