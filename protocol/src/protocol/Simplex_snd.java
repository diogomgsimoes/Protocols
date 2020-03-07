/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 2 : Simplex Sender protocol which does not receive frames
 * 
 * @author 50236, 50292 and 50732
 */
public class Simplex_snd extends Base_Protocol implements Callbacks {

    public Simplex_snd(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
        frame_expected = 0;
   
    }
    
    /**
     * Fetches the network layer for the next packet and starts it transmission
     * @return true is started data frame transmission, false otherwise
     */
    private boolean send_next_data_packet() {
        // We can only send one Data packet at a time
        //   you must wait for the DATA_END event before transmitting another one
        //   otherwise the first packet is lost in the channel
        String packet= net.from_network_layer();
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(0), 
                    packet);
            sim.to_physical_layer(frame);           
            return true;
        }
        return false;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nSimplex Stop&Wait Protocol - sender\n\n");
        send_next_data_packet();    // Start sending the first packet
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * @param time current simulation time
     * @param seq  sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        
        //Starts the timer to wait for an ACK
        sim.start_data_timer();  
                      
    }
    
    /**
     * CALLBACK FUNCTION: handle the data timer event; retransmit failed frames
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        
        sim.to_physical_layer(frame);  //Retransmit failed frames
      
    }
       
    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        
        sim.Log(time + " protocol Simplex_snd received: " + frame.toString() +" - Acknowledge\n");
        
        if (frame.kind() == Frame.ACK_FRAME) {     // Check the frame kind
            if (frame.ack()== frame_expected) {    // Check the acknowledge number
                frame_expected = next_seq(frame_expected); 
                sim.cancel_data_timer();           //Cancel timer in case of right acknowledge
                
                //Send next data packet
                next_frame_to_send= next_seq(next_frame_to_send);
                send_next_data_packet();
            }
        }
                
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    /* Variables */
    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    /**
     * Sequence number of the next data frame
     */
    private int next_frame_to_send;    
    
    /**
     * Expected sequence number of the next ACK frame received
     */
    private int frame_expected;
    
    /**
     * Frame that was transmitted and will be retransmitted in case of not receiving ACK frame
     */
  
    Frame frame ;
}
