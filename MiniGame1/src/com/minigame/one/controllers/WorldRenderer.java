package com.minigame.one.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable{
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	public WorldController worldController;
	
	private static final float V_HEIGHT = 10.0f;
	private static final float V_WIDTH = 20.0f;
	
	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}
	
	public void init() {
		camera = new OrthographicCamera(V_WIDTH,V_HEIGHT);
		camera.position.set(V_WIDTH/2f, V_HEIGHT/2f, 0);
		camera.zoom = 1.f;
		camera.update();
		
		batch = new SpriteBatch();
	}
	
	public void renderWorld() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(int i = 0;i < 20;i++) {
			for(int j = 0;j < 10;j++) {
				worldController.players[i][j].render(batch);
			}
		}
		worldController.player.render(batch);
		batch.end();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
