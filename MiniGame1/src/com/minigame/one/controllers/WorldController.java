package com.minigame.one.controllers;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.minigame.one.objects.Mini;
import com.minigame.one.objects.Player;

public class WorldController {
	
	public Mini player;
	
	public Player[][] players;
	
	public int updateTime;
	
	public int[] evenNumbersX;
	public int[] evenNumbersY;
	
	
	public WorldController() {
		init();
	}
	
	public void init() {
		player = new Player("data/player2.png");
		players = new Player[20][10];
		evenNumbersX = new int[players.length];
		evenNumbersY = new int[players.length/2];
				
		for(int i = 0;i < 20;i++) {
			for(int j = 0;j < 10;j++) {
				players[i][j] = new Player("data/player.png");
			}
		}
		
		
		for(int i = 0;i < 20;i+=1) {
			for(int j = 0;j < 10;j+=1) {
				int a;
				a = i+j;
				if((i + j) % 2==0) {
					//System.out.println(a + " is divisable");
					players[i][j].miniSprite.setPosition(i, j);
					players[i][j].black = 1;
				} else { 
					players[i][j].black = 0;
				}
				System.out.println();
				
			}
		}
		for(int i = 0;i < 20-1;i++) {
			if(i % 2==0) {
				evenNumbersX[i] = i;
				//System.out.println("Even NumbersX: "+evenNumbersX[i]);
			}
		}
		
		for(int j = 0;j < 10-1;j++) {
			if(j % 2==0) {
				evenNumbersY[j] = j;
				//System.out.println("Even NumbersY: "+evenNumbersY[j]);
			}
		}
		
		System.out.println("Even NumbersX LENGTH: "+evenNumbersX.length);
		System.out.println("Even NumbersY LENGTH: "+evenNumbersY.length);
		
		
	}
	
	public void updateWorld() {
		updateControls();
		updateTime += 1;
		System.out.println("Start: " + updateTime);
		
		int black;
		
		for(int i = 0;i < 20;i++) {
			for(int j = 0;j < 10;j++) {
				black = players[i][j].black;
			}
		}
		
		
		if(updateTime >= 60) {
			player.position.x = MathUtils.random(0, evenNumbersX.length-1);
			player.position.y = MathUtils.random(0, evenNumbersY.length-1);
			player.update();
			updateTime = 0;			
		}
		System.out.println("End: " + updateTime);
		
	}
	
	public void updateControls() {
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			//player.velocity.x = 1;
		} else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.velocity.x = -1;
		} else {
			player.velocity.x = 0;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			player.velocity.y = 1;
		} else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			player.velocity.y = -1;
		} else {
			player.velocity.y = 0;
		}
	}

}
