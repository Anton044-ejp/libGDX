package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Player extends Sprite {
    //private String image = "dog (1).png";
    private final float speed = 2f; // units per second 
    private final FitViewport viewport;


    public Player(FitViewport viewport) {
        super(new Texture("dog (1).png"));
        this.viewport = viewport;
    }

    public void input() {

        float delta = Gdx.graphics.getDeltaTime(); // for all hw frame rate
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.translateX(-speed*delta); // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.translateX(speed*delta); // Move right
        }
         if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            this.translateY(speed*delta); // Move up 
        }  
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            this.translateY(-speed*delta); // Move down
        }
    }

    public void move() {
        //dog.translateX(1 * Gdx.graphics.getDeltaTime()); // Move right at 1 unit per second
        this.setX(MathUtils.clamp(this.getX(), 
      0, viewport.getWorldWidth() - this.getWidth()));
        this.setY(MathUtils.clamp(this.getY(),
      0, viewport.getWorldHeight() - this.getHeight()));
    }
    
}
