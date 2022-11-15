package segmentedfilesystem;

import java.util.Arrays;
import java.util.HashMap;

public class FileInLab7 {
    int fID;
    String fileName;
    int numChunks = 9999;
    HashMap<Integer, byte[]> data = new HashMap<Integer, byte[]>();

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
        byte[] chunk = new byte[buf.length - 4];
        for(int i = 4; i < buf.length; i++){
            chunk[i-4] = buf[i];
        }

        data.put(index, chunk);
    }
    public void finalData(byte[] buf, int a) {
        int index = findChunkNumber(Byte.toUnsignedInt(buf[2]),Byte.toUnsignedInt(buf[3]));
        byte[] chunk = new byte[a - 4];
        for(int i = 4; i < a; i++){
            chunk[i-4] = buf[i];
        }
        numChunks = index;
        data.put(index, chunk);
    }

    private int findChunkNumber(int x, int y) {
        return 256*x + y;
    }
    // if a data packet was sent before the header packet, this method will set the file name
    public void setHeader(byte[] buf) {
        if (fileName == null) {
            fileName = new String(Arrays.copyOfRange(buf, 2, buf.length));
        }
    }
}
