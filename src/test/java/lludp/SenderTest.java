package lludp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import lludp.core.*;

public class SenderTest {

    @Test public void testSocket() {
        try {
            
            DatagramPacket pack = new DatagramPacket("Hello World!\0".getBytes(StandardCharsets.UTF_8),13);
            DatagramSocket sock = new DatagramSocket(0);
            


            pack.setSocketAddress(new InetSocketAddress("localhost", 33105));

            Log.d("Created transmitter");
            Transmitter t = new Transmitter();

            for (int i = 0; i < 10; i++ ) {
            final long t1 = System.currentTimeMillis();
            t.send(sock, pack, new Sender.PacketSentCallback(){
            
                @Override
                public void onSent() {
                    Log.d("Packet was shoot/sent successully after "+(System.currentTimeMillis() - t1)+" ms");
                }
            });
            }

            t.getSender().processPendingPackets();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}