package segmentedfilesystem;

import java.util.ArrayList;
import java.util.Arrays;

public class FileInLab7 {
    int fID;
    String fileName;
    ArrayList<ArrayList<Byte>> data = new ArrayList<ArrayList<Byte>>();
    public boolean open;

    //constructor for header packet
    public FileInLab7(byte b, byte[] buf) {
        fID = b;
        fileName = new String(Arrays.copyOfRange(buf, 2, buf.length));
    }

    //constructor for data packet sent before header packet
    public FileInLab7(byte b){
        fID = b;
    }

    public void addData(byte[] buf) {
        int index = findChunkNumber(Byte.toUnsignedInt(buf[2]),Byte.toUnsignedInt(buf[3]));
        ArrayList<Byte> chunk = new ArrayList<Byte>();
        for(int i = 4; i < buf.length; i++){
            chunk.add(buf[i]);
        }
        data.add(index, chunk);
    }

    private int findChunkNumber(int x, int y) {
        return 256*x + y;
    }

    public void lastPacket(byte[] buf) {
        
    }

}
