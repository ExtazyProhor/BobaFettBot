package com.prohor.personal.bobaFettBot.bot.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotPrefixService<Task extends Identifiable<String>> {
    private final List<Task> list = new ArrayList<>();

    @SafeVarargs
    public BotPrefixService(Task... tasks) {
        list.addAll(Arrays.asList(tasks));
    }

    public Task getTask(String prefix) {
        for (Task task : list)
            if (task.getIdentifier().startsWith(prefix))
                return task;
        return null;
    }

    public boolean hasTask(String prefix) {
        return getTask(prefix) != null;
    }
}