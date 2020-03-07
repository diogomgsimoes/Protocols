/*
 * Sistemas de Telecomunicacoes 
 *          2017/2018
 */
package simulator;

import java.util.StringTokenizer;


/**
 * Defines the frames exchanged between the terminal protocol objects and the 
 * channel. Implements methods to create new objects, to serialize and deserialize.
 * 
 * @author lflb@fct.unl.pt
 */
public class Frame {
    
    /**
     * Undefined (uninitialized event)
     */
    public static final int UNDEFINED_FRAME = 0;
    
    /**
     * Data frame
     */
    public static final int DATA_FRAME = 21;
    
    /**
     * ACK frame
     */
    public static final int ACK_FRAME = 22;
    
    /**
     * NAK frame
     */
    public static final int NAK_FRAME = 23;
    
    /**
     * Undefined sequence number; it must be 0 or above
     */
    public static final int UNDEFINED_SEQ = -1;
    
    /**
     * Maximum length of the selective ACK vector - not used
     */
    public static final int MAX_ACKVEC_LENGTH = 32;
    
    /**
     * Maximum length of the data information string
     */
    public static final int MAX_INFO_LENGTH = 100;

    /**
     * Constructor
     */
    public Frame() {
        kind = UNDEFINED_FRAME;
        info = null;
        seq = UNDEFINED_SEQ;
        ack = UNDEFINED_SEQ;
        ackvector = null;
        sendTime = Event.UNDEF_TIME;
        recvTime = Event.UNDEF_TIME;
    }

    /**
     * Resets the frame contents to UNDEFINED_FRAME
     */
    private void reset_frame() {
        kind = UNDEFINED_FRAME;
        info = null;
        seq = UNDEFINED_SEQ;
        ack = UNDEFINED_SEQ;
        ackvector = null;
        sendTime = Event.UNDEF_TIME;
        recvTime = Event.UNDEF_TIME;
    }

    
    /* Static methods to create new frame object instances */
    
    /**
     * Creates a new instance (object) of a Data frame 
     * @param seq sequence number
     * @param ack acknowledgment number
     * @param info packet transmitted
     * @return the frame object created
     */
    public static Frame new_Data_Frame(int seq, int ack, String info) {
        Frame frame= new Frame();
        frame.set_DATA_frame(seq, ack, info);
        return frame;
    }
    
    /**
     * Creates a new instance (object) of an Ack frame
     * @param ack acknowledgment number
     * @return the frame object created
     */
    public static Frame new_Ack_Frame(int ack) {
        Frame frame= new Frame();
        frame.set_ACK_frame(ack);
        return frame;
    }

    /**
     * Creates a new instance (object) of an Nak frame
     * @param nak acknowledgment number
     * @return the frame object created
     **/
    public static Frame new_Nak_Frame(int nak) {
        Frame frame= new Frame();
        frame.set_NAK_frame(nak);
        return frame;
    }

    /* Methods to read the frame fields' contents */
        
    /**
     * Get the kind of the frame object
     * @return the kind of the frame
     */
    public int kind() {
        return kind;
    }
    
    /**
     * Get the sequence number (valid for DATA_FRAME kind)
     * @return the sequence number
     */
    public int seq() {
        return seq;
    }

    /**
     * Get the acknowledgement number
     * @return the acknowledgement number
     */
    public int ack() {
        return ack;
    }

    /**
     * Get the information of a DATA_FRAME
     * @return the information carried
     */
    public String info() {
        return info;
    }

    /**
     * Get the acknowledgement vector, with frames received after ack - not used
     * @return the acknowledgement vector
     *
    public int[] ackvec() {
        return ackvector;
    }*/

    /**
     * Get the initial sending time of the frame
     * @return the sending time, or Event.UNDEF_TIME
     */
    public long snd_time() {
        return this.sendTime;
    }

    /**
     * Get the reception time of the frame
     * @return the reception time, or Event.UNDEF_TIME
     */
    public long rcv_time() {
        return this.recvTime;
    }

    /**
     * Test if the event is undefined
     * @return true if is undefined, false otherwise
     */
    boolean is_undef() {
        return kind == UNDEFINED_FRAME;
    }

