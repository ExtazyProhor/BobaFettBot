package com.prohor.personal.bobaFettBot.bot.objects;

import java.util.*;

public class BotService<Identifier, Task extends Identifiable<Identifier>> {
    private final Map<Identifier, Task> map = new HashMap<>();

    @SafeVarargs
    public BotService(Task... tasks) {
        Arrays.stream(tasks).forEach(x -> map.put(x.getIdentifier(), x));
    }

    public Task getTask(Identifier identifier) {
        return map.get(identifier);
    }

    public boolean hasTask(Identifier identifier) {
        return map.containsKey(identifier);
    }

    public Collection<Task> getAllCommands() {
        return map.values();
    }
}
