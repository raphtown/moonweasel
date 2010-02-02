package org.atcs.moonweasel.gui;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class ObjLoader extends Loader 
{
	private static final String EXTENSION = "obj";
	
	protected String getExtension() {
		return EXTENSION;
	}
	
	@Override
	protected boolean loadModel(FileInputStream stream, GL2 gl) 
	{
		Scanner sc = new Scanner(stream);
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(null);
		ArrayList<Vertex> normalVertex = new ArrayList<Vertex>();
		normalVertex.add(null);
		ArrayList<Vertex> textureVertex = new ArrayList<Vertex>();
		textureVertex.add(null);
		
		while(sc.hasNext())
		{
			String nextToken = sc.next();
			if(nextToken.equals("v"))
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				coordinates[2] = sc.nextFloat();
				Vertex insertVertex = new Vertex(coordinates[0], coordinates[1], coordinates[2]);
				vertices.add(insertVertex);
			}
			else if(nextToken.equals("vt"))//Texture
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				Vertex insertVertex = new Vertex(coordinates[0], coordinates[1], -1);
				textureVertex.add(insertVertex);
			}
			else if(nextToken.equals("vn"))//Normal (light stuff)
			{
				float[] coordinates = new float[3];
				coordinates[0] = sc.nextFloat();
				coordinates[1] = sc.nextFloat();
				coordinates[2] = sc.nextFloat();
				Vertex insertVertex = new Vertex(coordinates[0], coordinates[1], coordinates[2]);
				normalVertex.add(insertVertex);
			}
			else if(nextToken.equals("f"))//Face
			{
				ArrayList<Integer> faceVertices = new ArrayList<Integer>();
				ArrayList<Integer> textures = new ArrayList<Integer>();
				ArrayList<Integer> normals = new ArrayList<Integer>();
				
				
				ArrayList<String> tokenArray = new ArrayList<String>();
				while(sc.hasNext("\\d+.+"))
				{
					tokenArray.add(sc.next());
				}
				
				int numOfVertices = tokenArray.size();
				
				boolean hasTextures = false;
				boolean hasNormals = false;
				String exampleToken = tokenArray.get(0);
				
				if(exampleToken.indexOf('/') == -1)
				{
					hasTextures = false;
					hasNormals = false;
				}
				else
				{
					exampleToken = exampleToken.substring(exampleToken.indexOf('/') + 1);
					if(exampleToken.indexOf('/') == 0)
					{
						hasTextures = false;
						hasNormals = true;
					}
					else if(exampleToken.indexOf('/') > 0)
					{
						hasTextures = true;
						hasNormals = true;
					}
					else
					{
						return false;
					}
				}
				
				if(!hasTextures && !hasNormals)
				{
					for(int i = 0; i < tokenArray.size(); i++)
					{
						faceVertices.add(Integer.parseInt(tokenArray.get(i)));
					}
				}
				else if(!hasTextures && hasNormals)
				{
					for(int i = 0; i < tokenArray.size(); i++)
					{
						String oneToken = tokenArray.get(i);
						String vertexString = oneToken.substring(0,oneToken.indexOf('/'));
						String normalString = oneToken.substring(oneToken.lastIndexOf('/') + 1);
						
						faceVertices.add(Integer.parseInt(vertexString));
						normals.add(Integer.parseInt(normalString));
					}
				}
				else if(hasTextures && hasNormals)
				{
					for(int i = 0; i < tokenArray.size(); i++)
					{
						String oneToken = tokenArray.get(i);
						String vertexString = oneToken.substring(0,oneToken.indexOf('/'));
						String textureString = oneToken.substring(oneToken.indexOf('/') + 1, oneToken.lastIndexOf('/'));
						String normalString = oneToken.substring(oneToken.lastIndexOf('/') + 1);
						
						faceVertices.add(Integer.parseInt(vertexString));
						textures.add(Integer.parseInt(textureString));
						normals.add(Integer.parseInt(normalString));
					}
				}
				else
				{
					return false;
				}
				
				
				if(numOfVertices < 3)
				{
					System.err.println("Less than 3 vertices specified.");
					return false;
				}
				else if(numOfVertices == 3)
				{
					gl.glBegin(GL.GL_TRIANGLES);
					if(!hasTextures && !hasNormals)
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
					}
					else if(!hasTextures && hasNormals)
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						Vertex normalVertex1 = normalVertex.get(normals.get(0));
						Vertex normalVertex2 = normalVertex.get(normals.get(1));
						Vertex normalVertex3 = normalVertex.get(normals.get(2));
						gl.glNormal3f(normalVertex1.getX(), normalVertex1.getY(), normalVertex1.getZ());
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glNormal3f(normalVertex2.getX(), normalVertex2.getY(), normalVertex2.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glNormal3f(normalVertex3.getX(), normalVertex3.getY(), normalVertex3.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
					}
					else //hasTextures && hasNormals
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						Vertex normalVertex1 = normalVertex.get(normals.get(0));
						Vertex normalVertex2 = normalVertex.get(normals.get(1));
						Vertex normalVertex3 = normalVertex.get(normals.get(2));
						Vertex textureVertex1 = textureVertex.get(textures.get(0));
						Vertex textureVertex2 = textureVertex.get(textures.get(1));
						Vertex textureVertex3 = textureVertex.get(textures.get(2));
						gl.glTexCoord2f(textureVertex1.getX(), textureVertex1.getY());
						gl.glNormal3f(normalVertex1.getX(), normalVertex1.getY(), normalVertex1.getZ());
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glTexCoord2f(textureVertex2.getX(), textureVertex2.getY());
						gl.glNormal3f(normalVertex2.getX(), normalVertex2.getY(), normalVertex2.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glTexCoord2f(textureVertex3.getX(), textureVertex3.getY());
						gl.glNormal3f(normalVertex3.getX(), normalVertex3.getY(), normalVertex3.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
					}
					gl.glEnd();
				}
				else if(numOfVertices == 4)
				{
					System.out.println("working so far");
					gl.glBegin(GL2.GL_QUADS);
					if(!hasTextures && !hasNormals)
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						Vertex vertex4 = vertices.get(faceVertices.get(3));
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
						gl.glVertex3f(vertex4.getX(), vertex4.getY(), vertex4.getZ());
					}
					else if(!hasTextures && hasNormals)
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						Vertex vertex4 = vertices.get(faceVertices.get(3));
						Vertex normalVertex1 = normalVertex.get(normals.get(0));
						Vertex normalVertex2 = normalVertex.get(normals.get(1));
						Vertex normalVertex3 = normalVertex.get(normals.get(2));
						Vertex normalVertex4 = normalVertex.get(normals.get(3));
						gl.glNormal3f(normalVertex1.getX(), normalVertex1.getY(), normalVertex1.getZ());
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glNormal3f(normalVertex2.getX(), normalVertex2.getY(), normalVertex2.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glNormal3f(normalVertex3.getX(), normalVertex3.getY(), normalVertex3.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
						gl.glNormal3f(normalVertex4.getX(), normalVertex4.getY(), normalVertex4.getZ());
						gl.glVertex3f(vertex4.getX(), vertex4.getY(), vertex4.getZ());
						
					}
					else //hasTextures && hasNormals
					{
						Vertex vertex1 = vertices.get(faceVertices.get(0));
						Vertex vertex2 = vertices.get(faceVertices.get(1));
						Vertex vertex3 = vertices.get(faceVertices.get(2));
						Vertex vertex4 = vertices.get(faceVertices.get(3));
						Vertex normalVertex1 = normalVertex.get(normals.get(0));
						Vertex normalVertex2 = normalVertex.get(normals.get(1));
						Vertex normalVertex3 = normalVertex.get(normals.get(2));
						Vertex normalVertex4 = normalVertex.get(normals.get(3));
						Vertex textureVertex1 = textureVertex.get(textures.get(0));
						Vertex textureVertex2 = textureVertex.get(textures.get(1));
						Vertex textureVertex3 = textureVertex.get(textures.get(2));
						Vertex textureVertex4 = textureVertex.get(textures.get(3));
						gl.glTexCoord2f(textureVertex1.getX(), textureVertex1.getY());
						gl.glNormal3f(normalVertex1.getX(), normalVertex1.getY(), normalVertex1.getZ());
						gl.glVertex3f(vertex1.getX(), vertex1.getY(), vertex1.getZ());
						gl.glTexCoord2f(textureVertex2.getX(), textureVertex2.getY());
						gl.glNormal3f(normalVertex2.getX(), normalVertex2.getY(), normalVertex2.getZ());
						gl.glVertex3f(vertex2.getX(), vertex2.getY(), vertex2.getZ());
						gl.glTexCoord2f(textureVertex3.getX(), textureVertex3.getY());
						gl.glNormal3f(normalVertex3.getX(), normalVertex3.getY(), normalVertex3.getZ());
						gl.glVertex3f(vertex3.getX(), vertex3.getY(), vertex3.getZ());
						gl.glTexCoord2f(textureVertex4.getX(), textureVertex4.getY());
						gl.glNormal3f(normalVertex4.getX(), normalVertex4.getY(), normalVertex4.getZ());
						gl.glVertex3f(vertex4.getX(), vertex4.getY(), vertex4.getZ());
					}
					gl.glEnd();
					
				}
				else if(numOfVertices > 4)
				{
					System.err.println("no.");
					return false;
				}
			}
			if(sc.hasNextLine())
			{
				sc.nextLine();
			}
		}
		return true;
	}
}
