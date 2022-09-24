/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package streamvideoaudio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author daniela.silva6
 */
public class RecebeMicrofone extends Thread {

    AudioInputStream audioInputStream;
    static AudioInputStream ais;
    static AudioFormat FORMATO;
    static boolean STATUS = true;
    static int PORTA = 50005;

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public void toSpeaker(byte soundbytes[]) {
        try {
            System.out.println("Falando");
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        } catch (Exception e) {
            System.out.println("Há um problema com o microfone");
        }
    }

    @Override
    public void run() {

        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(PORTA);
        } catch (SocketException ex) {
            Logger.getLogger(RecebeMicrofone.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        byte[] receiveData = new byte[4096];

        FORMATO = new AudioFormat(44100, 16, 2, true, false);
        dataLineInfo = new DataLine.Info(SourceDataLine.class, FORMATO);
        
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(RecebeMicrofone.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sourceDataLine.open(FORMATO);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(RecebeMicrofone.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sourceDataLine.start();

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());

        while (STATUS == true) {
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(RecebeMicrofone.class.getName()).log(Level.SEVERE, null, ex);
            }
            ais = new AudioInputStream(baiss, FORMATO, receivePacket.getLength());
            toSpeaker(receivePacket.getData());
        }

        sourceDataLine.drain();
        sourceDataLine.close();
    }
}
