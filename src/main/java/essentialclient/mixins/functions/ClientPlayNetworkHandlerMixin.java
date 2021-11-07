package essentialclient.mixins.functions;

import essentialclient.clientscript.MinecraftEventFunction;
import essentialclient.clientscript.values.ItemStackValue;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onHealthUpdate", at = @At("HEAD"))
    private void onHealthUpdate(CallbackInfo ci) {
        MinecraftEventFunction.ON_HEALTH_UPDATE.runFunction();
    }

    @Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"))
    private void onTotem(CallbackInfo ci) {
        MinecraftEventFunction.ON_TOTEM.runFunction();
    }

    @Inject(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;setGameMode(Lnet/minecraft/world/GameMode;)V"))
    private void onGamemodeChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        MinecraftEventFunction.ON_GAMEMODE_CHANGE.runFunction(List.of(new StringValue(GameMode.byId((int) packet.getValue()).getName())));
    }

    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void onPickUp(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        Entity entity = this.world.getEntityById(packet.getEntityId());
        LivingEntity livingEntity = (LivingEntity) this.world.getEntityById(packet.getCollectorEntityId());
        if (entity != null && this.client.player != null && this.client.player.equals(livingEntity) && entity instanceof ItemEntity itemEntity) {
            MinecraftEventFunction.ON_PICKUP.runFunction(List.of(new ItemStackValue(itemEntity.getStack())));
        }
    }
}
