package org.atcs.moonweasel.sound;

public class soundManager 
{
	private enum sounds {
		
		music("data/music/music.mp3"),
		laser("data/music/laser.mp3"),
		boost("data/music/boost.mp3"),
		explosion("data/music/explosion.mp3"),
		taunt("data/music/taunt.mp3");
		
		String filename;
		MP3 effect;
		private sounds(String filename)
		{
			effect = new MP3(filename);
		}
		public void play(){
			effect.play();
		}
		
	}
	
	public void playBackgroundMusic(){
		sounds.music.play();
	}
	public void playLaser(){
		sounds.laser.play();
	}
	public void playExplosion(){
		sounds.explosion.play();
	}
	public void playtaunt(){
		sounds.taunt.play();
	}
}
