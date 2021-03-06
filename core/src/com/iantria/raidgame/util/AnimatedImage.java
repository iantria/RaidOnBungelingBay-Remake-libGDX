package com.iantria.raidgame.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedImage extends Image
{
    protected Animation<TextureRegion> animation = null;
    private float stateTime = 0;
    private float deltaSpeed = 1f;

    public AnimatedImage(Animation<TextureRegion> animation, float deltaSpeed) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        this.deltaSpeed = deltaSpeed;
    }

    @Override
    public void act(float delta)
    {
        ((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime+=delta*deltaSpeed, true));
        super.act(delta);
    }
}