
package lludp;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

public class TransmissionTest {

    @Test public void testTransmission() {
        Thread p2ReceivingThread = null;

        try {
            final DatagramSocket p1 = new DatagramSocket(6510);
            final DatagramSocket p2 = new DatagramSocket();
            
            final Transmitter t1 = new Transmitter();
            final Transmitter t2 = new Transmitter();

            Thread p1SendingThread = new Thread() {
                @Override
                public void run() {
                    while ( true )
                        {
                            t1.getSender().processPendingPackets();
                            try {
                                Thread.sleep(10);
                            } catch (Exception e){};
                        }

                }
            };

            Thread p1ReceivingThread = new Thread() {
                @Override
                public void run() {
                    DatagramPacket p = t1.getReceiver().receive(p1);
                    Log.d("[P1] Receied Datagram of length: "+p.getLength());
                    Log.d("[P1] Sending datagram to P2");
                    
                    try {   DatagramPacket p2 = new DatagramPacket("Hello World 2!".getBytes("UTF-8"),14);
                         p2.setSocketAddress(p.getSocketAddress());
                         t1.send(p1, p2, null);
                    } catch (Exception e){};
                }
            };
            Thread p2SendingThread = new Thread() {
                @Override
                public void run() {
                    while ( true )
                        {
                            t2.getSender().processPendingPackets();
                            try {
                                Thread.sleep(10);
                            } catch (Exception e){};
                        }

                }
            };
        

            p2ReceivingThread = new Thread() {
                @Override
                public void run() {
                    DatagramPacket p = t2.getReceiver().receive(p2);
                    Log.d("[P2] Receied Datagram of length: "+p.getLength());
                }
            };
            

            p1SendingThread.start();
            p2SendingThread.start();
            p1ReceivingThread.start();
            p2ReceivingThread.start();


            Log.d("[P2] Sending Packet to P1");

            DatagramPacket p = new DatagramPacket("Hello World!".getBytes("UTF-8"),12); 
            p.setSocketAddress(new InetSocketAddress("127.0.0.1",6510));
            
            t2.send(p2, p, null);
            
            
            p2ReceivingThread.join(1000);

            assertTrue("P2 Receiving Thread should've been closed by now", p2ReceivingThread.isAlive() == false);
        }
        catch (Exception e) {
            e.printStackTrace();;
            assertNotNull(null);
        }
    
    }
}