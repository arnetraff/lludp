package lludp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;


import lludp.Log;


public class Sender {

    protected List<QueuedPacket> toSendList = new LinkedList<QueuedPacket>();
    protected Receiver receiver = null;
    
    protected int SeqId = 0;
    protected int SeqOffset = 0; 
    
    protected int SEND_DELAY = 3; // In milli second

    public int resentPackets = 0;

    public final static int HEADER_SIZE = 17;
    public final static int MAX_QUEUE_LENGTH = 5000;
    
    public Sender() {
        
        assert toSendList.size() == 0;
    }

    public void setReceiver(Receiver rc) {
        this.receiver = rc;
    }

    public void resetSequence(int offset) {
        resentPackets += SeqId - offset;
        
        SeqId = offset;
        
        Log.d("Reseting Sequence: "+SeqId+" -> "+offset);
    }


    /**
     * Add Datagram packet to sending queue
     * @param sock Socket is to use
     * @param pack datagram packet to use
     */
    public void send(DatagramSocket sock, DatagramPacket pack,PacketSentCallback cb) {
        
        
        QueuedPacket p = new QueuedPacket(sock, pack, cb);
        
        toSendList.add(p);
        Log.d("Adding packet to queue");
        trimList();
    }

    /**
     * Remove refrences to old packets
     */
    protected void trimList() {
        
        while (this.toSendList.size() > MAX_QUEUE_LENGTH ) {
            toSendList.remove(0); // Remove the head / First / Oldest packet   
            SeqOffset++; // Increament Sequence Offset 
        }
    }

    /**
     * Send all pending packets
     */
    public void processPendingPackets() {

        if ( toSendList.size() == 0 )
            return;


        QueuedPacket p = null;

        /**
         * SeqOffset offsets the list index because not all SeqId indexes exists older packets are deleted from index
         */
        while ( ( ( SeqId - SeqOffset ) < toSendList.size() ) && // Taking advantage from short-ciruit evaluation
                ( p = toSendList.get( ( SeqId - SeqOffset ) ) ) != null ) {
            try
            {
                
                if ( receiver.getStatus() != 0 ) { // IF status is NOT_OK

                    Thread.sleep(SEND_DELAY);
                    continue;

                }

                p.setId( SeqId++ ); // Assign and Increament sequence Id
                sendPacket(p);
            
                Thread.sleep( SEND_DELAY );
            }
            catch (Exception e){  // Although not expected
                e.printStackTrace();
                break;
            }; 
        }
    }

    /**
     * Send a queued packet, it will be send to source datagram distination using source socket of packet
     * @param p Packet to send
     */

    protected void sendPacket(QueuedPacket p) {

        DatagramPacket pack = p.getDatagramPacket();
        
        // Create a copy of packet, b'coz we don't want to modify original
        // packet ( like attaching LLUDP Header ) as user may want send it again
        DatagramPacket clonedPack = new DatagramPacket(pack.getData().clone(), pack.getData().length);
        clonedPack.setSocketAddress(pack.getSocketAddress());
        

        ByteBuffer b = ByteBuffer.allocate(HEADER_SIZE + clonedPack.getLength()); // HEADER_SIZE

        b.putInt(0); // Reserved
        b.put((byte)0); // Type
        b.putInt(p.getId());
        b.putInt(receiver.getLastReceivedId());
        b.putInt(0); // Reserved

        b.put(clonedPack.getData());

        b.flip();

        clonedPack.setData(b.array());
        
        try {
            Log.d("Sending datagram of length: " + clonedPack.getLength());
            p.getSocket().send(clonedPack);
            
            Log.d("Sent datagram with Id: " + p.getId());

            if ( p.getCallback() != null )
                p.getCallback().onSent();


        } catch (IOException e) {

            Log.d("Failed to send datagram with Id: " + p.getId()+" - " + e.getMessage()+"\n");
            e.printStackTrace();


            if ( p.getCallback() != null )
                p.getCallback().onFailedSent(e);
        }
        
    }

    
    public int getLastSentId() {
        return SeqId - 1;
    }


    public static abstract class PacketSentCallback {
        /**
         * This method invokes when a packet is sent there is no gurantee that this packet will approache
         * to distination or not
         */
        public abstract void onSent();
        public void onFailedSent(Exception e){};
        
    }
}

