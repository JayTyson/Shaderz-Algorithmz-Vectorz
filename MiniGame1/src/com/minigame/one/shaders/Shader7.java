package com.minigame.one.shaders;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader7 implements ApplicationListener {
	
final String VERT =  
		"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
		"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
		
		"uniform mat4 u_projTrans;\n" + 
		" \n" + 
		"varying vec2 vTexCoord;\n" +
		
		"void main() {\n" +  
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
			"varying vec2 vTexCoord;\n" + 
			"\n" + 
			"//our texture sampler\n" + 
			"uniform sampler2D u_texture;\n" + 
			"uniform float utime;\n" +
			
			"const float speed = 2.0;\n" +
			"const float bendFactor = 1.5;\n" +
			"\n" + 
			"void main() {\n" +  
			"float height = vTexCoord.y;\n" +
			"float offset = pow(height,0.2);\n" +
			"offset *= (sin(utime * speed) * bendFactor);\n" +
			"//offset *= smoothstep(0.0,1.0,sin(utime));\n" +
			"vec3 normalColor = texture2D(u_texture, fract(vec2(vTexCoord.x+offset,vTexCoord.y))).rgb;\n"+
			
			"\n" +
			"gl_FragColor = vec4(normalColor, 1.0);\n" + 
			"}";
	
	Texture tex;
	SpriteBatch batch;
	OrthographicCamera cam;
	ShaderProgram shader;
	Sprite sprite;
	float dt = 0;
	
	@Override
	public void create() {
		//the texture does not matter since we will ignore it anyways
		tex = new Texture(Gdx.files.internal("data/grass_block.png"));
		sprite = new Sprite(tex);
		
		//important since we aren't using some uniforms and attributes that SpriteBatch expects
		ShaderProgram.pedantic = false;
				
		shader = new ShaderProgram(VERT, FRAG);
		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}
		
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		

		batch = new SpriteBatch();
		batch.setShader(shader);
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		batch.begin();

		
		//notice that LibGDX coordinate system origin is lower-left
		shader.setUniformf("utime", dt);
		
		
		//batch.draw(tex, 0, 0);
		sprite.draw(batch);
		
		batch.end();
		dt += Gdx.graphics.getDeltaTime();
	
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		shader.dispose();
		tex.dispose();
	}	
}
