package streamvideoaudio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class EnviaMicrofone extends Thread {

    public byte[] buffer;
    private final int PORTA = 50005;
    static AudioInputStream ais;
    public String ENDERECO_IP = "127.0.0.1";

    @Override
    public void run() {
        // tipo DataLine do qual os dados de áudio podem ser lidos
        TargetDataLine dadosDestino;
        DatagramPacket dp;

        // nomeia o tipo específico de representação de dados usado para um fluxo de áudio
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int channels = 2;
        int sampleSize = 16;
        boolean bigEndian = false;
        InetAddress endereco;

        // especifica um arranjo particular de dados em um fluxo de som
        AudioFormat formato = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);

        // constrói o objeto de informações de uma linha de dados a partir das informações especificadas, 
        // que incluem um único formato de áudio
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, formato);
        // indica se o sistema suporta quaisquer linhas que correspondam ao Line.Info objeto especificado
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Linha " + info + " não suportada.");
            return;
        }

        try {
            // obtém uma linha que corresponde à descrição no Line.Infoobjeto especificado
            dadosDestino = (TargetDataLine) AudioSystem.getLine(info);

            // retorna o tamanho real do buffer usado para a resposta
            int buffsize = dadosDestino.getBufferSize() / 5;
            buffsize += 512;

            // abre a linha com o formato especificado, fazendo com que a linha adquira quaisquer 
            // recursos de sistema necessários e fique operacional.
            dadosDestino.open(formato);

            dadosDestino.start();

            int numBytesRead;
            byte[] data = new byte[4096];

            endereco = InetAddress.getByName(ENDERECO_IP);
            DatagramSocket socket = new DatagramSocket();
            while (true) {
                numBytesRead = dadosDestino.read(data, 0, data.length);
                dp = new DatagramPacket(data, data.length, endereco, PORTA);

                socket.send(dp);
            }

        } catch (LineUnavailableException | IOException e) {
            Logger.getLogger(RecebeMicrofone.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
