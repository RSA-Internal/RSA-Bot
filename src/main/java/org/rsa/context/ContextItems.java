package org.rsa.context;

import lombok.Getter;
import org.rsa.context.contextitems.message.AcceptAnswerContextItem;
import org.rsa.context.contextitems.message.ClosePostAsSpamContextItem;

import java.util.HashMap;

public class ContextItems {
    @Getter
    private static final HashMap<String, MessageContextObject> loadedMessageItems = new HashMap<>();
    @Getter
    private static final HashMap<String, UserContextObject> loadedUserItems = new HashMap<>();

    static {
        System.out.println("Loading context menu items.");

        addMessageItem(new AcceptAnswerContextItem());
        addMessageItem(new ClosePostAsSpamContextItem());
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
