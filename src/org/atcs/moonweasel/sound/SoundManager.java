package org.atcs.moonweasel.sound;

public class SoundManager 
{
	public enum Sounds {
		MUSIC("data/music/music.mp3"),
		LASER("data/music/laser.mp3"),
		BOOST("data/music/boost.mp3"),
		EXPLOSION("data/music/explosion.mp3"),
		TAUNT("data/music/taunt.mp3");
		
		private Sounds(String filename)
		{
		}
		
		public void play(){
		}
	}
}
