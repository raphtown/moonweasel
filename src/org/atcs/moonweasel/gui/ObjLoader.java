package org.atcs.moonweasel.gui;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.util.Vector;

public class ObjLoader extends Loader 
{
	private static final String EXTENSION = "obj";
	private static final Pattern FACE_PHRASE = Pattern.compile("(\\d+)/?(\\d+)?/?(\\d+)?");
	
	protected String getExtension() {
		return EXTENSION;
	}
	
	@Override
	protected boolean loadModel(FileInputStream stream, GL2 gl) 
	{
		Scanner sc = new Scanner(stream);
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
				float[] coordinates = new float[3];
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
				handleFace(sc, gl, vertices, textures, normals);
			}
			
			if(sc.hasNextLine())
			{
				sc.nextLine();
			}
		}
		return true;
	}
	
	private void handleFace(Scanner sc, GL2 gl, List<Vector> vertices, 
			List<Vector> textures, List<Vector> normals) {
		gl.glBegin(GL2.GL_POLYGON);
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
				applyFace(gl, v, vt, vn);
			}
		gl.glEnd();	
	}
	
	private void applyFace(GL2 gl, Vector v, Vector vt, Vector normal) {
		if (vt != null) {
			if (vt.y != Float.NaN && vt.z != Float.NaN) {
				gl.glTexCoord3f(vt.x, vt.y, vt.z);
			} else if (vt.y != Float.NaN) {
				gl.glTexCoord2f(vt.x, vt.y);
			} else {
				gl.glTexCoord1f(vt.x);
			}
		}
		
		if (normal != null) {
			gl.glNormal3f(normal.x, normal.y, normal.z);
		}

		gl.glVertex3f(v.x, v.y, v.z);
	}
}
