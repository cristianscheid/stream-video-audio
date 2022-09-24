package streamvideoaudio;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EnviaImagem extends Thread {

    @Override
    public void run() {
        // buffer para armazenar o bloco da tela
        byte buffer[] = new byte[Util.BLOCK_X * Util.BLOCK_Y * 4 + 4 + 4]; // pegar R, G, B, e alfa para cada pixel + 4 pois quero informar o posX e  + 4 posY (posicão sorteada)

        try {

            Robot robot = new Robot();
            DatagramSocket senderSocket = new DatagramSocket();
            InetAddress ipDestino = InetAddress.getByName("127.0.0.1"); // destinatário

            while (true) {

                try {

                    BufferedImage bi = robot.createScreenCapture(new Rectangle(Util.RESOLUCAO_X, Util.RESOLUCAO_Y)); // capturei a tela toda

                    int iMaxSize = Util.RESOLUCAO_X / Util.BLOCK_X;
                    int kMaxSize = Util.RESOLUCAO_Y / Util.BLOCK_Y;
                    for (int i = 0; i < iMaxSize; i++) {
                        for (int k = 0; k < kMaxSize; k++) {

                            int posX = Util.BLOCK_X * i;
                            int posY = Util.BLOCK_Y * k;

                            int aux = 0;
                            for (int y = 0; y < Util.BLOCK_Y; y++) {
                                for (int x = 0; x < Util.BLOCK_X; x++) {

                                    int cor = bi.getRGB(posX + x, posY + y); //ARGB

                                    byte auxBuffer[] = Util.integerToBytes(cor);
                                    for (int j = 0; j < auxBuffer.length; j++) {
                                        buffer[aux++] = auxBuffer[j];
                                    }
                                }
                            }

                            // bytes do posX
                            byte auxBufferPosX[] = Util.integerToBytes(posX);
                            for (int j = 0; j < auxBufferPosX.length; j++) {
                                buffer[aux++] = auxBufferPosX[j];
                            }

                            // bytes do posY
                            byte auxBufferPosY[] = Util.integerToBytes(posY);
                            for (int j = 0; j < auxBufferPosY.length; j++) {
                                buffer[aux++] = auxBufferPosY[j];
                            }

                            DatagramPacket enviaPacote = new DatagramPacket(buffer, buffer.length, ipDestino, Util.PORTA);
                            senderSocket.send(enviaPacote);

//                            Thread.sleep(10);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
