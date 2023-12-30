package org.rsa.command;

import lombok.Getter;
import org.rsa.command.contextitems.message.AcceptAnswerContextItem;

import java.util.HashMap;

public class ContextItems {
    @Getter
    private static final HashMap<String, MessageContextObject> loadedMessageItems = new HashMap<>();
    @Getter
    private static final HashMap<String, UserContextObject> loadedUserItems = new HashMap<>();

    static {
        System.out.println("Loading context menu items.");

        addMessageItem(new AcceptAnswerContextItem());
    }

    private static void addMessageItem(MessageContextObject item)
    {
        System.out.println("Loading context item (message): " + item.getName());
        loadedMessageItems.put(item.getName(), item);
    }

    private static void addUserItem(UserContextObject item)
    {
        System.out.println("Loading context item (user): " + item.getName());
        loadedUserItems.put(item.getName(), item);
    }
}
