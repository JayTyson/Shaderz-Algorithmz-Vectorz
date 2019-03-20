package com.minigame.one.vectors;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BallPhysics implements ApplicationListener {
	
	OrthographicCamera camera;
	ShapeRenderer shape;
	
	ArrayList<Ball> balls;
	
	Ball selected = null;
	Vector3 temp = new Vector3();
	
	class Ball {
		Vector2 position;
		Vector2 velocity;
		Vector2 acc;
		float radius;
		int id;
		
		public Ball() {
			position = new Vector2();
			velocity = new Vector2();
			acc = new Vector2();
			radius = 0;
		}
		
		public void updateBall(float delta) {
			
		}
	}
	
	void addBall(float x,float y,float radius) {
		Ball ball = new Ball();
		ball.position.set(x, y);
		ball.radius = radius;
		
		ball.id = balls.size();
		balls.add(ball);
	}
	
	boolean doBallCollide(float x1,float y1,float r1,float x2,float y2,float r2) {
		return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) <= (r1+r2)*(r1+r2);
	}
	
	boolean isPointerInside(float x1,float y1,float r1,float px,float py) {
		return Math.abs((x1 - px) * (x1 - px) + (y1 - py) * (y1 - py)) < (r1*r1);
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		
		shape = new ShapeRenderer();
		
		balls = new ArrayList<Ball>();
		
		float defaultRadius = 30;
		
		addBall(Gdx.graphics.getWidth() * 0.25f,Gdx.graphics.getHeight() * 0.5f,defaultRadius);
		addBall(Gdx.graphics.getWidth() * 0.75f,Gdx.graphics.getHeight() * 0.5f,defaultRadius);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	void mouseInput() {
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(temp);
			float px = temp.x;
			float py = temp.y;
			selected = null;
			for(Ball ball:balls) {
				if(isPointerInside(ball.position.x,ball.position.y,ball.radius,px,py)) {
					selected = ball;
					break;
				}
			}
			
		}
		
		if(Gdx.input.isTouched()) {
			if(selected != null) {
				selected.position.x = temp.x;
				selected.position.y = temp.y;
			}
		}
		
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)) {
			selected = null;
		}
	}

	@Override
	public void render() {
		mouseInput();
		for(Ball ball:balls) {
			for(Ball target:balls) {
				if(ball.id != target.id) {
					if(doBallCollide(ball.position.x,ball.position.y,ball.radius,target.position.x,target.position.y,target.radius)) {
						float distance = (float) Math.sqrt((ball.position.x - target.position.x) * (ball.position.x - target.position.x) +
								(ball.position.y - target.position.y) * (ball.position.y - target.position.y));
						
						float overlap = 0.5f * (distance - ball.radius - target.radius);
						
						ball.position.x -= overlap * (ball.position.x - target.position.x) / distance;
						ball.position.y -= overlap * (ball.position.y - target.position.y) / distance;
						
						target.position.x += overlap * (ball.position.x - target.position.x) / distance;
						target.position.y += overlap * (ball.position.y - target.position.y) / distance;
					}
				}
			}
		}
		
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		// TODO Auto-generated method stub
		shape.setProjectionMatrix(camera.combined);
		shape.begin(ShapeType.Filled);
		for(Ball ball:balls) {
			shape.circle(ball.position.x, ball.position.y, ball.radius);
		}
		shape.end();
		
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
