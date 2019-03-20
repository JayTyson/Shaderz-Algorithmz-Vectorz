package com.minigame.one.shaders;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Vignette implements ApplicationListener {
	
	SpriteBatch batch;
	OrthographicCamera cam;

	Vector3 temp;
	ShaderProgram shader;
	float delta;
	
	Texture t;
	
	float outerRadius = 0.5f; 
	float innerRadius = 0.4f;
	
	final String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			
			"uniform mat4 u_projTrans;\n" + 
			" \n" + 
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			
			"void main() {\n" +  
			"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";
	
	final String FRAG = 
			//GL ES specific stuff
			  "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"//attributes from vertex shader\n" + 
			"varying LOWP vec4 vColor;\n" + 
			"varying vec2 vTexCoord;\n" + 
			"\n" + 
			"//our texture samplers\n" + 
			"uniform sampler2D u_texture;   //diffuse map\n" + 
			"\n" + 
			"uniform vec2 Resolution;\n" +
			"uniform float outerRadius;\n" +
			"uniform float innerRadius;\n" +
			"uniform vec2 vignettePos;\n" +
			"\n" + 
			"float plot(vec2 st,float pct) {\n" +
			"	return smoothstep(pct-0.02,pct,st.y) - smoothstep(pct,pct+0.02,st.y);\n" +
			"}\n" +
			"void main() {\n" + 
			"	vec4 color = texture2D(u_texture,vTexCoord);\n" +
			"	vec2 relativePosition = (gl_FragCoord.xy/Resolution.xy) - vignettePos.xy;\n" +
			"	relativePosition.x *= Resolution.x / Resolution.y;\n" +
			"	float len = length(relativePosition);\n" +
			"	float vignette = smoothstep(outerRadius,innerRadius,len);\n" +
			"	color.rgb = mix(color.rgb,color.rgb * vignette,.7);\n" +
			"	gl_FragColor = vColor * color;\n" + 
			"}";
	
	private void setUpShader() {
		shader = new ShaderProgram(VERT, FRAG);
		//ensure it compiled
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		
		shader.begin();
		
		shader.setUniformf("outerRadius", outerRadius); //0.01
		shader.setUniformf("innerRadius", innerRadius); //0.009
		
		shader.end();
		
	}

	@Override
	public void create() {
		ShaderProgram.pedantic = false;
		setUpShader();
		
		batch = new SpriteBatch();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
		
		t = new Texture(Gdx.files.internal("data/brick.png"));
		
		//handle mouse wheel
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean scrolled(int delta) {
				//LibGDX mouse wheel is inverted compared to lwjgl-basics
				outerRadius = Math.max(0.01f,outerRadius - (delta*0.005f));
				innerRadius = Math.max(0.009f,innerRadius - (delta*0.005f));
				System.out.println(outerRadius);
				return true;
			}
		});
		
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
		
		shader.begin();
		shader.setUniformf("Resolution", width, height);
		shader.end();
		
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		Vector3 temp = new Vector3();
		temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(temp);
		float x2 = temp.x / (float)Gdx.graphics.getWidth();
		float y2 = temp.y / (float)Gdx.graphics.getHeight();
		
		
		//batch.setProjectionMatrix(cam.combined);
		batch.setShader(shader);
		batch.begin();
		shader.setUniformf("outerRadius", outerRadius);
		shader.setUniformf("innerRadius", innerRadius);
		shader.setUniformf("vignettePos", new Vector2(x2,y2));
		batch.draw(t,0,0);
		batch.end();
		
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
