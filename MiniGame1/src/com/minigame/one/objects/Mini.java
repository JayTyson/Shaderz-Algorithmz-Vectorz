package com.minigame.one.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Mini {
	
	public Vector2 position,velocity;
	
	public Sprite miniSprite;
	public Texture miniTexture;
	public Rectangle miniRec;
	
	
	public Mini(String texDest) {
		miniTexture = new Texture(Gdx.files.internal(texDest));
		miniSprite = new Sprite(miniTexture);
		
	}
	
	public abstract void update();
	
	public abstract void render(SpriteBatch spriteBatch);

}
