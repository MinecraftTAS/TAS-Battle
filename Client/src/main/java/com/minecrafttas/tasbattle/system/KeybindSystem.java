package com.minecrafttas.tasbattle.system;

import com.minecrafttas.tasbattle.TASBattle;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages KEYBINDS and their categories.
 * @author Pancake
 */
public class KeybindSystem {

	private static final Map<Integer, Boolean> keys = new HashMap<>();
	private static final Keybind[] KEYBINDS = {
		new Keybind("Toggle spectating mode", "TAS Battle", GLFW.GLFW_KEY_R, true, () -> TASBattle.instance.getSpectatingSystem().changeMode())
	};

	private record Keybind(KeyMapping keyMapping, String category, boolean isInGame, Runnable onKeyDown) {
		public Keybind(String name, String category, int defaultKey, boolean isInGame, Runnable onKeyDown) {
			this(new KeyMapping(name, defaultKey, category), category, isInGame, onKeyDown);
		}
	}

	/**
	 * Initialize keybind Manager, register categories and kebinds.
	 * @param keyMappings KeyMappings array
	 */
	public static KeyMapping[] onKeybindInitialize(KeyMapping[] keyMappings) {
		// initialize categories
		Map<String, Integer> categories = KeyMapping.CATEGORY_SORT_ORDER;
		for (int i = 0; i < KEYBINDS.length; i++)
			if (!categories.containsKey(KEYBINDS[i].category))
				categories.put(KEYBINDS[i].category, i + 8);
		
		// add keybinds
		return ArrayUtils.addAll(keyMappings, Arrays.stream(KEYBINDS).map(Keybind::keyMapping).toArray(KeyMapping[]::new)); // convert Keybind array to KeyMapping on the fly
	}

	/**
	 * Watch for key presses and trigger KEYBINDS
	 * @param mc Instance of Minecraft
	 */
	public static void onGameLoop(Minecraft mc) {
		for (Keybind keybind : KEYBINDS) {
			if (keybind.isInGame && mc.level == null || !isKeyDown(mc, keybind.keyMapping.key.getValue()))
				continue;
			
			keybind.onKeyDown.run();
		}
	}
	

	/**
	 * Check whether key has been pressed recently.
	 * @param mc Instance of minecraft
	 * @param map Key mappings to check
	 * @return Key has been pressed recently
	 */
	public static boolean isKeyDown(Minecraft mc, int key) {
		// check if in a text field
		Screen screen = mc.screen;
		if (screen != null && ((screen.getFocused() instanceof EditBox && ((EditBox) screen.getFocused()).canConsumeInput()) || screen.getFocused() instanceof RecipeBookComponent))
			return false;

		boolean wasPressed = keys.getOrDefault(key, false);
		boolean isPressed = GLFW.glfwGetKey(mc.getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
		keys.put(key, isPressed);
		return !wasPressed && isPressed;
	}

}
