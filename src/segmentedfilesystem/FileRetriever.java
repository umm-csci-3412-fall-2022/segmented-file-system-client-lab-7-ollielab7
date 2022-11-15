package segmentedfilesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class FileRetriever {
	int port;
	InetAddress address;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] buf = new byte[1028];

	public FileRetriever(String server, int port) throws UnknownHostException {
		// Save the server and port for use in `downloadFiles()`
		// You'll need to convert the server string to an
		// InetAddress, and save that as well.
		address = InetAddress.getByName(server);
		this.port = port;
	}

	public void downloadFiles(int numFiles) throws IOException {
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
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
		ArrayList<FileInLab7> files = new ArrayList<FileInLab7>();
		while (needData(files, numFiles)) {
			buf = new byte[1028];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			buf = packet.getData();
			System.out.print(".");
			if (buf[0] % 2 == 0) {
				// header packet
				findFileOrMake(buf, files).setHeader(buf);
			} else {
				// data packet
				FileInLab7 file = findFileOrMake(buf, files);

				if ((buf[0] >> 1) % 2 == 1) {
					// last data packet of corresponding file, length of packet might not be 1028
					file.finalData(buf, packet.getLength());
				} else {
					file.addData(buf);
				}
			}
		}
		printFiles(files);
	}

	private boolean needData(ArrayList<FileInLab7> files, int numFiles) {
		if (files.size() < numFiles)
			return true;
		for (FileInLab7 file : files) {
			if (file.numChunks != file.data.size() - 1) {
				return true;
			}
		}
		return false;
	}

	private void printFiles(ArrayList<FileInLab7> files) {
		for (FileInLab7 file : files) {
			File fileToWrite = new File(file.fileName.trim());
			try {
				fileToWrite.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try (FileOutputStream writer = new FileOutputStream(fileToWrite)) {
				int i;
				for (i = 0; i < file.numChunks + 1; i++) {
					for (byte b : file.data.get(i)) {
						writer.write(b);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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
		files.add(file);
		return file;
	}
}
