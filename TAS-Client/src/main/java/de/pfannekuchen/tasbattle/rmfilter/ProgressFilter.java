package de.pfannekuchen.tasbattle.rmfilter;

import com.google.gson.JsonObject;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.Studio;
import com.replaymod.replaystudio.filter.StreamFilter;
import com.replaymod.replaystudio.stream.PacketStream;

public class ProgressFilter implements StreamFilter {

	private final long total;
	private int lastUpdate;

	public ProgressFilter(long total) {
		this.total = total;
	}

	@Override
	public String getName() {
		return "progress";
	}

	@Override
	public void init(Studio studio, JsonObject config) {

	}

	@Override
	public void onStart(PacketStream stream) {
		lastUpdate = -1;
	}

	@Override
	public boolean onPacket(PacketStream stream, PacketData data) {
		int pct = (int) (data.getTime() * 100 / total);
		if (pct > lastUpdate) {
			lastUpdate = pct;
			System.out.print("Processing... " + pct + "%\r");
		}
		return true;
	}

	@Override
	public void onEnd(PacketStream stream, long timestamp) {
		System.out.println("Processing... done!");
	}
}