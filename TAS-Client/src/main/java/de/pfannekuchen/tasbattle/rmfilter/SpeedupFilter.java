package de.pfannekuchen.tasbattle.rmfilter;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.Studio;
import com.replaymod.replaystudio.filter.StreamFilter;
import com.replaymod.replaystudio.protocol.Packet;
import com.replaymod.replaystudio.protocol.PacketType;
import com.replaymod.replaystudio.stream.PacketStream;

import io.netty.buffer.ByteBuf;

/**
 * A filter that manipulates the packet timestamp
 * @author Pancake
 */
public class SpeedupFilter implements StreamFilter {
	
	@Override public String getName() { return "speedup"; }
	@Override public void init(Studio studio, JsonObject json) { }
	@Override public void onEnd(PacketStream stream, long time) throws IOException { }
	@Override public void onStart(PacketStream stream) throws IOException { }

	private float tickrate;
	private long offset;
	
	public SpeedupFilter(float tickrate) {
		this.tickrate = tickrate;
	}
	
	public int readVarInt(ByteBuf buf) {
		byte b;
		int i = 0, j = 0;
		do i |= ((b = buf.readByte()) & 0x7F) << j++ * 7; 
		while ((b & 0x80) == 128);
		return i;
	}
	
	@Override
	public boolean onPacket(PacketStream stream, PacketData packet) throws IOException {
		// Check for possible tickrate update packets
		Packet packetdata = packet.retain().getPacket();
		if (packetdata.getType() != PacketType.PluginMessage)
			stream.insert(new PacketData((long) (this.offset + ((packet.getTime() - this.offset)/(20.0f/tickrate))), packetdata));
		return false;
	}

}
