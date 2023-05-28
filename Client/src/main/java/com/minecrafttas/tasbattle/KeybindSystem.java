package com.minecrafttas.tasbattle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

/**
 * Manages keybinds and their categories.
 * @author Pancake
 */
@Environment(EnvType.CLIENT)
public class KeybindSystem {

	/**
	 * List of keybinds
	 */
	private static Keybind[] keybinds = {
		new Keybind("keybind.tasbattle.spectatenext", "keybind.category.tasbattle.tasbattle", GLFW.GLFW_KEY_R, true, () -> { //Spectate next player //TAS Battle
			SpectatorManager.getInstance().spectateNextPlayer();
		}),
		new Keybind("keybind.tasbattle.spectateprev", "keybind.category.tasbattle.tasbattle", GLFW.GLFW_KEY_Q, true, () -> { //Spectate previous player //TAS Battle
			SpectatorManager.getInstance().spectatePreviousPlayer();
		}),
		new Keybind("Test", "keybind.category.tasbattle.tasbattle", GLFW.GLFW_KEY_F8, true, () -> { //Spectate previous player //TAS Battle
			SpectatorManager.getInstance().startSpectating();
		}),
		new Keybind("Test2", "keybind.category.tasbattle.tasbattle", GLFW.GLFW_KEY_F9, true, () -> { //Spectate previous player //TAS Battle
			SpectatorManager.getInstance().stopSpectating();
		})
	};

	/**
	 * Represents a keybind
	 * @author Pancake
	 */
	private static class Keybind {

		/**
		 * Minecraft key mapping
		 */
		private KeyMapping keyMapping;

		/**
		 * Category of the keybind in the controls menu
		 */
		private String category;

		/**
		 * Should the keybind only be available if mc.level is not null
		 */
		private boolean isInGame;

		/**
		 * Will be run when the keybind is pressed
		 */
		private Runnable onKeyDown;

		/**
		 * Initializes a keybind
		 * @param name Name of the keybind
		 * @param category Category of the keybind
		 * @param defaultKey Default key of the keybind
		 * @param isInGame Should the keybind only be available if mc.level is not null
		 * @param onKeyDown Will be run when the keybind is pressed
		 */
		public Keybind(String name, String category, int defaultKey, boolean isInGame, Runnable onKeyDown) {
			this.keyMapping = new KeyMapping(name, defaultKey, category);
			this.category = category;
			this.isInGame = isInGame;
			this.onKeyDown = onKeyDown;
		}

		/**
		 * Returns the minecraft key mapping
		 * @return Minecraft key mapping
		 */
		public KeyMapping getKeyMapping() {
			return this.keyMapping;
		}

	}

	/**
	 * Initializes the keybind Manager, registers categories and key binds.
	 */
	public static KeyMapping[] onKeybindInitialize(KeyMapping[] keyMappings) {
		// Initialize categories
		Map<String, Integer> categories = KeyMapping.CATEGORY_SORT_ORDER;
		for (int i = 0; i < keybinds.length; i++)
			if (!categories.containsKey(keybinds[i].category))
				categories.put(keybinds[i].category, i + 8);
		// Add keybinds
		return ArrayUtils.addAll(keyMappings, Arrays.asList(keybinds).stream().map(Keybind::getKeyMapping).toArray(KeyMapping[]::new)); // convert Keybind array to KeyMapping on the fly
	}

	/**
	 * Watches for key presses and triggers sub events.
	 * @param mc Instance of minecraft
	 */
	public static void onGameLoop(Minecraft mc) {
		for (Keybind keybind : keybinds) {
			if (keybind.isInGame && mc.level == null || !isKeyDown(mc, keybind.getKeyMapping()))
				continue;
			keybind.onKeyDown.run();
		}
	}

	/**
	 * Map of pressed/non-pressed keys.
	 */
	private static Map<KeyMapping, Boolean> keys = new HashMap<>();

	/**
	 * Checks whether a key has been pressed recently.
	 * @param mc Instance of minecraft
	 * @param map Key mappings to check
	 * @return Key has been pressed recently
	 */
	private static boolean isKeyDown(Minecraft mc, KeyMapping map) {
		// Check if in a text field
		Screen screen = mc.screen;
		if (screen != null && ((screen.getFocused() instanceof EditBox && ((EditBox) screen.getFocused()).canConsumeInput()) || screen.getFocused() instanceof RecipeBookComponent))
			return false;

		boolean wasPressed = keys.containsKey(map) ? keys.get(map) : false;
		boolean isPressed = GLFW.glfwGetKey(mc.getWindow().getWindow(), map.key.getValue()) == GLFW.GLFW_PRESS;
		keys.put(map, isPressed);
		return !wasPressed && isPressed;
	}

}