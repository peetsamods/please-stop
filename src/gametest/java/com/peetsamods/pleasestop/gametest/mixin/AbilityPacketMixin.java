package com.peetsamods.pleasestop.gametest.mixin;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ClientConnection.class)
abstract class AbilityPacketMixin {
 @Inject(method="send(Lnet/minecraft/network/packet/Packet;)V",at=@At("HEAD"),require=1,allow=1)
 private void proof$countAbilityPacket(Packet<?> packet, CallbackInfo ci) {
  if(packet instanceof UpdatePlayerAbilitiesC2SPacket abilities) com.peetsamods.pleasestop.gametest.AbilityPacketCounter.record(abilities.isFlying());
 }
}
