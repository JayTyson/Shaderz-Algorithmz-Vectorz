package com.minigame.one.fbo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Rotation extends InputAdapter implements ApplicationListener {
	
	public SpriteBatch batch;
	public OrthographicCamera camera;
	
	Texture texture;
	Sprite spriteNoRotation;
	Sprite spriteCenterThenSetSizeThenRotate;
	Sprite spriteSetSizeThenCenterThenRotate;

	@Override
	public void create() {
		// TODO Auto-generated method stub
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		
		
		texture = new Texture(Gdx.files.internal("data/black_marked_0.png"));
		spriteNoRotation = new Sprite(texture);
		spriteNoRotation.setOrigin(5, 5);
		spriteNoRotation.setSize(texture.getWidth()/2, texture.getHeight()/2);
		//Gdx.input.setInputProcessor(this);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		
		updateControlls();
		//sprite.setOrigin(sprite.getX(), sprite.getY());
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// TODO Auto-generated method stub
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//sprite.draw(batch);
		batch.end();
		
	}
	
	private void updateControlls() {
		if(Gdx.input.isTouched()) {
			Vector3 temp = new Vector3();
			temp.set(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(temp);
			float x = temp.x;
			float y = temp.y;

			//float dx = x - sprite.getX();
			//float dy = y - sprite.getY();

			//float angle = (float) (MathUtils.atan2(dy,dx) * 180 / Math.PI);
			//sprite.setRotation(angle);
		}
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

}
