/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package protocol;

/**
 * Interface implemented by the Protocol to receive events from the channel.
 * 
 * @author lflb@fct.unl.pt
 */
public interface Callbacks {
    
    /**
     * Event generated in the beginning of a simulation
     * @param time current simulation time
     */
    void start_simulation(long time);
    
    /**
     * Event that signals the end of the transmission of a data frame 
     * @param time current simulation time
     * @param seq sequence number of the Data frame
     */
    void handle_Data_end(long time, int seq);
    
    /**
     * Data Timer event
     * @param time current simulation time
     */
    void handle_Data_Timer(long time);
    
    /**
     * ACK Timer event
     * @param time current simulation time
     */
    void handle_ack_Timer(long time);
    
    /**
     * Event received when a frame is received from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    void from_physical_layer(long time, simulator.Frame frame);

    /** Event generated in the beginning of a simulation
     * @param time current simulation time
     */
    void end_simulation(long time);
    
}
