package lludp.core;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import lludp.Log;


public class Receiver {

    protected Sender sender;
    protected int lastId = -1;
    protected int status = 0;

    protected static final int HEADER_SIZE = 17;

    protected static final int RCV_DELAY = 3;

    public static final int MAX_BUFFER_LENGTH = 2000;
    protected PacketReceivedCallback packetCB;

    protected boolean onGoingReq = false;

    public Receiver() {

    }

    

    /**
     * Blocks until a packet arrives
     * @param sock
     * @return DatagramPacket
     */
    public DatagramPacket receive(DatagramSocket sock) {
        while ( true ) {
            try {
                ByteBuffer buff = ByteBuffer.allocate(MAX_BUFFER_LENGTH);
                DatagramPacket packet = new DatagramPacket(buff.array(),buff.array().length);
                sock.receive(packet);
                buff.limit(packet.getLength());
                Log.d("RCV BUFF LEN: "+packet.getLength());
                int RES = buff.getInt();
                byte type = buff.get();

                if ( type == 1) {
                    Log.d("Received LLUDP Config packet");
                    onLLUDPConfigPacket(packet,buff);
                    continue;
                }
            
                int Id = buff.getInt();
                int SqID = buff.getInt();
                int RES2 = buff.getInt();



                if ( Id != getLastReceivedId() + 1) {
                    // OUT OF ORDER
                    makeResendRequest(sock,getLastReceivedId() + 1,packet.getSocketAddress());
                    status = 1;
                    Log.d("INVALID SQ ID > "+Id+" != "+(getLastReceivedId() + 1));
                    continue;
                }

                if ( SqID != sender.getLastSentId() ) {
                    // Well Not Good :/
                    Log.d("[WARN] Sender's last sent SQID doesn't match receiver's ACK "+SqID+" != "+sender.getLastSentId());

                }

                lastId++;

                byte[] data = new byte[buff.limit() - buff.position()];

                buff.get(data);

                //Log.o(data[0]);
                assert data.length < buff.array().length;
                DatagramPacket dp = new DatagramPacket(data,data.length);
                dp.setSocketAddress(packet.getSocketAddress());
                return dp;
            

            } catch (IOException e) {
                e.printStackTrace();
            }
        } // while loop end

    }

    protected void onLLUDPConfigPacket(DatagramPacket packet,ByteBuffer buff) {
        byte MSGID = buff.get();

        switch (MSGID) {
            case 1 : // RESEND REQUEST
                int offsetSeq =  buff.getInt();
                sender.resetSequence(offsetSeq);
                Log.d("Resend request from SQID " + offsetSeq +" - processing");
                break;
        }
    }


    protected void makeResendRequest(DatagramSocket sock,int offset,SocketAddress addr) {
        if (onGoingReq == true) return;
        new Timer().schedule(new TimerTask(){
        
            @Override
            public void run() {
                sendResendRequest(sock,offset,addr);
                onGoingReq = false;
            }
        }, 300); // Send resend request after 300 ms
    }

    protected void sendResendRequest(DatagramSocket sock,int offset,SocketAddress addr){
        ByteBuffer b = ByteBuffer.allocate( 10 );

        b.putInt(0); // Reserved
        b.put((byte)1); // LLUDP CONFIG PACKET - Type
        b.put((byte)1); // RESEND_FROM - Config Id
        b.putInt(offset); // Offset

        b.flip();

        DatagramPacket pack = new DatagramPacket(b.array(), b.limit());
        pack.setSocketAddress(addr);
        try {
            sock.send(pack);
        } catch (IOException e){};

    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public int getLastReceivedId() {
        return lastId;
    }

    public int getStatus() {
        return status;
    }


    public static abstract class PacketReceivedCallback {
        public abstract void onPacketReceived(SocketAddress addr, ByteBuffer d);
    }
}