    /**
     * Get a string with the kind of the frame
     * @return string with the kind of frame
     */
    public String kindString() {
        String str;
        switch (kind) {
            case UNDEFINED_FRAME:
                str = "UNDEFINED";
                break;
            case DATA_FRAME:
                str = "DATA";
                break;
            case ACK_FRAME:
                str = "ACK";
                break;
            case NAK_FRAME:
                str = "NAK";
                break;
            default:
                str = "INVALID";
        }
        return str;
    }

    /**
     * Returns a string with the frame object's contents
     * @return string with the frame object's contents
     */
    @Override
    public String toString() {
        String str = kindString();
        if (kind == DATA_FRAME) {
            str += " " + (seq == UNDEFINED_SEQ ? "undef seq" : seq);
        }
        if (kind == DATA_FRAME || kind == ACK_FRAME || kind == NAK_FRAME) {
            str += " " + (ack == UNDEFINED_SEQ ? "undef ack" : ack);
        }
        if (ackvector != null) {
            str += " ackvec(";
            for (int i= 0; i<ackvector.length; i++) {
                str += ((i==0)?"":" ") + ackvector[i];
            }
            str += ")";
        }
        // ...
        return str;
    }

    /**
     * Used with DATA_FRAME frames to set the fields values
     * @param seq sequence number
     * @param ack acknowledgement number
     * @param info packet information
     * @return true if successful, false otherwise
     */
    public boolean set_DATA_frame(int seq, int ack, String info) {
        if ((seq <= UNDEFINED_SEQ) || (ack <= UNDEFINED_SEQ) || (info == null)
                || info.isEmpty() || (info.length() > MAX_INFO_LENGTH)) {
            return false;
        }
        kind = DATA_FRAME;
        this.seq = seq;
        this.ack = ack;
        this.info = info;
        return true;
    }

    /**
     * Used with ACK_FRAME frames to set the fields values
     * @param ack acknowledgement number
     * @return true if successful, false otherwise
     */
    public boolean set_ACK_frame(int ack) {
        if (ack <= UNDEFINED_SEQ) {
            return false;
        }
        kind = ACK_FRAME;
        this.ack = ack;
        return true;
    }

    /**
     * Used with NAK_FRAME frames to set the fields values
     * @param nak missing sequence number
     * @return true if successful, false otherwise
     */
    public boolean set_NAK_frame(int nak) {
        if (nak <= UNDEFINED_SEQ) {
            return false;
        }
        kind = NAK_FRAME;
        this.ack = nak;
        return true;
    }

    /**
     * Set the acknowledgment vector value - not used
     * @param ackvector acknowledgment vector
     * @return true if successful, false otherwise
     *
    public boolean set_ACK_vector(int[] ackvector) {
        if ((ackvector == null) || (ackvector.length > MAX_ACKVEC_LENGTH)) {
            return false;
        }
        this.ackvector = ackvector;
        return true;
    }*/

    /**
     * Set the frame sending time
     * @param sendTime initial sending time
     * @return true if successful, false otherwise
     */
    public boolean set_sendTime(long sendTime) {
        if ((sendTime != Event.UNDEF_TIME) && (sendTime < 0)) {
            return false;
        }
        this.sendTime = sendTime;
        return true;
    }

    /**
     * Set the frame reception time
     * @param recvTime reception time
     * @return true if successful, false otherwise
     */
    public boolean set_recvTime(long recvTime) {
        if ((recvTime != Event.UNDEF_TIME) && (recvTime < 0) || (sendTime >= recvTime)) {
            return false;
        }
        this.recvTime = recvTime;
        return true;
    }

    
    /**
     * Prepares a string with the frame contents, serializing the object
     * @return the string, or null if error
     */
    public String frame_to_str() {
        if (kind == UNDEFINED_FRAME) {
            return null;
        }
        String str = "";

        // Set initial header 
        switch (kind) {
            case DATA_FRAME:
                str = "DATA " + seq + " " + ack;
                break;
            case ACK_FRAME:
                str = "ACK " + ack;
                break;
            case NAK_FRAME:
                str = "NAK " + ack;
                break;
        }

        // Write sendTime
        if (sendTime != Event.UNDEF_TIME) {
            str += " SNDTIME " + sendTime;
        }

        // Write sendTime
        if (recvTime != Event.UNDEF_TIME) {
            str += " RCVTIME " + recvTime;
        }

        // Write ACK vector
        if (ackvector != null) {
            str += " ACKVEC " + ackvector.length;
            for (int i = 0; i < ackvector.length; i++) {
                str += " " + ackvector[i];
            }
        }

        // Write DATA
        if (info != null) {
            str += " INFO " + info.length() + " " + info;
        }

        return str;
    }

