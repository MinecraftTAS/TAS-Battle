package com.minecrafttas.tasbattle.mixin.hooks;

import com.minecrafttas.tasbattle.system.DataSystem;
import com.minecrafttas.tasbattle.system.DimensionSystem;
import com.minecrafttas.tasbattle.system.TickrateChanger;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

/**
 * This mixin is purely responsible for the hooking up networking events
 * @author Pancake
 */
@Mixin(ClientPacketListener.class)
public class HookClientHandshakePacketListenerImpl {

    @Shadow
    public Minecraft minecraft;

    @Shadow
    public Connection connection;

    /**
     * Send tasbattle packets when creating the player initially
     * @param ci Callback Info
     */
    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void onHandleLogin(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        this.minecraft.doRunTask(() -> {
            this.connection.send(new ServerboundCustomPayloadPacket(new ResourceLocation("minecraft", "register"), new FriendlyByteBuf(Unpooled.buffer().writeBytes(TickrateChanger.IDENTIFIER.toString().getBytes(StandardCharsets.US_ASCII)))));
            this.connection.send(new ServerboundCustomPayloadPacket(new ResourceLocation("minecraft", "register"), new FriendlyByteBuf(Unpooled.buffer().writeBytes(TickrateChanger.IDENTIFIER.toString().getBytes(StandardCharsets.US_ASCII)))));
            this.connection.send(new ServerboundCustomPayloadPacket(new ResourceLocation("minecraft", "register"), new FriendlyByteBuf(Unpooled.buffer().writeBytes(DimensionSystem.IDENTIFIER.toString().getBytes(StandardCharsets.US_ASCII)))));
            this.connection.send(new ServerboundCustomPayloadPacket(TickrateChanger.IDENTIFIER, new FriendlyByteBuf(Unpooled.buffer(1))));
            this.connection.send(new ServerboundCustomPayloadPacket(DataSystem.IDENTIFIER, new FriendlyByteBuf(Unpooled.buffer(1))));
            this.connection.send(new ServerboundCustomPayloadPacket(DimensionSystem.IDENTIFIER, new FriendlyByteBuf(Unpooled.buffer(1))));
        });
    }

}
