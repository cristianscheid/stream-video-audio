package streamvideoaudio;

import java.io.IOException;

public class Recebe {
    public static void main(String[] args) throws IOException {
        new RecebeImagem().run();
        new RecebeMicrofone().start();
    }
}
