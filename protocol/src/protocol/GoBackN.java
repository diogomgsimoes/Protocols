/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package protocol;

import java.util.Objects;
import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 4 : Go-back-N protocol with one timer
 *
 * @author 50236, 50292 and 50732
 */
public class GoBackN extends Base_Protocol implements Callbacks {

    public GoBackN(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        packets_from_network_layer = new String[sim.get_max_sequence()+1];
        counter = sim.get_send_window();
        next_frame_to_send = 0;
        last_frame_not_confirmed = 0;
        frame_expected = 0;
        ack_expected = 0;
        retransmissionCounter = 0;
        nak_sent = false;
                
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nGo-Back-N Protocol\n\n");
        send_next_data_packet();    // Start sending the starter packets
        
    }
    
    /**
     * Fetches the network layer for the next packets and starts it transmission
     * @return true is started data frame transmission, false otherwise
     */
    private boolean send_next_data_packet() {
        
        String packet = net.from_network_layer();
        
        if (packet != null) {
            
            last_packet_sent = packet;
            packets_from_network_layer[next_frame_to_send] = packet;
            
            sim.cancel_ack_timer();          
            Frame frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
            
            sim.to_physical_layer(frame);
            next_frame_to_send = next_seq(next_frame_to_send);
            
            return true;
              
        } else{
            
            return false;
        }    
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * and send next until reaching the end of the sending window.
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        
        //Starts the timer to wait for an ACK
        
        if(!sim.isactive_data_timer())
                sim.start_data_timer(); 
        
        if(retransmissionCounter == 0){

            if(counter > 1){         //Counter equal to 1 means last packet to send
               counter--;
               send_next_data_packet();
            }
            else
                counter = sim.get_send_window();
        
        }else {
            if(retransmissionCounter > 1){

                    String packet = packets_from_network_layer[next_frame_to_send];
                    last_packet_sent = packet;
                    Frame data_frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
                    next_frame_to_send = next_seq(next_frame_to_send);
                    retransmissionCounter--;
                    sim.to_physical_layer(data_frame);
            }else {

                retransmissionCounter = 0;

                if(counter == 0)
                    counter = sim.get_send_window();
                else
                    send_next_data_packet();
                
                }  
            }  
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames.
     *
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        
        retransmissionCounter = diff_seq(last_frame_not_confirmed, next_frame_to_send);
        int index = 0;
        
        for(int i = 0; i < sim.get_max_sequence()+1 ; i++){
            if (Objects.equals(packets_from_network_layer[i], last_packet_sent)){
                index = i;
                break;
            }
        }
        
        int i = retransmissionCounter;
        
         while(i>1){
             
             index = prev_seq(index);
             i--;
        }
        //Now we know the packet that failed
        
        next_frame_to_send = index;
        ack_expected = index;
        
        counter=sim.get_send_window()-retransmissionCounter;
      
        String packet = packets_from_network_layer[next_frame_to_send];
        last_packet_sent = packet;

        sim.cancel_ack_timer();    
        Frame data_frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
        next_frame_to_send = next_seq(next_frame_to_send);
        sim.to_physical_layer(data_frame);
 
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        
        Frame ack = Frame.new_Ack_Frame(prev_seq(frame_expected)); //Create ACK frame
        sim.to_physical_layer(ack);  
        
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical
     * layer
     *
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        
        if (frame.kind() == Frame.DATA_FRAME) {                     // Check if it is a DATA frame
            if (frame.seq() == frame_expected) {                    // Check the sequence number
                nak_sent = false;
                net.to_network_layer(frame.info());
                frame_expected = next_seq(frame_expected);
                sim.start_ack_timer();                                  //Start ACK Timer
               
            }
            else{
                if (!nak_sent){
                    nak_sent = true;
                    Frame nak = Frame.new_Nak_Frame(frame_expected);
                    sim.to_physical_layer(nak);
                    
                }
                else{
                    Frame ack = Frame.new_Ack_Frame(prev_seq(frame_expected));
                    sim.to_physical_layer(ack);
                }
                    
            }
            
            if(between(ack_expected, frame.ack(), next_frame_to_send)) {       //Check the acknowledge number
                
                sim.cancel_data_timer();
                ack_expected = frame.ack();
                last_frame_not_confirmed = ack_expected;
                
                if (prev_seq(next_frame_to_send) == last_frame_not_confirmed){    //When the send window is confirmed
                    send_next_data_packet();
                       
                }
                else {
                    sim.start_data_timer();
                    ack_expected = next_seq(ack_expected);
                    counter = sim.get_send_window()-diff_seq(ack_expected, next_frame_to_send);
                    last_frame_not_confirmed = ack_expected;
                    if (counter>0)
                        send_next_data_packet();             //Sends the last packet of new window
                
                }         
            }
        }
                     
        if (frame.kind() == Frame.ACK_FRAME) {         //Check if it is an ACK frame
            
            if(between(ack_expected, frame.ack(), next_frame_to_send)){ 
                
                sim.cancel_data_timer();
                ack_expected = frame.ack();
                last_frame_not_confirmed = ack_expected;
                
                if (prev_seq(next_frame_to_send) == last_frame_not_confirmed){    //When the send window is confirmed
                    send_next_data_packet();
                       
                }
                else {
                    sim.start_data_timer();
                    ack_expected = next_seq(ack_expected);
                    counter = sim.get_send_window()-diff_seq(ack_expected, next_frame_to_send);
                    last_frame_not_confirmed = ack_expected;
                    send_next_data_packet();             //Sends the last packet of new window
                        
                }         
            }
        }
        
        if (frame.kind() == Frame.NAK_FRAME) {
            
            sim.cancel_data_timer();           //Cancel data timer
            
            int failed_packet = frame.ack();
            retransmissionCounter = diff_seq(failed_packet, next_frame_to_send);
            if(failed_packet==next_frame_to_send)
                send_next_data_packet();
            else{
            
            next_frame_to_send = failed_packet;
            last_frame_not_confirmed = failed_packet;
            ack_expected = failed_packet;
            
            //Now we know the packet that failed

            counter=sim.get_send_window()-retransmissionCounter;

            String packet = packets_from_network_layer[next_frame_to_send];
            last_packet_sent = packet;

            sim.cancel_ack_timer();    
            Frame data_frame = Frame.new_Data_Frame(next_frame_to_send, prev_seq(frame_expected), packet);
            next_frame_to_send = next_seq(next_frame_to_send);
            sim.to_physical_layer(data_frame);
            }   
        }
    }    

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     *
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }

    /* Variables */
    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    /**
     * Array of strings which saves the received packets from the network layer
     */
    String[] packets_from_network_layer;   
    
    /**
     * Expected sequence number of the next data frame received
     */
    private int last_frame_not_confirmed;
    
    /**
     * Last packet that was sent
     */
    private String last_packet_sent;
    
    /**
     * Sequence number of the next data frame
     */
    private int next_frame_to_send;   
  
    /**
     * Expected sequence number of the next data frame received
     */
    private int frame_expected;
    
    /**
     * Expected ACK frame to receive
     */
    private int ack_expected;
    
    /**
     * Counter of the send_window
     */
    private int counter;
    
    /**
     * Counter of the retransmission
     */
    private int retransmissionCounter;
    
    /**
     * True if it has already sent a NAK frame, False otherwise
     */
    private boolean nak_sent;
    
}