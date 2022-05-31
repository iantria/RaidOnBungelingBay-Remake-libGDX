package com.iantria.raidgame.entity;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class ScrollingCombatText {

    public String id;
    public Vector2 position;
    public float rotation;
    public Color color;
    public String text;
    public BitmapFont font;
    public float timer;
    public boolean isFinished;
    public boolean isScroll;
	private GlyphLayout layout;
	private Color c;
	public static float lastCombatTextAddedToQueue = Statistics.gameTime;
	private boolean isReadyToStart;

	
    public ScrollingCombatText(String id, float scale, Vector2 position, String text, Color color, BitmapFont font, boolean scroll) {
        this.id = id;
        this.position = position;
        this.color = color;
        this.text = text;
        this.font = font;
        this.isScroll = scroll;
        this.isFinished = false;
        this.isReadyToStart = false;
        this.font.getData().setScale(0.25f);
        init();
    }
	
    public void init(){
		layout = new GlyphLayout(font, text);
    	if (!isScroll){
    		position.x = position.x + Constants.helicopter.image.getRegionWidth()/2*Constants.helicopter.scale - layout.width/2 ;
    	} else if (color == Color.RED){
    		position.y = position.y + Constants.helicopter.image.getRegionHeight()*Constants.helicopter.scale;
    		position.x = position.x - 10 - layout.width;
    	} else if (color == Color.WHITE){
    		position.y = position.y + Constants.helicopter.image.getRegionHeight()*Constants.helicopter.scale;
    		position.x = position.x + Constants.helicopter.image.getRegionWidth()*Constants.helicopter.scale + 10;
    	}  else if (color == Color.GREEN || color == Color.YELLOW){
    		position.y = position.y + Constants.helicopter.image.getRegionHeight()*Constants.helicopter.scale + 10;
    		position.x = position.x + Constants.helicopter.image.getRegionWidth()/2*Constants.helicopter.scale - layout.width/2 ;
    	}
    }
    
    public void reset() {
    	this.isFinished = false;
    }
    
    public void update(float delta){
    	if (!isReadyToStart) {
			if (Statistics.gameTime > lastCombatTextAddedToQueue + 0.5f){
				lastCombatTextAddedToQueue = Statistics.gameTime;
				isReadyToStart = true;
			} else {
				return;
			}
		}

    	timer += delta;
    	if (isScroll) {
    		position.y += 20f * delta;
	    	if (timer > 2.200f){
	    		timer = 0;
	    		isFinished = true;
	    		Constants.removeCombatTextList.add(this);
	    		return;
	    	}
    	} else {
    		if (timer > 4.4f){
	    		timer = 0;
	    		isFinished = true;
	    		Constants.removeCombatTextList.add(this);
	    		return;    			
    		}
    	}
    }
    
    public void render(Batch batch){
    	if (!isReadyToStart) return;

		font.setUseIntegerPositions(false);
    	if (!isFinished ) {
    		if (isScroll){
    			if (timer >= 0.6f) {
					if (color == Color.RED) c = new Color(1f, .10f, .10f, (1f - (timer / 1.600f)));
					if (color == Color.WHITE) c = new Color(1f, 1f, 1f, (1f - (timer / 1.600f)));
					if (color == Color.YELLOW) c = new Color(1f, 1f, .10f, (1f - (timer / 1.600f)));
					if (color == Color.GREEN) c = new Color(.10f, 1f, .10f, (1f - (timer / 1.600f)));
				} else {
					if (color == Color.RED) c = new Color(1f, .10f, .10f, 1f);
					if (color == Color.WHITE) c = new Color(1f, 1f, 1f, 1f);
					if (color == Color.YELLOW) c = new Color(1f, 1f, .10f, 1f);
					if (color == Color.GREEN) c = new Color(.10f, 1f, .10f, 1f);
				}
	    		font.setColor(c);
				font.draw(batch, text, position.x, position.y);
	    	} else {
	    		if (timer <= 1.40f){
		    		if (color == Color.RED)	c = new Color(1f, .10f, .10f,  (0 + (timer/1.40f)));
		    		if (color == Color.WHITE)	c = new Color(1f, 1f, 1f,  (0 + (timer/1.40f)));
		    		if (color == Color.YELLOW)	c = new Color(1f, 1f, .10f,  (0 + (timer/1.40f)));
		    		if (color == Color.GREEN)	c = new Color(.1f, 1f, .10f,  (0 + (timer/1.40f)));
	    		} else if (timer > 1.40f && timer <= 3.00f) {
		    		if (color == Color.RED)	c = new Color(1f, .10f, .10f, 1f);
		    		if (color == Color.WHITE)	c = new Color(1f, 1f, 1f,  1f);
		    		if (color == Color.YELLOW)	c = new Color(1f, 1f, .10f,  1f);
		    		if (color == Color.GREEN)	c = new Color(.10f, 1f, .10f, 1f);
	    		} else {
					if (color == Color.RED)	c = new Color(1f, .10f, .10f, (1f - ((timer-3.00f)/1.40f)));
					if (color == Color.WHITE)	c = new Color(1f, 1f, 1f,  (1f - ((timer-3.00f)/1.40f)));
					if (color == Color.YELLOW)	c = new Color(1f, 1f, .10f,  (1f - ((timer-3.00f)/1.40f)));
					if (color == Color.GREEN)	c = new Color(.10f, 1f, .10f, (1f - ((timer-3.00f)/1.40f)));
				}

				font.setColor(c);
	    		font.draw(batch, text, position.x, position.y);
	    	}
    	}
    }
}
