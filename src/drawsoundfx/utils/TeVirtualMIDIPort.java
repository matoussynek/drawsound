package drawsoundfx.utils;

import de.tobiaserichsen.tevm.TeVirtualMIDI;
import de.tobiaserichsen.tevm.TeVirtualMIDIException;

public class TeVirtualMIDIPort extends Thread{
    TeVirtualMIDI port;

    public TeVirtualMIDIPort ( String name ) {

        this.port = new TeVirtualMIDI( name );

    }

    public TeVirtualMIDIPort ( String name, int maxSysexLength, int flags, String manuId, String prodId ) {

        this.port = new TeVirtualMIDI( name, maxSysexLength, flags, manuId, prodId );

    }

    public void shutdown() {

        this.port.shutdown();

    }


    static public String byteArrayToString( byte[] b ) {

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < b.length; i++ ) {

            if ( i > 0 ) {

                sb.append( ':' );

            }

            sb.append( Integer.toString( ( b[i] & 0xff ) + 0x100, 16 ).substring( 1 ) );

        }

        return sb.toString();

    }


    @Override
    public void run() {

        byte[] command;

        try {

            while( true ) {

                command = this.port.getCommand();

                this.port.sendCommand( command );

                System.out.println( "command: " + byteArrayToString( command ) );

            }
        } catch ( TeVirtualMIDIException e ) {

            System.out.println( "thread aborting: " + e );

            return;

        }

    }
}
