package org.atcs.moonweasel.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;

public class ObjLoader extends Loader 
{
	private static final String EXTENSION = "obj";
	private static final Pattern FACE_PHRASE = Pattern.compile("(\\d+)/?(\\d+)?/?(\\d+)?");
	
	private Material lastMaterial = null;
	
	protected String getExtension() {
		return EXTENSION;
	}
	
	protected Vector[] loadGeometry(String path, String name) throws IOException
	{
		Scanner sc = new Scanner(new File(path + name + ".obj"));
		ArrayList<Vector> vertices = new ArrayList<Vector>();
		
		while(sc.hasNext())
		{
			String nextToken = sc.next();
			if(nextToken.equals("v"))
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				coordinates[2] = sc.nextFloat();
				Vector insertVertex = new Vector(coordinates[0], coordinates[1], coordinates[2]);
				vertices.add(insertVertex);
			}
			
			if(sc.hasNextLine())
			{
				sc.nextLine();
			}
		}
		
		Vector[] array = new Vector[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			array[i] = vertices.get(i);
		}
		return array;
	}
	
	@Override
	protected boolean loadModel(String path, String name) throws IOException
	{
		Map<String, Material> materials = new HashMap<String, Material>();
		lastMaterial = new Material();
		materials.put("(null)", lastMaterial);
		Scanner sc = new Scanner(new File(path + name + ".obj"));
		ArrayList<Vector> vertices = new ArrayList<Vector>();
		vertices.add(null);
		ArrayList<Vector> normals = new ArrayList<Vector>();
		normals.add(null);
		ArrayList<Vector> textures = new ArrayList<Vector>();
		textures.add(null);
		
		while(sc.hasNext())
		{
			String nextToken = sc.next();
			if(nextToken.equals("v"))
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				coordinates[2] = sc.nextFloat();
				Vector insertVertex = new Vector(coordinates[0], coordinates[1], coordinates[2]);
				vertices.add(insertVertex);
			}
			else if(nextToken.equals("vt"))//Texture
			{
				float[] coordinates = new float[2];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				Vector insertVertex = new Vector(coordinates[0], coordinates[1], -1);
				textures.add(insertVertex);
			}
			else if(nextToken.equals("vn"))//Normal (light stuff)
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				coordinates[2] = sc.nextFloat();
				Vector insertVertex = new Vector(coordinates[0], coordinates[1], coordinates[2]);
				normals.add(insertVertex);
			}
			else if(nextToken.equals("f"))//Face
			{
				handleFace(sc, vertices, textures, normals);
			}
			else if(nextToken.equals("usemtl"))
			{
				lastMaterial.disable();
				
				String materialName = sc.next();
				lastMaterial = materials.get(materialName);
				lastMaterial.enable();
			}
			else if(nextToken.equals("mtllib"))
			{
				String mtlFileName = sc.next();
				loadMaterials(materials, path, mtlFileName);
			}
			
			if(sc.hasNextLine())
			{
				sc.nextLine();
			}
		}
		return true;
	}
	
	public void loadMaterials(Map<String, Material> materials, String path, String name) throws IOException
	{
		Scanner sc = new Scanner(new File(path + name));
		Material x = null;
		while(sc.hasNext())
		{
			String nextToken = sc.next();
			if(nextToken.equals("newmtl"))
			{
				String materialName = sc.next();
				x = new Material();
				x.setFilePath(path);
				x.setName(materialName);
				materials.put(materialName, x);
			}
			else if(nextToken.equals("Ka"))
			{
				float[] insertFloats = new float[3];
				insertFloats[0] = sc.nextFloat();
				insertFloats[1] = sc.nextFloat();
				insertFloats[2] = sc.nextFloat();
				x.setAmbient(insertFloats);
			}
			else if(nextToken.equals("Kd"))
			{
				float[] insertFloats = new float[3];
				insertFloats[0] = sc.nextFloat();
				insertFloats[1] = sc.nextFloat();
				insertFloats[2] = sc.nextFloat();
				x.setAmbient(insertFloats);
			}
			else if(nextToken.equals("Ks"))
			{
				float[] insertFloats = new float[3];
				insertFloats[0] = sc.nextFloat();
				insertFloats[1] = sc.nextFloat();
				insertFloats[2] = sc.nextFloat();
				x.setSpecular(insertFloats);
			}
			else if(nextToken.equals("d"))
			{
				float insertFloat = sc.nextFloat();
				x.setD(insertFloat);
			}
			else if(nextToken.equals("Ns"))
			{
				float insertFloat = sc.nextFloat();
				x.setPhong(insertFloat);
			}
			else if(nextToken.equals("Ni"))
			{
				float insertFloat = sc.nextFloat();
				x.setOpticalDensity(insertFloat);
			}
			else if(nextToken.equals("illum"))
			{
				int insertInt = sc.nextInt();
				x.setIllum(insertInt);
			}
			else if(nextToken.equals("map_Kd"))
			{
				String textureFileName = sc.next();
				x.setTextureFileName(textureFileName);
			}
			else if(nextToken.equals("bump"))
			{
				String fileName = sc.next();
				x.setBumpFileName(fileName);
			}
		}
	}
	
	private void handleFace(Scanner sc, List<Vector> vertices, 
			List<Vector> textures, List<Vector> normals) {
		GL11.glBegin(GL11.GL_POLYGON);
			Matcher phrase;
			while (sc.hasNext("\\d+.+")) {
				phrase = FACE_PHRASE.matcher(sc.next());
				phrase.find();
				Vector v = vertices.get(Integer.parseInt(phrase.group(1)));
				Vector vt = null;
				if (phrase.group(2) != null) {
					vt = textures.get(Integer.parseInt(phrase.group(2)));
				}
				Vector vn = null;
				if (phrase.group(3) != null) {
					vn = normals.get(Integer.parseInt(phrase.group(3)));
				}
				
				applyFace(v, vt, vn);
			}
		GL11.glEnd();	
	}
	
	private void applyFace(Vector v, Vector vt, Vector normal) {
		if (vt != null) {
			if (vt.y != Float.NaN && vt.z != Float.NaN) {
				GL11.glTexCoord3f(vt.x, 1 - vt.y, vt.z);
			} else if (vt.y != Float.NaN) {
				GL11.glTexCoord2f(vt.x, 1 - vt.y);
			} else {
				GL11.glTexCoord1f(vt.x);
			}
		}
		
		if (normal != null) {
			GL11.glNormal3f(normal.x, normal.y, normal.z);
		}

		GL11.glVertex3f(v.x, v.y, v.z);
	}
}
