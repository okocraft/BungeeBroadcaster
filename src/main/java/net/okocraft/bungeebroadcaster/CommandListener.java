package net.okocraft.bungeebroadcaster;

import com.github.siroshun09.sirolibrary.message.BungeeMessage;
import com.github.siroshun09.sirolibrary.text.NumberChecker;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandListener extends Command implements TabExecutor {
    private final static CommandListener INSTANCE = new CommandListener();

    private final Set<String> subCommands = Set.of("add", "append", "period", "prefix", "remove", "reload", "show", "set", "next");
    private final StringBuilder builder = new StringBuilder();
    private final MessageYaml yaml = MessageYaml.get();

    private CommandListener() {
        super("broadcaster", null, "bcr");
    }

    @NotNull
    public static CommandListener get() {
        return INSTANCE;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("broadcaster.command")) {
            send(sender, "&cYou don't have permission.", true);
            return;
        }

        if (0 < args.length) {
            String sub = args[0].toLowerCase();
            if (subCommands.contains(sub)) {
                switch (sub) {
                    case "add":
                        add(sender, args);
                        return;
                    case "append":
                        append(sender, args);
                        return;
                    case "period":
                        period(sender, args);
                        return;
                    case "prefix":
                        prefix(sender, args);
                        return;
                    case "reload":
                        reload(sender);
                        return;
                    case "remove":
                        remove(sender, args);
                        return;
                    case "set":
                        set(sender, args);
                        return;
                    case "show":
                        show(sender);
                        return;
                }
                return;
            }
        }

        send(sender, "&7&b/bcr add <previousIndex> <message>", true);
        send(sender, "&7&b/bcr append <message>", true);
        send(sender, "&7&b/bcr period <seconds>", true);
        send(sender, "&7&b/bcr prefix {prefix}", true);
        send(sender, "&7&b/bcr reload", true);
        send(sender, "&7&b/bcr remove <index>", true);
        send(sender, "&7&b/bcr set <index> <message>", true);
        send(sender, "&7&b/bcr show", true);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return BungeeMessage.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        }

        if (args.length == 2 &&
                (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove"))) {
            List<String> indexList = MessageYaml.get().getIndexList();
            indexList.add("append");
            return BungeeMessage.copyPartialMatches(args[1], indexList, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private void add(@NotNull CommandSender sender, @NotNull String[] args) {
        if (2 < args.length && NumberChecker.isInteger(args[1])) {
            int index = Integer.parseInt(args[1]);
            String msg = join(2, args);
            MessageYaml.get().addMessage(index, msg);
            send(sender, "&7Added message: &r" + msg, true);
        } else {
            send(sender, "&cInvalid format: &b/bcr add <previousIndex> <message>", true);
        }
    }

    private void append(@NotNull CommandSender sender, @NotNull String[] args) {
        if (1 < args.length) {
            String msg = join(1, args);
            MessageYaml.get().addMessage(msg);
            send(sender, "&7Added message: &r" + msg, true);
        } else {
            send(sender, "&cInvalid format: &b/bcr append <message>", true);
        }
    }

    private void period(@NotNull CommandSender sender, @NotNull String[] args) {
        if (1 < args.length && NumberChecker.isLong(args[1])) {
            long period = Long.parseLong(args[1]);
            MessageYaml.get().setPeriod(period);
            send(sender, "&7Set period: &r" + yaml.getPeriod(), true);
        } else {
            send(sender, "&cInvalid format: &b/bcr period <seconds>", true);
        }
    }


    private void prefix(@NotNull CommandSender sender, @NotNull String[] args) {
        if (1 < args.length) {
            String prefix = join(1, args);
            MessageYaml.get().setPrefix(prefix);
            send(sender, "&7Set prefix: &r" + yaml.getPrefix(), true);
        } else {
            send(sender, "&cInvalid format: &b/bcr prefix {prefix}", true);
        }
    }

    private void reload(@NotNull CommandSender sender) {
        MessageYaml.get().reload();
        send(sender, "&7messages.yml has been reloaded.", true);
    }

    private void remove(@NotNull CommandSender sender, @NotNull String[] args) {
        if (1 < args.length && NumberChecker.isInteger(args[1])) {
            MessageYaml.get().removeMessage(Integer.parseInt(args[1]) - 1);
            send(sender, "&7Message " + args[1] + " has been removed!", true);
        } else {
            send(sender, "&cInvalid format: &b/bcr remove <index>", true);
        }
    }

    private void set(@NotNull CommandSender sender, @NotNull String[] args) {
        if (2 < args.length && NumberChecker.isInteger(args[1])) {
            int index = Integer.parseInt(args[1]) - 1;
            String msg = join(2, args);
            MessageYaml.get().setMessage(index, msg);
            send(sender, "&7Set &8[&b" + yaml.getIndex(msg) + "&8]&7 message:&r " + msg, true);
        } else {
            send(sender, "&cInvalid format: &b/bcr set <index> <message>", true);
        }
    }

    private void show(@NotNull CommandSender sender) {
        int i = 1;
        send(sender, "&7Showing all message...", true);
        for (String msg : MessageYaml.get().getAllMessages()) {
            send(sender, "&8[&b" + i + "&8]&r " + msg, false);
            i++;
        }
    }

    @NotNull
    private String join(int startIndex, @NotNull String[] args) {
        builder.setLength(0);
        for (int i = startIndex; i < args.length; i++) {
            if (startIndex != i) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private void send(@NotNull CommandSender sender, @NotNull String msg, boolean prefix) {
        BungeeMessage.sendMessageWithColor(sender, prefix ? "&8[&6Broadcaster&8]&r " + msg : msg);
    }
}
