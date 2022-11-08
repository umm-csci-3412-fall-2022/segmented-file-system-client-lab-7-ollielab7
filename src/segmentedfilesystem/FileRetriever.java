package segmentedfilesystem;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class FileRetriever {
        int port;
        InetAddress address;
        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] sendBuf = new byte[256];

        public FileRetriever(String server, int port) throws UnknownHostException {
                // Save the server and port for use in `downloadFiles()`
                // You'll need to convert the server string to an
                // InetAddress, and save that as well.
                address = InetAddress.getByName(server);
                this.port = port;
        }

        public void downloadFiles() throws IOException {
                // Do all the heavy lifting here.
                // This should
                // * Connect to the server
                // * Download packets in some sort of loop
                // * Handle the packets as they come in by, e.g.,
                // handing them to some PacketManager class
                // Your loop will need to be able to ask someone
                // if you've received all the packets, and can thus
                // terminate. You might have a method like
                // PacketManager.allPacketsReceived() that you could
                // call for that, but there are a bunch of possible
                // ways.

                socket = new DatagramSocket();
                packet = new DatagramPacket(sendBuf, sendBuf.length, address, port);
                socket.send(packet);

                byte[] buf = new byte[1028];
                packet = new DatagramPacket(buf, buf.length);
                ArrayList<FileInLab7> files = new ArrayList<FileInLab7>();

                while (PacketManager.allPacketsReceived() == false) {
                        socket.receive(packet);
                        buf = packet.getData();

                        if (buf[0] % 2 == 0) {
                                System.out.println("This is a header packet");
                                FileInLab7 file = findFileOrMake(buf, files);
                                files.add(file);
                        } else {
                                System.out.println("This is a data packet");
                                FileInLab7 file = findFileOrMake(buf, files);
                                file.addData(buf);
                                files.add(file);
                                if ((buf[0] >> 1) % 2 == 1) {                                        
                                        System.out.println("This is the last packet");
                                }
                        }
                }
        }


        private FileInLab7 findFileOrMake(byte[] buf, ArrayList<FileInLab7> files) {
                for (FileInLab7 file : files) {
                        if (file.fID == buf[1]) {
                                return file;
                        }
                }
                FileInLab7 file = new FileInLab7(buf[1]);
                return file;
        }

}
