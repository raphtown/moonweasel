package org.atcs.moonweasel.gui;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class Material 
{
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
		texture = TextureIO.newTexture(new File(filePath + fileNameIn), false);
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
	public void applyMaterial(GL2 gl)
	{
		if(hasAmbient)
		{
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, ambient, 0);
		}
		if(hasDiffuse)
		{
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);
		}
		if(hasSpecular)
		{
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specular, 0);
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
			texture.enable();
			texture.bind();
		}
		if(hasBumpFile)
		{
			
		}
	}
}
