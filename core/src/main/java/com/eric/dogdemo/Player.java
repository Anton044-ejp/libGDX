package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Player extends Sprite {
    private final float speed = 2f; // units per second 
    private final FitViewport viewport;
    private float velocityY = 0f; // velocity is speed + direction
    private final float gravity = -9.8f; // Adjust as needed
    private final float jumpVelocity = 5f; //  Adjust as needed
    private boolean isGrounded = true;
    private float slowTimer = 0f;
    private float currentSpeed = 2f;  // whatever your current speed is
    private final float slowedSpeed = 1f; // half


    public Player(FitViewport viewport) {
        super(new Texture("dog (1).png"));
        this.viewport = viewport;
    }

    public void input() {
        float delta = Gdx.graphics.getDeltaTime(); // for all hw frame rate

        // Update slow timer
        slowTimer -= delta;
        if (slowTimer < 0) slowTimer = 0f;
        currentSpeed = (slowTimer > 0) ? slowedSpeed : speed;
        
        // **************Horizontal movement logic**************
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.translateX(-currentSpeed*delta); // Move left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.translateX(currentSpeed*delta); // Move right
        }

        // **************Jumping logic**************
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isGrounded) {
            velocityY = jumpVelocity; // Set initial jump velocity
            isGrounded = false; // Player is now in the air
        }
        // Apply gravity
        velocityY += gravity * delta;
        this.translateY(velocityY * delta);
        // Check if player has landed on the ground (y <= 0)
        if (this.getY() <= 0) {
            this.setY(0);
            velocityY = 0;
            isGrounded = true;
        }
    }

    public void move() {
        this.setX(MathUtils.clamp(this.getX(), 
      0, viewport.getWorldWidth() - this.getWidth()));
        this.setY(MathUtils.clamp(this.getY(),
      0, viewport.getWorldHeight() - this.getHeight()));
    }

    public void applySlow() {
        slowTimer = 1f; // seconds of slow (adjust 1-2f as you like)       
    }


    
}
