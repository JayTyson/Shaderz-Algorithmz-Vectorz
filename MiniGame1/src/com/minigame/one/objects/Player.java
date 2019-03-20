package com.minigame.one.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;;

public class Player extends Mini {
	
	public int black;

	public Player(String texDest) {
		super(texDest);
		
		position = new Vector2();
		velocity = new Vector2();
		miniRec = new Rectangle();
		miniSprite.setSize(1f, 1f);
	}

	@Override
	public void update() {
		position.add(velocity);
		miniSprite.setPosition(position.x, position.y);
	}

	@Override
	public void render(SpriteBatch spriteBatch) {
		miniSprite.draw(spriteBatch);
	}
	
	@Override
	public String toString() {
		return "This is a string";
	}

}
