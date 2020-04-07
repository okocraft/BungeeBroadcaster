package net.okocraft.bungeebroadcaster;

import com.github.siroshun09.sirolibrary.bungeeutils.BungeeUtil;
import com.github.siroshun09.sirolibrary.message.BungeeMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class BungeeBroadcaster extends Plugin {
    private static BungeeBroadcaster INSTANCE;

    public BungeeBroadcaster() {
        if (INSTANCE != null) {
            throw new IllegalStateException("BungeeBroadcaster is already initialized.");
        }
        INSTANCE = this;
    }

    @NotNull
    public static BungeeBroadcaster get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("BungeeBroadcaster is not initialized.");
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        MessageYaml.get().load();
        BungeeUtil.registerCommand(this, CommandListener.get());
        schedule();
        BungeeMessage.printEnabledMsg(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ProxyServer.getInstance().getScheduler().cancel(this);
        MessageYaml.get().save();
        BungeeMessage.printDisabledMsg(this);
    }

    public void reschedule() {
        ProxyServer.getInstance().getScheduler().cancel(this);
        schedule();
    }

    private void schedule() {
        BungeeUtil.runLater(this, this::broadcast, MessageYaml.get().getPeriod(), TimeUnit.SECONDS);
    }

    private void broadcast() {
        MessageYaml.get().broadcast();
        schedule();
    }
}
