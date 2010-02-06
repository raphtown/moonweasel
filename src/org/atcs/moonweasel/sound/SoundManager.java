package org.atcs.moonweasel.sound;

public class SoundManager 
{
	private enum Sounds {
		MUSIC("data/MUSIC/MUSIC.mp3"),
		LASER("data/MUSIC/LASER.mp3"),
		BOOST("data/MUSIC/BOOST.mp3"),
		EXPLOSION("data/MUSIC/EXPLOSION.mp3"),
		TAUNT("data/MUSIC/TAUNT.mp3");
		
		private final String filename;
		private final MP3 effect;
		
		private Sounds(String filename)
		{
			effect = new MP3(filename);
		}
		
		public void play(){
			effect.play();
		}
	}
	
	public void playBackgroundMusic(){
		Sounds.MUSIC.play();
	}
	public void playLaser(){
		Sounds.LASER.play();
	}
	public void playExplosion(){
		Sounds.EXPLOSION.play();
	}
	public void playTAUNT(){
		Sounds.TAUNT.play();
	}
}
