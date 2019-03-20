package com.minigame.one.shaders;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NormalMap implements ApplicationListener {
	
	Texture rock,rockNormals,faneto,brick,brick_normals;
	
	SpriteBatch batch;
	OrthographicCamera cam;

	Vector3 temp;
	ShaderProgram shader;

	//our constants...
	public static final float DEFAULT_LIGHT_Z = 0.025f;
	public static final float AMBIENT_INTENSITY = 0.3f;
	public static final float LIGHT_INTENSITY = 0.5f;
	
	public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);
	
	//Light RGB and intensity (alpha)
	public static final Vector3 LIGHT_COLOR = new Vector3(1.0f, 0.2f, 0.2f);

	//Ambient RGB and intensity (alpha)
	public static final Vector3 AMBIENT_COLOR = new Vector3(0.5f, 0.5f, 0.5f);

	//Attenuation coefficients for light falloff
	public static final Vector3 FALLOFF = new Vector3(.05f, .05f, 20.5f);
	
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
	
		//no changes except for LOWP for color values
		//we would store this in a file for increased readability
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
				"uniform sampler2D u_normals;   //normal map\n" + 
				"\n" + 
				"//values used for shading algorithm...\n" + 
				"uniform vec2 Resolution;         //resolution of screen\n" + 
				"uniform vec3 LightPos;           //light position, normalized\n" + 
				"uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity\n" + 
				"uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity \n" + 
				"uniform vec3 Falloff;            //attenuation coefficients\n" + 
				"\n" + 
				"void main() {\n" + 
				"	//RGBA of our diffuse color\n" + 
				"	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);\n" + 
				"	\n" + 
				"	//RGB of our normal map\n" + 
				"	vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;\n" + 
				"	\n" + 
				"	//The delta position of light\n" + 
				"	vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);\n" + 
				"	\n" + 
				"	//Correct for aspect ratio\n" + 
				"	LightDir.x *= Resolution.x / Resolution.y;\n" + 
				"	\n" + 
				"	//Determine distance (used for attenuation) BEFORE we normalize our LightDir\n" + 
				"	float D = length(LightDir);\n" + 
				"	\n" + 
				"	//normalize our vectors\n" + 
				"	vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" + 
				"	vec3 L = normalize(LightDir);\n" + 
				"	\n" + 
				"	//Pre-multiply light color with intensity\n" + 
				"	//Then perform \"N dot L\" to determine our diffuse term\n" + 
				"	vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);\n" + 
				"\n" + 
				"	//pre-multiply ambient color with intensity\n" + 
				"	vec3 Ambient = AmbientColor.rgb * AmbientColor.a;\n" + 
				"	\n" + 
				"	//calculate attenuation\n" + 
				"	float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );\n" + 
				"	\n" + 
				"	//the calculation which brings it all together\n" + 
				"	vec3 Intensity = Ambient + Diffuse * Attenuation;\n" + 
				"	vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" + 
				"	gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);\n" + 
				"}";
		
				
		
				
	@Override
	public void create() {
		// TODO Auto-generated method stub
		rock = new Texture(Gdx.files.internal("data/brick.png"));
		rockNormals = new Texture(Gdx.files.internal("data/brick_normal.png"));
		faneto = new Texture(Gdx.files.internal("data/faneto_diffuse_map.png"));
		
		brick = new Texture(Gdx.files.internal("data/brick.png"));
		brick_normals = new Texture(Gdx.files.internal("data/brick_normal.png"));
		
		
		ShaderProgram.pedantic = false;
		setUpShader();
		
		batch = new SpriteBatch();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
		
		//handle mouse wheel
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean scrolled(int delta) {
				//LibGDX mouse wheel is inverted compared to lwjgl-basics
				LIGHT_POS.z = Math.max(0f, LIGHT_POS.z - (delta * 0.005f));
				System.out.println("New light Z: "+LIGHT_POS.z);
				return true;
			}
		});
	}
	
	private void setUpShader() {
		shader = new ShaderProgram(VERT, FRAG);
		//ensure it compiled
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		//setup default uniforms
		shader.begin();

		//our normal map
		shader.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		//light/ambient colors
		//LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
		shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
		shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
		shader.setUniformf("Falloff", FALLOFF);
		
		//LibGDX likes us to end the shader program
		shader.end();
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
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0.2f);
		//reset light Z
		if (Gdx.input.isTouched()) {
			LIGHT_POS.z = DEFAULT_LIGHT_Z;
			System.out.println("New light Z: "+LIGHT_POS.z);
		}
				
		//shader will now be in use...
		
		//update light position, normalized to screen resolution
		//float x = Mouse.getX() / (float)Display.getWidth();
		//float y = Mouse.getY() / (float)Display.getHeight();
		Vector3 temp = new Vector3();
		temp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(temp);
		float x2 = temp.x / (float)Gdx.graphics.getWidth();
		float y2 = temp.y / (float)Gdx.graphics.getHeight();
				
		LIGHT_POS.x = x2;
		LIGHT_POS.y = y2;
		
		batch.setShader(null);
		batch.begin();
		batch.draw(faneto,512,0);
		batch.end();
		
		
		batch.setShader(shader);
		batch.begin();
		
		//shader will now be in use...
		
		//send a Vector3f to GLSL
		shader.setUniformf("LightPos", LIGHT_POS);
		
		//bind normal map to texture unit 1
		rockNormals.bind(1);
		
		//bind diffuse color to texture unit 0
		//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
		rock.bind(0);
				
		//draw the texture unit 0 with our shader effect applied
		//batch.draw(brick,0,0);
		batch.draw(rock, 0, 0);
		
		
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
