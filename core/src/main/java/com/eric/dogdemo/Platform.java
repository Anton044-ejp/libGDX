package com.eric.dogdemo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    private final Rectangle bounds;
    private final Sprite sprite;

    public Platform(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
        sprite = new Sprite(new Texture("platform.png"));
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

}
