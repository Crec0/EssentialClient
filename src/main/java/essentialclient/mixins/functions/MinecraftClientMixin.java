package essentialclient.mixins.functions;

import essentialclient.feature.clientrule.ClientRuleHelper;
import essentialclient.feature.clientscript.MinecraftEventFunction;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.values.StringValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftEventFunction.ON_CLIENT_TICK.tryRunFunction();
        if (CommandHelper.needUpdate && this.player != null) {
            this.player.networkHandler.onCommandTree(ClientRuleHelper.serverPacket);
            CommandHelper.needUpdate = false;
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfo ci) {
        MinecraftEventFunction.ON_ATTACK.tryRunFunction();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onUse(CallbackInfo ci) {
        MinecraftEventFunction.ON_USE.tryRunFunction();
    }

    @Inject(method = "doItemPick", at = @At("HEAD"))
    private void onPickBlock(CallbackInfo ci) {
        MinecraftEventFunction.ON_PICK_BLOCK.tryRunFunction();
    }

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void onOpenScreen(Screen screen, CallbackInfo ci) {
        if (screen == null || screen.getTitle() == null)
            return;
        String screenName = screen.getTitle().getString();
        if (screenName.length() == 0)
            return;
        MinecraftEventFunction.ON_OPEN_SCREEN.tryRunFunction(List.of(new StringValue(screenName)));
    }
}
