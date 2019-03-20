package com.minigame.one.vectors;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Vec implements ApplicationListener {
	
	
	OrthographicCamera camera;
	SpriteBatch spriteBatch;
	
	Vector2 dist;
	
	Moon moon;
	Player player;

	@Override
	public void create() {
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.zoom = 3f;
		camera.update();
		spriteBatch = new SpriteBatch();
		
		moon = new Moon();
		moon.position.set((Gdx.graphics.getWidth()*0.5f)-moon.sprite.getWidth()*0.5f,
				(Gdx.graphics.getHeight()*0.5f)-moon.sprite.getHeight()*0.5f);
		
		player = new Player();
		
		dist = new Vector2();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		
		updateInput();
		player.updatePlayer();
		updateGravity();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(1, 0, 0, 1);
		
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		moon.renderMoon(spriteBatch);
		player.renderPlayer(spriteBatch);
		spriteBatch.end();
		
	}
	
	public void updateInput() {
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.velocity.x = -2;
		} else if(!Gdx.input.isKeyPressed(Keys.LEFT)){
			//player.velocity.x = 0;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.velocity.x = 2;
		} else if(!Gdx.input.isKeyPressed(Keys.RIGHT)){
			//player.velocity.x = 0;
		}
		
		
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			player.velocity.y = -2;
		} else if(!Gdx.input.isKeyPressed(Keys.DOWN)){
			//player.velocity.x = 0;
		}
		
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			player.velocity.y = 2;
		} else if(!Gdx.input.isKeyPressed(Keys.UP)){
			//player.velocity.x = 0;
		}
	}
	
	public void updateGravity() {
		dist.x = moon.position.x - player.sprite.getX();
		dist.y = moon.position.y - player.sprite.getX();
		float distance = (float)Math.sqrt(dist.x * dist.x + dist.y * dist.y);
		
		float dx = dist.x / distance;
		float dy = dist.y / distance;
		
		/*player.gravity.set(dx*0.05f, dy * 0.05f);*/
		player.gravity.x = dx*(5*15)/distance;
		player.gravity.y = dy*(5*15)/distance;
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	class Moon {
		
		Vector2 position;
		
		Sprite sprite;
		Texture texture;
		
		public Moon() {
			position = new Vector2();
			
			texture = new Texture(Gdx.files.internal("data/moon.png"));
			sprite = new Sprite(texture);
		}
		
		public void renderMoon(SpriteBatch batch) {
			
			sprite.setPosition(position.x, position.y);
			sprite.draw(batch);
		}
		
	}
	
	class Player {
		
		Vector2 position;
		Vector2 velocity;
		Vector2 gravity;
		Sprite sprite;
		Texture texture;
		
		public Player() {
			
			velocity = new Vector2();
			position = new Vector2();
			gravity = new Vector2();
			texture = new Texture(Gdx.files.internal("data/player.png"));
			sprite = new Sprite(texture);
			
		}
		
		public void updatePlayer() {
			velocity.add(gravity);
			position.add(velocity);
			sprite.setPosition(position.x, position.y);
		}
		
		public void renderPlayer(SpriteBatch batch) {
			sprite.draw(batch);
		}
		
	}

}
