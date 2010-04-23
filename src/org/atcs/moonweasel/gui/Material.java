package org.atcs.moonweasel.gui;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Material 
{
	private static final TextureLoader textureLoader;
	static {
		textureLoader = new TextureLoader();
	}
	
	String name;
	Texture texture;
	String bumpFileName;
	float[] ambient = new float[3];
	float[] diffuse = new float[3];
	float[] specular = new float[3];
	float d;
	float phong;
	float opticalDensity;
	int illum;
	
	String filePath;
	
	boolean hasName = false;
	boolean hasAmbient = false;
	boolean hasDiffuse = false;
	boolean hasSpecular = false;
	boolean hasD = false;
	boolean hasPhong = false;
	boolean hasOpticalDensity = false;
	boolean hasIllum = false;
	boolean hasTextureFile = false;
	boolean hasBumpFile = false;
	
	public Material()
	{
	}
	public void setName(String nameIn)
	{
		name = nameIn;
		hasName = true;
	}
	public void setAmbient(float[] ambientIn)
	{
		ambient = ambientIn;
		hasAmbient = true;
	}
	public void setDiffuse(float[] diffuseIn)
	{
		diffuse = diffuseIn;
		hasDiffuse = true;
	}
	public void setSpecular(float[] specularIn)
	{
		specular = specularIn;
		hasSpecular = true;
	}
	public void setD(float dIn)
	{
		d = dIn;
		hasD = true;
	}
	public void setPhong(float phongIn)
	{
		phong = phongIn;
		hasPhong = true;
	}
	public void setIllum(int illumIn)
	{
		illum = illumIn;
		hasIllum = true;
	}
	public void setTextureFileName(String fileNameIn) throws IOException
	{
		texture = textureLoader.getTexture(filePath + fileNameIn);
		hasTextureFile = true;
	}
	
	public void setOpticalDensity(float densityIn)
	{
		opticalDensity = densityIn;
		hasOpticalDensity = true;
	}
	
	public void setBumpFileName(String nameIn)
	{
		bumpFileName = nameIn;
		hasBumpFile = true;
	}
	
	public void setFilePath(String pathIn)
	{
		filePath = pathIn;
	}
	
	public void disable() {
		if (hasTextureFile) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public void enable()
	{
		if(hasAmbient)
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			buffer.put(ambient);
			buffer.put(1);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, buffer);
		}
		if(hasDiffuse)
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			buffer.put(diffuse);
			buffer.put(1);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, buffer);
		}
		if(hasSpecular)
		{
			FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			buffer.put(specular);
			buffer.put(1);
			buffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, buffer);
		}
		if(hasD)
		{
		}
		if(hasPhong)
		{
			
		}
		if(hasOpticalDensity)
		{
			
		}
		if(hasIllum)
		{
			
		}
		if(hasTextureFile)
		{
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			texture.bind();
			GL11.glTexCoord2f(0, 0);
			GL11.glTexCoord2f(0, texture.getHeight());
			GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
			GL11.glTexCoord2f(texture.getWidth(), 0);
		}
		if(hasBumpFile)
		{
			
		}
	}
}
