package de.pfannekuchen.replaymod;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.UUID;

import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.Studio;
import com.replaymod.replaystudio.io.ReplayOutputStream;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.packet.State;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.version.ProtocolVersion;
import com.replaymod.replaystudio.protocol.PacketTypeRegistry;
import com.replaymod.replaystudio.replay.ReplayFile;
import com.replaymod.replaystudio.replay.ReplayMetaData;
import com.replaymod.replaystudio.replay.ZipReplayFile;
import com.replaymod.replaystudio.stream.PacketStream;
import com.replaymod.replaystudio.studio.ReplayStudio;

import de.pfannekuchen.replaymod.filter.ProgressFilter;
import de.pfannekuchen.replaymod.filter.SpeedupFilter;

public class Server {

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ServerSocket socket = new ServerSocket(6663);
		while (true) {
			final Socket s = socket.accept();
			safeHandleClient(s);
		}
	}
	
	public static void safeHandleClient(Socket client) throws Exception {
		Path temp = Files.createTempDirectory("replaymod");
		try {
			handleClient(client, temp.toFile());
		} catch (Exception e) {
			if (client != null)
				client.close();
			System.out.println("Safe Handle Client caught an exception!");
			e.printStackTrace();
		}
		Files.walk(temp).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		Files.deleteIfExists(temp);
	}
	
	public static void handleClient(Socket client, File temp) throws Exception {
		DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
		
		float tickrate = in.readFloat();
		
		int dataLength = in.readInt();
		if (dataLength > 1e+8)
			throw new Exception("Client file too big!");
		
		byte[] data = new byte[dataLength];
		in.read(data);
		
		convert(data, out, tickrate, temp);
		
		client.close();
	}
	
	public static void convert(byte[] in, BufferedOutputStream out, float tickrate, File temp) throws Exception {
		// Create a Studio
		Studio studio = new ReplayStudio();

		// Load Input File
		File f = new File(temp, UUID.randomUUID() + ".mcpr");
		Files.write(f.toPath(), in, StandardOpenOption.CREATE);
		ReplayFile replayFileIn = new ZipReplayFile(studio, f);
		ReplayMetaData replayMetaIn = replayFileIn.getMetaData();
		ProtocolVersion replayVersionIn = replayMetaIn.getProtocolVersion();
		PacketStream replayStreamIn = replayFileIn.getPacketData(PacketTypeRegistry.get(replayVersionIn, State.PLAY)).asPacketStream();
		
		// Make Output Stream
		ReplayOutputStream replayStreamOut = new ReplayOutputStream(replayVersionIn, out, null);
		
		// Prepare Stream
		replayStreamIn.start();
		replayStreamIn.addFilter(new ProgressFilter(replayMetaIn.getDuration()));
		replayStreamIn.addFilter(new SpeedupFilter(tickrate), 0, replayMetaIn.getDuration());
		
		// Copy Input File to Output File while applying a Speedup Filter
		PacketData data;
		while ((data = replayStreamIn.next()) != null) {
			replayStreamOut.write(data);
		}
	
		for (PacketData dat : replayStreamIn.end()) {
			replayStreamOut.write(dat);
		}
		
		// Close all streams
		replayStreamOut.close();
		replayFileIn.close();
	}
	
}
