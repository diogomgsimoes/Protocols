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
 * Protocol 5 : Selective Repeat protocol
 *
 * @author 50236, 50292 and 50732
 */
public class SelectiveRepeat extends Base_Protocol implements Callbacks {

    public SelectiveRepeat(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol

        // Initialize object fields
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nSelective Repeat Protocol\n\n");
        sim.Log("\nNot implemented yet\n\n");
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * and send next until reaching the end of the sending window
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.Log("handle_Data_end not implemented\n");
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames
     *
     * @param time current simulation time
     */
    @Override
    public void handle_Data_Timer(long time) {
        sim.Log("handle_Data_Timeout not implemented\n");
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log("from_ack_Timer not implemented\n");
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
        sim.Log("from_physical_layer not implemented\n");
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

}
