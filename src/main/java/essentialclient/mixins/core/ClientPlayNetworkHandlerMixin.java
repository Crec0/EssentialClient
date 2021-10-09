package essentialclient.mixins.core;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import essentialclient.commands.CommandRegister;
import essentialclient.feature.clientrule.*;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@SuppressWarnings("unchecked")
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        // This packet gets updated after it's been assigned???
        ClientRuleHelper.serverPacket = packet;
        CommandRegister.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @Redirect(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void checkMessages(InGameHud inGameHud, MessageType type, Text message, UUID sender) {
        if (type == MessageType.SYSTEM && ClientRules.DISABLE_OP_MESSAGES.getBoolean()) {
            if (message instanceof TranslatableText && !ClientRules.DISABLE_JOIN_LEAVE_MESSAGES.getBoolean()) {
                switch (((TranslatableText) message).getKey()) {
                    case "multiplayer.player.joined": case "multiplayer.player.left": case "multiplayer.player.joined.renamed":
                        break;
                    default:
                        return;
                }
            }
            else
                return;
        }
        inGameHud.addChatMessage(type, message, sender);
    }
}
