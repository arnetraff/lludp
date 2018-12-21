

package lludp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import lludp.core.*;


/**
 * Basic class to send and receive datagrams, you can also supply your own
 * sender and receiver object
 */
public class Transmitter 
{
    
    protected Sender sender;
    protected Receiver receiver;

    /**
     * Create new sender and receiver objects
     */
    public Transmitter() {
        sender = new Sender();
        receiver = new Receiver();


        sender.setReceiver(receiver);
        receiver.setSender(sender);
    }
    /**
     * Use supplied sender and receiver
     * @param sender Sender object to use in transmission
     * @param receiver Receiver object to use in transmission
     */
    public Transmitter(Sender sender,Receiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }


    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void steReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Sender getSender() {
        return sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }


    public void send(DatagramSocket sock,DatagramPacket pack,Sender.PacketSentCallback cb) {
        sender.send(sock, pack, cb);
    }

    
    
}