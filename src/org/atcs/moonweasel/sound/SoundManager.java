package org.atcs.moonweasel.sound;

public class SoundManager
{
	private enum Sounds {
		MUSIC("data/music/music.mp3"),
		LASER("data/music/laser.mp3"),
		BOOST("data/music/boost.mp3"),
		EXPLOSION("data/music/explosion.mp3"),
		TAUNT("data/music/taunt.mp3");

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
	public void playTaunt(){
		Sounds.TAUNT.play();
	}
}