    /**
     * Decodes the contents of a string to the frame, desirealizing it
     * @param line - string with the frame's contents
     * @return true if decoded successfully, false otherwise
     */
    public boolean str_to_frame(String line, Log log) {
        if (line == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(line);
        String cmd = null;

        try {
            while (st.hasMoreTokens()) {
                cmd = st.nextToken();
                switch (cmd) {
                    case "DATA":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = DATA_FRAME;
                        if (st.countTokens() < 2) {
                            log.Log("Received DATA without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        seq = Event.parseInt(st.nextToken());
                        ack = Event.parseInt(st.nextToken());
                        break;
                    case "ACK":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = ACK_FRAME;
                        if (st.countTokens() < 1) {
                            log.Log("Received ACK without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        ack = Event.parseInt(st.nextToken());
                        break;
                    case "NAK":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = NAK_FRAME;
                        if (st.countTokens() < 1) {
                            log.Log("Received NAK without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        ack = Event.parseInt(st.nextToken());
                        break;
                    case "SNDTIME":
                        if (st.countTokens() < 1) {
                            log.Log("Received SNDTIME without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        sendTime = Event.parseLong(st.nextToken());
                        break;
                    case "RCVTIME":
                        if (st.countTokens() < 1) {
                            log.Log("Received RCVTIME without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        recvTime = Event.parseLong(st.nextToken());
                        break;
                    case "ACKVEC":
                        log.Log("Received ACKVEC: Ignored - not supported\n");
                        reset_frame();
                        return false;
                        /*if (st.hasMoreTokens()) {
                            int len = Event.parseInt(st.nextToken());
                            if (len < 1 || len > MAX_ACKVEC_LENGTH) {
                                log.Log("Received ACKVEC with invalid vector length\n");
                                reset_frame();
                                return false;
                            }
                            if (st.countTokens() < len) {
                                log.Log("Received ACKVEC with insufficient number of vector elements\n");
                                reset_frame();
                                return false;
                            }
                            ackvector = new int[len];
                            for (int i = 0; i < len; i++) {
                                ackvector[i] = Event.parseInt(st.nextToken());
                            }
                        } else {
                            log.Log("Received ACKVEC without enough parameters\n");
                            reset_frame();
                            return false;
                        } break; */   
                    case "INFO":
                        if (st.hasMoreTokens()) {
                            int len = Event.parseInt(st.nextToken());
                            if (len < 1 || len > MAX_INFO_LENGTH) {
                                log.Log("Received DATA with invalid data length\n");
                                reset_frame();
                                return false;
                            }
                            info = st.nextToken();
                            if (info.length() != len) {
                                log.Log("Received DATA with invalid length (" + len + "!=" + info.length() + ")\n");
                                reset_frame();
                                return false;
                            }
                        } else {
                            log.Log("Received INFO without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        break;
                    default:
                        log.Log("Received invalid token '" + cmd + "'\n");
                        reset_frame();
                        return false;
                }
            }
            return true;
        } catch (NumberFormatException ne) {
            log.Log("Invalid number in " + (cmd == null ? "" : cmd) + " element\n");
            reset_frame();
            return false;
        } catch (Exception e) {
            log.Log("Exception in " + (cmd == null ? "" : cmd) + " element: " + e + "\n");
            reset_frame();
            return false;
        }
    }
    
    
    /* Frame fields */
    
    /**
     * Frame kind - can be: UNDEFINED_PCKT or DATA_PCKT or ACK_PCKT
     */
    private int kind;
    
    /**
     * Data - only used for DATA packets
     */
    private String info;
    
    /**
     * Sequence number - only used for DATA packets
     */
    private int seq;
    
    /**
     * Acknowledge number - for DATA, ACK and NAK packets; it defines the 
     * sequence number of the last DATA frame successfully received for DATA and 
     * ACK; if defines the missing DATA frame for NAK packets
     */
    private int ack;
    
    /**
     * Acknowledge vector, with packets received above 'ack' - for DATA and ACK
     * packets - NOT USED!
     */
    private int[] ackvector;
    
    /**
     * Frame sending time
     */
    private long sendTime;
    
    /**
     * Frame reception time
     */
    private long recvTime;
}
