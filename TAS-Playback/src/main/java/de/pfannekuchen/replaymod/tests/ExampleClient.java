package de.pfannekuchen.replaymod.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ExampleClient {

	public static void main(String[] args) throws Exception {
		// Load file
		final byte[] EXAMPLE_FILE = Files.readAllBytes(new File("input/original.mcpr").toPath());
		
		// Connect
		Socket client = new Socket("localhost", 6663);
		BufferedInputStream in = new BufferedInputStream(client.getInputStream());
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
		
		long time = System.currentTimeMillis();
		
		// Send data
		out.writeFloat(4.0f); // Tickrate
		out.writeInt(EXAMPLE_FILE.length); // Data Length
		out.write(EXAMPLE_FILE);
		out.flush();
		
		// Read data
		Files.write(new File("speedup.mcpr").toPath(), in.readAllBytes(), StandardOpenOption.CREATE);
		
		System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
		client.close();
	}
	
}
