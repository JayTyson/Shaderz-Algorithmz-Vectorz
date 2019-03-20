package com.minigame.one.shaders;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Shader3 implements ApplicationListener {
	
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
	
	
	//This will be dumped to System.out for clarity
	final String FRAG = 
			//GL ES specific stuff
			  "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"//texture 0\n" + 
			
			"uniform sampler2D u_texture;\n" + 
			"\n" + 
			"//our screen resolution, set from Java whenever the display is resized\n" + 
			"uniform vec2 resolution;\n" + 
			"\n" + 
			"//\"in\" attributes from our vertex shader\n" + 
			"varying LOWP vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" + 
			"\n" + 
			"//RADIUS of our vignette, where 0.5 results in a circle fitting the screen\n" + 
			"const float radius = 0.75;\n" + 
			"\n" + 
			"//softness of our vignette, between 0.0 and 1.0\n" + 
			"const float SOFTNESS = 0.45;\n" + 
			"\n" + 
			"//sepia colour, adjust to taste\n" + 
			"const vec3 SEPIA = vec3(1.2, 1.0, 0.8); \n" + 
			"\n" + 
			"void main() {\n" + 
			"	//sample our texture\n" + 
			"	vec4 texColor = texture2D(u_texture, vTexCoord);\n" + 
			"		\n" + 
			"	//1. VIGNETTE\n" + 
			"	\n" + 
			"	//determine center position\n" + 
			"	vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);\n" + 
			"	\n" + 
			"	position.x = position.x * (resolution.x / resolution.y);\n" +
			"	//determine the vector length of the center position\n" + 
			"	float len = length(position);\n" + 
			"	\n" + 
			"	//use smoothstep to create a smooth vignette\n" + 
			"	float vignette = smoothstep(radius, radius-SOFTNESS, len);\n" + 
			"	\n" + 
			"	//apply the vignette with 50% opacity\n" + 
			"	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);\n" + 
			"		\n" + 
			"	//2. GRAYSCALE\n" + 
			"	\n" + 
			"	//convert to grayscale using NTSC conversion weights\n" + 
			"	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n" + 
			"	\n" + 
			"	//3. SEPIA\n" + 
			"	\n" + 
			"	//create our sepia tone from some constant value\n" + 
			"	//vec3 sepiaColor = vec3(gray) * SEPIA;\n" + 
			"		\n" + 
			"	//again we'll use mix so that the sepia effect is at 75%\n" + 
			"	//texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);\n" + 
			"		\n" + 
			"	//final colour, multiplied by vertex colour\n" + 
			"	gl_FragColor = texColor * vColor;\n" + 
			"}";
	
	Texture tex;
	SpriteBatch batch;
	OrthographicCamera cam;
	ShaderProgram shader;
	 
	@Override
	public void create() {
		//the texture does not matter since we will ignore it anyways
		tex = new Texture(Gdx.files.internal("data/scene.png"));
		
		//important since we aren't using some uniforms and attributes that SpriteBatch expects
		ShaderProgram.pedantic = false;
		
		//print it out for clarity
		System.out.println("Vertex Shader:\n-------------\n\n"+VERT);
		System.out.println("\n");
		System.out.println("Fragment Shader:\n-------------\n\n"+FRAG);
		
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
		
		Vector2 res = new Vector2(640,480);
		Vector2 fc = new Vector2(32,10);
		Vector2 temp = new Vector2(0.5f,0.5f);
		Vector2 ans = fc.div(res).sub(temp);
		System.out.println(ans.x + " " + ans.y);
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
		
	}

	@Override
	public void render() {
		Gdx.gl20.glClearColor(0, 0, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		//bind the shader, then set the uniform, then unbind the shader
		shader.begin();
		shader.setUniformf("resolution", width, height);
		shader.end();
		
		batch.begin();
		
		batch.draw(tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch.end();
		
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
