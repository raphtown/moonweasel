package org.atcs.moonweasel.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class SimpleMidiLoader 
{
	final String SOUND_ROOT_DIR = "data/music/";
	String fnm;
	public SimpleMidiLoader(String filename)
	{
		fnm = filename;
	}

	public void playMidi()
	{
		try {
			// From file
			Sequence sequence = MidiSystem.getSequence(new File(""+SOUND_ROOT_DIR+fnm));


			// Create a sequencer for the sequence
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);

			// Start playing
			sequencer.start();
		} 
		catch(IOException E)
		{
			
		} 
		catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
