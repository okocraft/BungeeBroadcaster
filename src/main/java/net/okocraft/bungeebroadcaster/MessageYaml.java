package net.okocraft.bungeebroadcaster;

import com.github.siroshun09.sirolibrary.config.BungeeYaml;
import com.github.siroshun09.sirolibrary.message.BungeeMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageYaml extends BungeeYaml {
    private final static MessageYaml INSTANCE = new MessageYaml();

    private final List<String> messages = new ArrayList<>();
    private int nextIndex = 0;

    private MessageYaml() {
        super(BungeeBroadcaster.get().getDataFolder().toPath().resolve("messages.yml"), false);
    }

    @NotNull
    public static MessageYaml get() {
        return INSTANCE;
    }

    public long getPeriod() {
        long value = getLong("period", 300);
        return 0 < value ? value : 300;
    }

    public void setPeriod(long period) {
        if (config != null && 0 < period) {
            config.set("period", period);
            super.save();
        }
    }

    public String getPrefix() {
        return getString("prefix", "&7* ");
    }

    public void setPrefix(@NotNull String prefix) {
        if (config != null) {
            config.set("prefix", prefix);
            super.save();
            BungeeBroadcaster.get().reschedule();
        }
    }

    @NotNull
    public List<String> getAllMessages() {
        return List.copyOf(messages);
    }

    public int getIndex(@NotNull String message) {
        return messages.indexOf(message) + 1;
    }

    public int getMaxIndex() {
        return messages.size() - 1;
    }

    public List<String> getIndexList() {
        List<String> numbers = new ArrayList<>();
        int i = 1, max = getMaxIndex() + 1;
        while (i < max) {
            numbers.add(String.valueOf(i));
            i++;
        }
        return numbers;
    }

    @NotNull
    public String getMessage(int index) {
        if (checkIndex(index)) {
            return messages.get(index);
        } else {
            return "";
        }
    }

    public void addMessage(@NotNull String message) {
        messages.add(message);
        save();
    }

    public void addMessage(int index, @NotNull String message) {
        if (checkIndex(index)) {
            messages.add(index, message);
            save();
        } else {
            addMessage(message);
        }
    }

    public void removeMessage(int index) {
        if (checkIndex(index)) {
            messages.remove(index);
            save();
        }
    }

    public void setMessage(int index, @NotNull String message) {
        if (checkIndex(index)) {
            messages.set(index, message);
            save();
        } else {
            addMessage(message);
        }
    }

    public void next() {
        if (nextIndex == getMaxIndex()) {
            nextIndex = 0;
        } else {
            nextIndex++;
        }
    }

    public void broadcast() {
        broadcast(nextIndex);
        next();
    }

    public void broadcast(int index) {
        if (checkIndex(index)) {
            BungeeMessage.broadcastWithColor(getPrefix() + getMessage(index));
        }
    }

    @Override
    public void load() {
        super.load();
        messages.clear();
        messages.addAll(getStringList("messages"));
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public void save() {
        if (config != null) {
            config.set("messages", messages);
            super.save();
        }
    }

    private boolean checkIndex(int index) {
        return 0 <= index && index < messages.size();
    }
}
