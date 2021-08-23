package com.boomballgame.Core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

enum BallColor {
    RED,
    GREEN,
    BLUE,
    BOOM
}

public class Ball implements IDrawable{

    private Sprite sprite;
    private BallColor ballColor;

    public Ball(Texture texture, BallColor cellColor, float size){
        this.ballColor = cellColor;
        sprite = new Sprite(texture);
        sprite.setSize(size, size);
    }
    public void setDrawPosition(float x, float y){

        sprite.setPosition(x * sprite.getWidth(), y * sprite.getHeight());
    }
    public void Draw(SpriteBatch sb){
        sprite.draw(sb);
    }
    public BallColor getBallColor(){
        return ballColor;
    }
}
