/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 3 : Stop & Wait protocol
 * 
 * @author 50236, 50292 and 50732
 */
public class StopWait extends Base_Protocol implements Callbacks {

    public StopWait(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
        frame_expected = 0;
        
    }
    
    /**
     * Fetches the network layer for the next packet and starts it transmission
     * @return true is started data frame transmission, false otherwise
     */
    private boolean send_next_data_packet() {
        //   We can only send one Data packet at a time
        //   you must wait for the DATA_END event before transmitting another one
        //   otherwise the first packet is lost in the channel
        packet = net.from_network_layer();
        if (packet != null) {
            
            if(sim.isactive_ack_timer()){       
                sim.cancel_ack_timer();
                Frame frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
                sim.to_physical_layer(frame);  
                return true;
            }
            
            else{
                Frame frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
                sim.to_physical_layer(frame);       
                return true;      
            }
        }
        return false;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nStop&Wait Protocol\n\n");
        send_next_data_packet();
        
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
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        
        Frame frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
        sim.to_physical_layer(frame);  //Retransmit failed frames
        
    }
    
    /**
     * CALLBACK FUNCTION: handle the ACK timer event; send ACK frame
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        
        Frame ack = Frame.new_Ack_Frame(prev_seq(frame_expected)); //Create ACK frame
        sim.to_physical_layer(ack);                                //Send ACK frame
        
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        
        if (frame.kind() == Frame.DATA_FRAME) {                     // Check if it is a DATA frame
            if (frame.seq() == frame_expected) {                    // Check the sequence number
                net.to_network_layer(frame.info());
                frame_expected = next_seq(frame_expected);
               
            }
                
            if (frame.ack() == next_frame_to_send){ 
                sim.cancel_data_timer();                            //Cancel timer in case of right acknowledge
                next_frame_to_send = next_seq(next_frame_to_send);
                send_next_data_packet();
            }
       
            sim.start_ack_timer();                                  //Start ACK Timer
               
        }
                     
        if (frame.kind() == Frame.ACK_FRAME) {                      //Check if it is an ACK frame
            if (frame.ack()== next_frame_to_send) {                 //Check the acknowledge number
                sim.cancel_data_timer();                            //Cancel timer in case of right acknowledge
                next_frame_to_send= next_seq(next_frame_to_send);   //Send next data packet
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
     * Expected sequence number of the next data frame received
     */
    private int frame_expected;
    
    /**
     * Packet that was transmitted and will be retransmitted in case of not receiving ACK frame
     */
    String packet ;
 
}
