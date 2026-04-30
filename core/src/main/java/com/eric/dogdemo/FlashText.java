package com.eric.dogdemo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class FlashText {
    private final BitmapFont font;
    private String text;
    private float timer = 0f;
    private boolean active = false;
    private Color color = Color.WHITE; 

    public FlashText(BitmapFont font) {
        this.font = font;
    }

    public void show(String text, float duration, Color color) {
        this.text = text;
        this.timer = duration;
        this.active = true;
        this.color = color;
    }

    public void update(float delta) {
        if (active) {
            timer -= delta;
            if (timer <= 0) active = false;
        }
    }

    public void render(SpriteBatch batch, ScreenViewport uiViewport) {
        if (!active) return;

        // Flash effect: toggle visibility every 0.1 seconds
        if ((int)(timer / 0.1f) % 2 == 0) {
            font.setColor(color);
            font.draw(batch, text,
                uiViewport.getScreenWidth() / 2f - 60,  // roughly centered
                uiViewport.getScreenHeight() / 2f);
                
        }
    }
}