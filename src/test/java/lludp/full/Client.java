
package lludp.full;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.junit.Test;

import lludp.Log;
import lludp.Transmitter;
import lludp.core.Sender;

public class Client {
    public void run() {
    
        try {

            boolean useT = true;


            Log.d("Starting client");
            DatagramSocket socket = new DatagramSocket();

            Transmitter t = new Transmitter();


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
                    Log.d(new String(p.getData()));
                    Thread.sleep(3);
                    } catch (Exception e) {};
                }
            }
            };


            senderThread.start();
            receiverThread.start();

            for (int i = 0; i < 50; i++) {
                String s = "";
                for (int x = 0; x < 10; x++) {
                    s += "-";
                    DatagramPacket p = new DatagramPacket(s.getBytes("UTF-8"), s.getBytes("UTF-8").length);
                    p.setSocketAddress(new InetSocketAddress("127.0.0.1",6510));
                   if ( useT)
                    t.send(socket, p, null);
                    else socket.send(p);

                    //Log.d(s);
                    i++;
                    Thread.sleep(200);
                }

            }
            
            

            receiverThread.join();

        } catch (Exception e) {}       

    }
}