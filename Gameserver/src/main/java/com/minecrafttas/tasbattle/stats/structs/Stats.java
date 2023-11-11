package com.minecrafttas.tasbattle.stats.structs;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Stats {

    private final String mode;
    private final List<PlayerData> players = new ArrayList<PlayerData>();

}
