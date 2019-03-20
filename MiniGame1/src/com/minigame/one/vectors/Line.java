package com.minigame.one.vectors;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Line implements ApplicationListener {
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private ShapeRenderer shape;
	
	public class Point {
		public int x,y;
		
		public Point(int x,int y) {
			//position = new Vector2(x,y);
			this.x = x;
			this.y = y;
		}
		
		public void setXY(int x,int y) {
			this.x = x;
			this.y = y;
		}
		
		
		
	}
	
	private Point startPoint;
	private Point endPoint;
	
	private ArrayList<Point> points;
	float t = 0;
	
	Vector3 temp;

	@Override
	public void create() {
		
		camera = new OrthographicCamera(40,40);
		camera.position.x = 40/2;
		camera.position.y = 40/2;
		//camera.zoom = 1.5f;
		camera.update();
		//camera.setToOrtho(false);
		batch = new SpriteBatch();
		
		shape = new ShapeRenderer();
		
		points = new ArrayList<Point>();
		startPoint = new Point(2,3);
		endPoint = new Point(39,39);
		
		temp = new Vector3();		
		
	}
	
	private Point roundPoint(Point point) {
		return new Point(Math.round(point.x),Math.round(point.y));
	}
	
	private int lerp(int startPoint,int endPoint,float t) {
		return (int) (startPoint + t * (endPoint - startPoint));
	}
	
	private Point lerpPoint(Point startPoint,Point endPoint,float t) {
		return new Point(lerp(startPoint.x,endPoint.x,t), 
						 lerp(startPoint.y,endPoint.y,t));
	}
	
	private float diag_Distance(Point startPoint,Point endPoint) {
		float dx = endPoint.x - startPoint.x; 
		float dy = endPoint.y - startPoint.y;
		return Math.max(Math.abs(dx), Math.abs(dy));
	}
	
	private void updateControls() {
		if(Gdx.input.isButtonPressed(0)) {
			
			//Vector3 temp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			temp.x = Gdx.input.getX();
			temp.y = Gdx.input.getY();
			camera.unproject(temp);
			float x = temp.x;
			float y = temp.y;
			Point endPointy = new Point((int)x,(int)y);
			endPoint = endPointy;
			points = drawLine(startPoint,endPointy);
			System.out.println(points.size());
		} else if(Gdx.input.isButtonPressed(1)) {
			temp.x = Gdx.input.getX();
			temp.y = Gdx.input.getY();
			camera.unproject(temp);
			float x = temp.x;
			float y = temp.y;
			Point startPointy = new Point((int)x,(int)y);
			startPoint = startPointy;
			points = drawLine(startPointy,endPoint);
			System.out.println("Right");
		}
	}
	
	private ArrayList<Point> drawLine(Point startPoint,Point endPoint) {
		ArrayList<Point> points = new ArrayList<Point>();
		
		float N = diag_Distance(startPoint,endPoint);
		for(int step = 0;step <= N;step++) {
			t = N == 0 ? 0.0f : step/N;
			points.add(roundPoint(lerpPoint(endPoint,startPoint,t)));
		}
		
		return points;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		updateControls();
		Gdx.gl20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.end();
		// TODO Auto-generated method stub
		
		float w = Gdx.graphics.getWidth()/40;
		float h = Gdx.graphics.getHeight()/40;
		
		shape.setProjectionMatrix(camera.combined);
		shape.begin(ShapeType.Line);
		shape.setColor(Color.WHITE);
		for(int i = 0;i < 40;i++) {
			for(int j = 0;j < 40;j++) {
				shape.rect(i, j, 1, 1);
			}
		}
		shape.end();
		
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.WHITE);
		//shape.rect(startPoint.x, startPoint.y, 1, 1);
		//shape.rect(endPoint.x, endPoint.y, 1, 1);
		
		
		for(Point point:points) {
			shape.rect(point.x, point.y, 1, 1);
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
