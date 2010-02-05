package org.atcs.moonweasel.sound;

public class SoundManager 
{
	private enum Sounds {
		
		music("data/music/music.mp3"),
		laser("data/music/laser.mp3"),
		boost("data/music/boost.mp3"),
		explosion("data/music/explosion.mp3"),
		taunt("data/music/taunt.mp3");
		
		String filename;
		MP3 effect;
		private Sounds(String filename)
		{
			effect = new MP3(filename);
		}
		public void play(){
			effect.play();
		}
		
	}
	
	public void playBackgroundMusic(){
		Sounds.music.play();
	}
	public void playLaser(){
		Sounds.laser.play();
	}
	public void playExplosion(){
		Sounds.explosion.play();
	}
	public void playtaunt(){
		Sounds.taunt.play();
	}
}
