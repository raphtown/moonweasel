package org.atcs.moonweasel.sound;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;


public class MP3 {
    private String filename="data/music/music.mp3";
    private Player player; 

    // constructor that takes the name of an MP3 file
    public MP3(String filename) 
    {
    	
    	this.filename = filename;

    }

    public void close() { if (player != null) player.close(); }

    // play the MP3 file to the sound card
    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
            
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { player.play(); }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();




    }


    // test client
    public static void main(String[] args) {
 
        String filename = "data/music/laser.mp3";
        MP3 mp3 = new MP3(filename);
        mp3.play();

        // do whatever computation you like, while music plays
   

        // when the computation is done, stop playing it
      

        // play from the beginning
      

    }

}

