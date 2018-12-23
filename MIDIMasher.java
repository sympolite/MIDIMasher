
package midimasher;

import javax.sound.midi.*;
import java.io.*;
import java.util.*;

/**
 * Takes two user-selected MIDI <code>Sequences</code> 
 * and replaces <code>MidiEvent</code> data 
 * from the first with <code>MidiEvent</code> data
 * at random points in the first <code>Sequence</code>.
 *
 * @author  Aia-Jean Anderson
 * @version 1.0, 2/21/2017
 * @since   1.0
 */
public class MIDIMasher {
    
    public static Scanner scanner = new Scanner(System.in);
    public static Sequencer sequencer;
    public static Sequence seq1;
    public static Sequence seq2;
    public static File file1; 
    public static File file2;
    public static File[] path;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("==============================================\n"+
                            "~*~MIDI MASHER~*~\n" +
                            "Version:\t1.0\n"+
                            "Author:\tAia-Jean Anderson\n"+
                            "sympolite.tumblr.com\n" +
                            "==============================================\n");
        loadFiles();
        
        String keyboard;
        String input = "";
        
        System.out.println("Type \"Help\" for a list of commands.");
        
        while (!input.equalsIgnoreCase("exit")) {
            keyboard = scanner.next();
            input = keyboard.toLowerCase().trim();
            
            switch (input) {
                case "exit":
                    sequencer.close();
                    System.out.println("==============================================\n"+
                            "~*~MIDI MASHER~*~\n" +
                            "Version:\t1.0\n"+
                            "Author:\tAia-Jean Anderson\n"+
                            "sympolite.tumblr.com\n" +
                            "==============================================\n");
                    System.exit(0);
                    break;
                case "help":
                    printHelp();
                    break;
                case "load":
                    loadFiles();
                    break;
                case "loop":
                    setLoop();
                    break;
                case "mash":
                    randomize();
                    break;
                case "play":
                    sequencer.setMicrosecondPosition(0);
                    sequencer.start();
                    break;
                case "show":
                    printFiles();
                    break;
                case "stop":
                    sequencer.stop();
                    sequencer.setMicrosecondPosition(0);
                    break;
                default:
                    System.out.println(input + " is not a valid command.");
                    System.out.println("Type \"Help\" for a list of commands.");
            }
            
            System.out.print("\n");
        }
    
    }//end main
    
    /**
     * Deletes <code>MidiEvent</code> objects from the first MIDI file and then,
     * based on the user-provided weight (chance) of swapping,
     * takes <code>MidiEvent</code> data from the second MIDI file and
     * places it into the first at random points.
     * 
     * 
     * @param seq1 the first MIDI file, from which data is removed and replaced
     * @param seq2 the second MIDI file, from which data is copied to <code>seq1</code>
     * @param weight the chance that data from <code>seq2</code> is put into <code>seq1</code>
     * @see Sequence
     * @see Track
     * @see MidiEvent
     * @since 1.0
     */
    private static void doSwap(Sequence seq1, Sequence seq2, int weight) {
        Track[] tracks1 = seq1.getTracks();
        Track[] tracks2 = seq2.getTracks();
        
        //checks for which MIDI file has the fewest tracks,
        //and sets maxTracks to that number
        int maxTracks;
        
        if (tracks1.length <= tracks2.length) {
            maxTracks = tracks1.length;
        }
        else maxTracks = tracks2.length;
        
        //BEGIN FOR LOOP: SWAPPING MIDI DATA AT RANDOM
        for (int i = 0; i < maxTracks; i++) {
            Track selectedTrack1 = tracks1[i];
            Track selectedTrack2 = tracks2[i];
            
            int maxTrackSize;
            
            //checks for which MIDI track has the fewest events,
            //and sets maxTrackSize to that number
            if (selectedTrack1.size() <= selectedTrack2.size()) {
                maxTrackSize = selectedTrack1.size();
            }
            else maxTrackSize = selectedTrack2.size();
            
            //the swap
            for (int j = 0; j < maxTrackSize; j++) {
                double coinFlip = Math.random();
                
                if (coinFlip > 1.0-(weight/100.0)) {
                    MidiEvent swapOut = selectedTrack1.get(j);
                    MidiEvent swapIn = selectedTrack2.get(j);
                    swapIn.setTick(swapOut.getTick());
                    
                    selectedTrack1.remove(swapOut);
                    selectedTrack1.add(swapIn);
                }
            }//end for
        }//end for
    }//end doSwap
    
    private static void randomize() {
        if (sequencer != null) {
            if (sequencer.isRunning()) sequencer.stop();
        }
        int weight = 1;
        boolean fail2 = true;
        while (fail2 == true) {
            try {
                do {
                    System.out.print("Please select the mash weight (between 1 and 100): ");
                    weight = scanner.nextInt();
                    System.out.println();
                } while (weight > 100 || weight < 1);
                fail2 = false;
            }
            catch (InputMismatchException imex) {
            System.out.println("Input must be an integer!");
            fail2 = true;
            }
        }    
        doSwap(seq1, seq2, weight);
        System.out.println("Mashing complete! Type \"play\" to hear the result!");
    }
    
    private static void printFiles() {
        
        path = new File("./MIDI").listFiles();
        int numFiles = (path.length-1);
        
        System.out.println("FILES IN .\\MIDI\\:");
        for (int i = 0; i < path.length; i++) {
            System.out.println(i + ") " + path[i]);
        }
    }
    
    /**
     * Loads the two MIDI files into the program's memory.
     * 
     * @since 1.0
     */
    private static void loadFiles() {
        printFiles();
        
        int selector1 = 0;
        int selector2 = 0;
        boolean fail = true;
        if (sequencer != null) {
            if (sequencer.isRunning()) sequencer.stop();
        }
        
        while (fail == true) {
            try {
                
                do {
                    System.out.print("Please select the first file: ");
                    selector1 = scanner.nextInt();
                    System.out.print("\n");
                } while (selector1 < 0 || selector1 >= path.length);
        
                do {
                    System.out.print("Please select the second file: ");
                    selector2 = scanner.nextInt();
                    System.out.print("\n");
                } while (selector2 < 0 || selector2 >= path.length); 
                
                fail = false;
            }    
            catch (InputMismatchException imex) {
                System.out.println("Input must be an integer!");
                scanner.next();
            }
        }
        
        
        file1 = path[selector1];
        file2 = path[selector2];

        try {
        seq1 = MidiSystem.getSequence(file1);
        seq2 = MidiSystem.getSequence(file2);
        
        sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(seq1);
        
        }
        catch (FileNotFoundException fnfex) {
            System.out.println("Uh-Oh! File has not been found!");
        }
         catch (IOException ioex) {
            System.out.println("Uh-Oh! File has not been found!");
        }
        catch (InvalidMidiDataException imdex) {
            System.out.println("Uh-Oh! Invalid Data!");
        }
        catch (MidiUnavailableException muaex) {
            System.out.println("Uh-Oh! Midi Unavailable!");
        }
        System.out.println(file1.getName() + " and " + file2.getName() +
                " have been loaded and selected.");
    }

    /**
     * Prints a list of commands in the console.
     */
    private static void printHelp() {
        System.out.println("HELP WITH COMMANDS: \n" +
                "[Commands are not case-sensitive.]\n" +
                "exit\tExits the program.\n" +
                "help\tOpens the list of commands.\n" +
                "load\tloads two MIDI files.\n"+
                "loop\tTurns looping on or off. (Off by default)\n"+
                "mash\tMashes the two MIDI files together.\n" +
                "play\tPlays the first of the two MIDI files*.\n"+
                "show\tShows the MIDI files in the MIDI folder.\n"+
                "stop\tStops playback of the MIDI file.\n\n" +
                "*The first MIDI file is the one that is altered to give " +
                "the \"mashed-up\" result.\n" +
                "_____________________________________________________________"
                );
    }

    /**
     * Toggles the looping sequence.
     */
    private static void setLoop() {
        if (sequencer.getLoopCount() == 0) {
            sequencer.setLoopCount(1000000); //yes, one million times.
            System.out.println("Looping is ON.");
        }
        else {
            sequencer.setLoopCount(0);
            System.out.println("Looping is OFF.");
        }
    }
        
}//end MIDIMasher
