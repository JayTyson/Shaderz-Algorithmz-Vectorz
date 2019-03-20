package com.minigame.one.fbo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferSample implements ApplicationListener {
	
	private enum GALLERY_STATE {
			PICTURE,
			TRANSITIONING,
	}
	
	private static final int GALLERY_NUM_PICTURES = 4;
	private static final float GALLERY_PICTURE_TIME = 3.0F;
	private static final float GALLERY_TRANSITION_TIME = 2.0F;
	
	public SpriteBatch batch;
	public OrthographicCamera camera;
	
	public TextureRegion[] gallery;
	public FrameBuffer currentFrameBuffer;
	public FrameBuffer nextFrameBuffer;
	
	private int currentPicture;
	private float time;
	private GALLERY_STATE state;

	@Override
	public void create() {
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera(20,20);
		camera.position.set(20f/2f, 20f/2f, 0f);
		camera.update();
		gallery = new TextureRegion[GALLERY_NUM_PICTURES];
		for(int i = 0;i < GALLERY_NUM_PICTURES;++i) {
			gallery[i] = new TextureRegion(new Texture(Gdx.files.internal("data/pic" + (i+1) + ".png")));
		}
		
		currentFrameBuffer = new FrameBuffer(Format.RGB888,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		nextFrameBuffer = new FrameBuffer(Format.RGB888,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		
		currentPicture = 0;
		time = 0.0f;
		state = GALLERY_STATE.PICTURE;
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		batch.setProjectionMatrix(camera.combined);
		time += Gdx.graphics.getDeltaTime();
		
		switch(state) {
		case PICTURE:
			updateStatePicture();
			break;
		case TRANSITIONING:
			updateStateTransitioning();
			break;
		}
		
	}
	
	private void drawRegion(TextureRegion region) {
		batch.draw(region, 0, 0, 10, 10);
	}
	
	private void drawTexture(Texture tex) {
		batch.draw(tex, 0, 0, 0, 0);
	}
	
	public void updateStatePicture() {
		TextureRegion region = gallery[currentPicture];
		
		batch.begin();
		drawRegion(gallery[currentPicture]);
		batch.end();
		
		if(time > GALLERY_PICTURE_TIME) {
			time = 0.0f;
			state = GALLERY_STATE.TRANSITIONING;
			
			region.flip(false, true);
			currentFrameBuffer.begin();
			
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			batch.begin();
			drawRegion(region);
			batch.end();
			currentFrameBuffer.end();
			
			region.flip(false, true);
			
			currentPicture = (currentPicture + 1) % GALLERY_NUM_PICTURES;
			
			region = gallery[currentPicture];
			region.flip(false, true);
			
			nextFrameBuffer.begin();
			
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			batch.begin();
			drawRegion(gallery[currentPicture]);
			batch.end();
			nextFrameBuffer.end();
			
			region.flip(false, true);
		}
	}
	
	private void updateStateTransitioning() {
		float alpha = Math.min(time / GALLERY_TRANSITION_TIME,1.0f);
		batch.begin();
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f-alpha);
		drawTexture(currentFrameBuffer.getColorBufferTexture());
		
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		drawTexture(currentFrameBuffer.getColorBufferTexture());
		batch.end();
		
		if(time > GALLERY_TRANSITION_TIME) {
			time = 0.0f;
			state = GALLERY_STATE.PICTURE;
		}
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}
	
	

}
