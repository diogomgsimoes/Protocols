/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package terminal;

/**
 * Interface implemented by the Terminal that defines the methods the protocol 
 * can use to get the configuration parameters and send comands.
 * 
 * @author lflb@fct.unl.pt
 */
public interface Simulator extends simulator.Log { 
    
    /* Configuration parameters */
    
    /**
     * Get the sending window size
     * @return the sending window size
     */
    int get_send_window();
    
    /**
     * Get the receiving window size
     * @return the receiving window size
     */
    int get_recv_window();
    
    /**
     * Get the maximum sequence number
     * @return the maximum sequence number
     */
    int get_max_sequence();
    
    /**
     * Get the time interval the protocol should wait before retransmitting a data frame
     * @return the timeout value
     */
    long get_timeout_time();
    
    /**
     * Get to current simulation time
     * @return the current simulation time
     */
    long get_time();
    
    /*  Comands  */
    
    /**
     * Send one frame to the channel
     * @param frame frame to send
     */
    void to_physical_layer(simulator.Frame frame);
    
    
    /**
     * Verifies if the terminal is sending a data frame
     * @return true if is sending data, false otherwise
     */
    boolean is_sending_data();

    /**
     * Start a data timer; method handle_Data_Timer of the protocol will be 
     * called after this time.
     */
    void start_data_timer();
    
    /**
     * Resets the data timer time count, or starts the timer if it is stopped.
     */
    void restart_data_timer();
    
    /**
     * Cancels the timer associated with the key.
     */
    void cancel_data_timer();
    
    /**
     * Checks if the Data timer is active.
     * @return true if timer is active
     */
    boolean isactive_data_timer();
    
    /**
     * Starts an ACK timer to wait for the transmission of a data frame before sending the ACK
     */
    void start_ack_timer();
        
    /**
     * Cancels the ACK timer
     */
    void cancel_ack_timer();
    
    /**
     * Test if the ack timer is active
     * @return true if timer with key is active
     */
    boolean isactive_ack_timer();

    /**
     * Stop the simulation
     */
    void stop();
    
}
