package com.minecrafttas.tasbattle.stats.structs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Player statistics struct
 * @author Pancake
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayerData {

    private final UUID id;
    private String username;
    private int wins, losses, kills, deaths;

    /**
     * Calculates a players scoreboard index using some stolen math (idk if this is a good idea)
     * @return Player index
     */
    public int calculateIndex() {
        return (int) (wins * Math.sqrt(kills / (double) Math.max(1, deaths)));
    }

}
