package de.pfannekuchen.tasbattle.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;

@Mixin(SelectWorldScreen.class)
public interface SelectWorldScreenAccessor {

	@Accessor
	public WorldSelectionList getList();
	
}
