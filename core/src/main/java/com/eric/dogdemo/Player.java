package com.eric.dogdemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Player extends Sprite {
    private float speed = 4f; // units per second 
    private final FitViewport viewport;
    private float velocityY = 0f; // velocity is speed + direction
    private final float gravity = -7f; // upHeight/downSpeed-Adjust as needed
    private final float jumpVelocity = 5f; // upSpeed-Adjust as needed
    private boolean isGrounded = true;
    private float slowTimer = 0f;
    private float currentSpeed = 4f;  // whatever your current speed is
    private final float slowedSpeed = 0.5f; // slowed speed when hit by enemy


    public Player(FitViewport viewport) {
        super(new Texture("dog (1).png"));
        this.viewport = viewport;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void input() {
        float delta = Gdx.graphics.getDeltaTime(); // for all hw frame rate

        //**************Slow movement logic**************
        slowTimer -= delta;
        // Ensure slowTimer doesn't go negative
        if (slowTimer <= 0) {
            slowTimer = 0f;
        }
        // If slowTimer is active, use slowed speed; otherwise, use normal speed
        if (slowTimer > 0) {
            currentSpeed = slowedSpeed;
        } else {
            currentSpeed = speed;
        }
        
        // **************Horizontal movement logic**************
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.translateX(-currentSpeed*delta); // Move left
            if (!isFlipX()) flip(true, false); // flip to face left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.translateX(currentSpeed*delta); // Move right
            if (isFlipX()) flip(true, false); // flip to face right
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
    }// end of input method

    /***************Platform landing logic**************
     * Checks if player is landing on a platform and adjusts position and velocity accordingly.
     * Only allows landing if player is falling and feet are near the top of the platform.
     */
    public void landOnPlatform(Rectangle platform) {
        Rectangle dogBounds = getBoundingRectangle();
        // only land if falling downward and feet are near the top of platform
        if (velocityY < 0 && dogBounds.overlaps(platform) &&
            getY() >= platform.y) {
                setY(platform.y + platform.height);
                velocityY = 0;
                isGrounded = true;
            }
    }

    // **************World clamping logic**************
    public void move() {
        this.setX(MathUtils.clamp(this.getX(), 
      0, viewport.getWorldWidth() - this.getWidth()));
        this.setY(MathUtils.clamp(this.getY(),
      0, viewport.getWorldHeight() - this.getHeight()));
    }

    //**************Slow movement methods**************
    public void applySlow() {
        slowTimer = 3f; // seconds of slow (adjust 1-2f as you like)       
    }

    public boolean isSlowed() {
        return slowTimer > 0;
    }
    // **************Reset player state (for new game or after death)**************
    public void reset() {
        slowTimer = 0f;
        velocityY = 0f;
        isGrounded = true;
    }


}
