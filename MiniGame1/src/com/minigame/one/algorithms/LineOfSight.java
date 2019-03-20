package com.minigame.one.algorithms;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class LineOfSight implements ApplicationListener {
	
	public OrthographicCamera camera;
	public SpriteBatch batch;
	
	public ShapeRenderer shape;
	
	public Vector3 temp;
	
	int worldWidth = 40;
	int worldHeight = 30;
	
	float blockWidth = 1.0f;
	
	class Edge {
		float sx,sy;
		float ex,ey;
	}
	
	class Cell {
		int edge_id[] = new int[4];
		boolean edge_exist[] = new boolean[4];
		boolean exist;
		
	}
	
	public Cell world[];
	
	private ArrayList<Edge> edges;
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	
	@Override
	public void create() {
		camera = new OrthographicCamera(worldWidth,worldHeight);
		camera.position.set(worldWidth/2, worldHeight/2, 0);
		camera.update();
		//camera.setToOrtho(false);
		
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		temp = new Vector3();
		
		edges = new ArrayList<Edge>();
		
		world = new Cell[worldWidth * worldHeight];	
		for(int i = 0;i < world.length;i++) {
			world[i] = new Cell();
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		updateInput();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		
		shape.setProjectionMatrix(camera.combined);
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.BLUE);
		
		for(int x = 0;x < worldWidth;x++) {
			for(int y = 0;y < worldHeight;y++) {
				if(world[y * worldWidth + x].exist) {
					shape.rect(x * blockWidth, y * blockWidth, blockWidth, blockWidth);
				}
			}
		}
		
		
		
		shape.end();
		
		shape.begin(ShapeType.Line);
		shape.setColor(Color.WHITE);
		
		for(Edge e : edges) {
			shape.line(e.sx, e.sy, e.ex, e.ey);
		}
		
		shape.end();
		
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.RED);
		
		for(Edge e : edges) {
			///shape.line(e.sx, e.sy, e.ex, e.ey);
			shape.circle(e.sx, e.sy, 0.3f);
			shape.circle(e.ex, e.ey, 0.3f);
		}
		
		shape.end();
				
	}
	
	public void updateInput() {
		
		if(Gdx.input.justTouched()) {
			temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(temp);
			float x = temp.x;
			float y = temp.y;			
			
			int i  = ((int)y / (int)blockWidth) * worldWidth + ((int)x / (int)blockWidth);
			world[i].exist = !world[i].exist;
			System.out.println(edges.size());
		}
		
		convertTileMapToPolyMap(0,0,40,30,blockWidth,worldWidth);
		
		
	}
	
	private void convertTileMapToPolyMap(int sx,int sy,int rWidth,int rHeight,float blockWidth,int pitch) {
		
		edges.clear();
		
		for(int x = 0;x < rWidth;x++) {
			for(int y = 0;y < rHeight;y++) {
				for(int j = 0;j < 4;j++) {
					world[(y + sy) * pitch + (x + sx)].edge_exist[j] = false;
					world[(y + sy) * pitch + (x + sx)].edge_id[j] = 0;
				}
			}
		}
		
		for(int x = 1;x < rWidth-1;x++) {
			for(int y = 1;y < rHeight-1;y++) {
				
				//Get the current cell and it's neihgbors
				int i = (y + sy) * pitch + (x + sx);
				int n = (y + sy + 1) * pitch + (x + sx);
				int s = (y + sy - 1) * pitch + (x + sx);
				int w = (y + sy) * pitch + (x + sx - 1);
				int e = (y + sy) * pitch + (x + sx + 1);
				
				
				if(world[i].exist) {
					
					if(!world[w].exist) {
						
						if(world[n].edge_exist[WEST]) {
							
							edges.get(world[n].edge_id[WEST]).ey += blockWidth;
							world[i].edge_id[WEST] = world[n].edge_id[WEST];
							world[i].edge_exist[WEST] = true;
							
						} else {
							Edge edge = new Edge();
							
							edge.sx = (sx + x) * blockWidth;
							edge.sy = (sy + y) * blockWidth;
							
							edge.ex = edge.sx;
							edge.ey = edge.sy + blockWidth;
							
							int edge_id = edges.size();
							edges.add(edge);
							
							world[i].edge_id[WEST] = edge_id;
							world[i].edge_exist[WEST] = true;
						}
					}
					
					if(!world[e].exist) {
						if(world[n].edge_exist[EAST]) {
							edges.get(world[n].edge_id[EAST]).ey += blockWidth;
							world[i].edge_id[EAST] = world[n].edge_id[EAST];
							world[i].edge_exist[EAST] = true;
						} else {
							Edge edge = new Edge();
							edge.sx = (sx + x + 1) * blockWidth;
							edge.sy = (sy + y) * blockWidth;
							
							edge.ex = edge.sx;
							edge.ey = edge.sy + blockWidth;
							
							int edge_id = edges.size();
							edges.add(edge);
							
							world[i].edge_id[EAST] = edge_id;
							world[i].edge_exist[EAST] = true;
						}
					}
					
					if(!world[n].exist) {
						if(world[w].edge_exist[NORTH]) {
							edges.get(world[w].edge_id[NORTH]).ex += blockWidth;
							world[i].edge_id[NORTH] = world[w].edge_id[NORTH];
							world[i].edge_exist[NORTH] = true;
						} else {
							Edge edge = new Edge();
							edge.sx = (sx + x) * blockWidth;
							edge.sy = (sy + y + 1) * blockWidth;
							
							edge.ex = edge.sx + blockWidth;
							edge.ey = edge.sy;
							
							int edge_id = edges.size();
							edges.add(edge);
							
							world[i].edge_id[NORTH] = edge_id;
							world[i].edge_exist[NORTH] = true;
						}
					}
					
					if(!world[s].exist) {
						if(world[w].edge_exist[SOUTH]) {
							edges.get(world[w].edge_id[SOUTH]).ex += blockWidth;
							world[i].edge_id[SOUTH] = world[w].edge_id[SOUTH];
							world[i].edge_exist[SOUTH] = true;
						} else {
							Edge edge = new Edge();
							edge.sx = (sx + x) * blockWidth;
							edge.sy = (sy + y) * blockWidth;
							
							edge.ex = edge.sx + blockWidth;
							edge.ey = edge.sy;
							
							int edge_id = edges.size();
							edges.add(edge);
							
							world[i].edge_id[SOUTH] = edge_id;
							world[i].edge_exist[SOUTH] = true;
						}
					}
				}
			}
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
