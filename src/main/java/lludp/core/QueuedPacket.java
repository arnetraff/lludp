package lludp.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class QueuedPacket {

    protected int status = 0;
    protected int Id;

    protected DatagramSocket sock;
    protected DatagramPacket pack;
    
    protected Sender.PacketSentCallback cb;

    public QueuedPacket(DatagramSocket sock, DatagramPacket pack,Sender.PacketSentCallback cb) {
        this.sock = sock;
        this.pack = pack;
        this.cb = cb;
    }

    public Sender.PacketSentCallback getCallback() {
        return cb;
    }
    public int getId() {
        return Id;
    }

    public int getStatus() {
        return status;
    }
    public void setId(int Id) {
        this.Id = Id;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public DatagramSocket getSocket() {
        return sock;
    }
    public DatagramPacket getDatagramPacket() {
        return pack;
    }

    public void setSocket(DatagramSocket sock) {
        this.sock = sock;
    }

    public void setDatagramPacket(DatagramPacket pack){
        this.pack = pack;
    }

    
}