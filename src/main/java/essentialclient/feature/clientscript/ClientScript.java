package essentialclient.feature.clientscript;

import essentialclient.feature.clientrule.ClientRules;
import essentialclient.feature.keybinds.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.CommandHelper;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasListExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientScript {
    /**
     * This is the ThreadGroup that all script threads should be inside.
     *
     * If a script thread is not generated inside a thread it is possible
     * it will never stop executing.
     */
    public static final ThreadGroup arucasThreadGroup = new ThreadGroup("Arucas Thread Group");
    public static final Object ERROR_LOCK = new Object();

    private static Context context;
    private static Thread thread;

    private static boolean hasErrored;

    public static void registerKeyPress() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinding clientKeybind = ClientKeybinds.CLIENT_SCRIPT.getKeyBinding();
            if (clientKeybind.isPressed() && !clientKeybind.wasPressed()) {
                clientKeybind.setPressed(false);
                ClientScript.toggleScript();
            }
        });
    }
    
    public static boolean isScriptRunning() {
        return context != null;
    }
    
    public static void toggleScript() {
        boolean running = isScriptRunning();
        if (running) {
            stopScript();
        } else {
            startScript();
        }
        
        EssentialUtils.sendMessageToActionBar("§6Script is now " + (running ? "§cOFF" : "§aON"));
    }
    
    public synchronized static void startScript() {
        if (isScriptRunning()) {
            // We need to close the already running code?????
            stopScript();
        }
        hasErrored = false;
        executeScript();
    }
    
    public synchronized static void stopScript() {
        // Interrupt the thread group to close all threads.
        arucasThreadGroup.interrupt();
        
        // Set the context and thread reference to null.
        context = null;
        thread = null;
        
        // Clear custom commands and keybinds.
        CommandHelper.functionCommand.clear();
        resetKeys(MinecraftClient.getInstance());
        
        // Tell the client that the script has finished executing.
        EssentialUtils.sendMessageToActionBar("§6Script is now §cOFF");
    }
    
    public static Path getFile() {
        return getDir().resolve(ClientRules.CLIENT_SCRIPT_FILENAME.getString() + ".arucas");
    }
    
    public static Path getDir() {
        return EssentialUtils.getEssentialConfigFile().resolve("Scripts");
    }
    
    private static synchronized void executeScript() {
        // Read the text content of the script to run.
        final String fileName = ClientRules.CLIENT_SCRIPT_FILENAME.getString();
        final String fileContent;
        try {
            fileContent = Files.readString(getFile());
        }
        catch (IOException e) {
            EssentialUtils.sendMessage("§cAn error occurred while trying to read the script");
            e.printStackTrace();
            return;
        }
        
        // When we execute a script it is important that all other
        // currently running scripts are closed.
        // stopScript();
        
        // Create a new context for the file we should run.
        Context context = new ContextBuilder()
            .setDisplayName("Arucas client")
            .setExtensions(ArucasBuiltInExtension.class, ArucasListExtension.class, ArucasMinecraftExtension.class)
            .create();
        
        // Change the client script context to the new context.
        ClientScript.context = context;
        
        // Create a new deamon thread.
        ClientScript.thread = new Thread(arucasThreadGroup, () -> {
            try {
                Run.run(context, fileName, fileContent);
            }
            catch (ThrowStop e) {
                EssentialUtils.sendMessage("§c%s".formatted(e.toString(context)));
            }
            catch (CodeError e) {
                if (e.errorType != CodeError.ErrorType.INTERRUPTED_ERROR) {
                    synchronized (ERROR_LOCK) {
                        if (hasErrored) {
                            return;
                        }
                        EssentialUtils.sendMessage("§cAn error occurred while running the script");
                        EssentialUtils.sendMessage("§c--------------------------------------------\n" + e.toString(context));
                        hasErrored = true;
                    }
                }
            }
            catch (Throwable t) {
                ClientScript.sendReportMessage(t, fileContent);
                t.printStackTrace();
            }
            finally {
                ClientScript.stopScript();
            }
        }, "Client Script Thread");
        ClientScript.thread.setDaemon(true);
        ClientScript.thread.start();
    }
    
    public static synchronized void runRootAsyncFunction(ThrowableConsumer<Context> consumer) {
        if (!ClientScript.isScriptRunning()) {
            return;
        }
        
        ClientScript.runAsyncFunction(ClientScript.context.createRootBranch(), consumer);
    }
    
    public static synchronized void runBranchAsyncFunction(ThrowableConsumer<Context> consumer) {
        if (!ClientScript.isScriptRunning()) {
            return;
        }
    
        ClientScript.runAsyncFunction(ClientScript.context.createBranch(), consumer);
    }
    
    private static synchronized void runAsyncFunction(final Context context, ThrowableConsumer<Context> consumer) {
        Thread thread = new Thread(ClientScript.arucasThreadGroup, () -> {
            try {
                consumer.accept(context);
            }
            catch (CodeError e) {
                if (e.errorType != CodeError.ErrorType.INTERRUPTED_ERROR) {
                    synchronized (ERROR_LOCK) {
                        if (hasErrored) {
                            return;
                        }
                        EssentialUtils.sendMessage("§cAn error occurred while running the script");
                        EssentialUtils.sendMessage("§c--------------------------------------------\n" + e.toString(context));
                        hasErrored = true;
                        ClientScript.stopScript();
                    }
                }
            }
            catch (Throwable t) {
                ClientScript.sendReportMessage(t);
                t.printStackTrace();
                ClientScript.stopScript();
            }
        }, "Client Runnable Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private static void sendReportMessage(Throwable t) {
        sendReportMessage(t, null);
    }

    private static void sendReportMessage(Throwable t, String content) {
        String gitReport = getGithubLink(t, content);
        EssentialUtils.sendMessage("§cAn error occurred while running the script");
        EssentialUtils.sendMessage("§cIf you believe this is a bug please report it");
        EssentialUtils.sendMessage(
            new LiteralText("https://github.com/senseiwells/EssentialClient/issues/new")
                .formatted(Formatting.UNDERLINE)
                .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    gitReport))
                )
            .append("\n")
        );
    }

    private static String getGithubLink(Throwable t, String content) {
        String stacktrace = ExceptionUtils.getStackTrace(t);
        int charsLeft = 1400 - stacktrace.length();
        String report = """
        ### Minecraft Version: `%s`
        ### Essential Client Version: `%s`
        ### Arucas Version: `%s`
        ### Script:
        ```kotlin
        %s
        ```
        ### Crash:
        ```
        %s
        ```
        """.formatted(
            EssentialUtils.getMinecraftVersion(),
            EssentialUtils.getVersion(),
            EssentialUtils.getArucasVersion(),
            content == null || content.length() > charsLeft ? "'Script could not be included please send it manually" : content,
            stacktrace
        );
        return "https://github.com/senseiwells/EssentialClient/issues/new?title=ClientScript%20Crash&body=" + URLEncoder.encode(report, StandardCharsets.UTF_8);
    }

    public static void resetKeys(MinecraftClient client) {
        client.options.keySneak.setPressed(false);
        client.options.keyForward.setPressed(false);
        client.options.keyAttack.setPressed(false);
        client.options.keyUse.setPressed(false);
    }
    
    public interface ThrowableConsumer<T> {
        void accept(T obj) throws Throwable;
    }
}
