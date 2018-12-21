


package lludp.full;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;

import lludp.Log;
import lludp.Transmitter;

public class Server {
    public void run() {
        try {
            Log.d("Starting server on port 6510");
            DatagramSocket socket = new DatagramSocket(6510);

            Transmitter t = new Transmitter();

            boolean useT = true;

            Thread senderThread = new Thread() {
                @Override
                public void run() {
                    while (true){

                    
                        if ( useT == true)
                        t.getSender().processPendingPackets();
                        
                        try {
                            Thread.sleep(2);
                        } catch (Exception e){};
                    }
                }
            };

            Thread receiverThread = new Thread() {
                @Override
                public void run() {
                    
                    DatagramPacket p;
                    while (true) {
                        try {
                            if ( useT == true )
                            p = t.getReceiver().receive(socket);
                            else {
                                byte[] b = new byte[100];
                                p = new DatagramPacket(b, 100);
                                socket.receive(p);
                            }
                    Log.o(new String(p.getData(),0,p.getLength()));
                    Thread.sleep(3);
                    } catch (Exception e) {};
                }
            }
            };


            senderThread.start();
            receiverThread.start();

        } catch (Exception e) {e.printStackTrace();};


    }
}