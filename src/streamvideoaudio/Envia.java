package streamvideoaudio;

import java.io.IOException;

public class Envia {
    public static void main(String[] args) throws IOException {
        new EnviaImagem().start();
        new EnviaMicrofone().start();
    }
}