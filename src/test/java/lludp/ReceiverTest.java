package lludp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.junit.Test;

import lludp.core.Receiver;

public class ReceiverTest {
    /*@Test*/ public void  receiverTest() {
        try {
            boolean o = false;
            DatagramSocket sock = new DatagramSocket(6510);
            Transmitter t = new Transmitter();
        
            
            Log.d("Waiting for packet to arrive");
            DatagramPacket packet =  t.getReceiver().receive(sock);
            Log.d("Recevied packet of length "+ packet.getLength());





        } catch (Exception e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}