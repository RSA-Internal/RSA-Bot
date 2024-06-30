package org.rsa.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.rsa.context.contextitems.message.AcceptAnswerContextItem;
import org.rsa.context.contextitems.message.ClosePostAsSpamContextItem;

import java.util.HashMap;

@Slf4j
public class ContextItems {

    @Getter
    private static final HashMap<String, MessageContextObject> loadedMessageItems = new HashMap<>();
    @Getter
    private static final HashMap<String, UserContextObject> loadedUserItems = new HashMap<>();

    static {
        log.info("Loading context menu items.");

        addMessageItem(new AcceptAnswerContextItem());
        addMessageItem(new ClosePostAsSpamContextItem());
    }

    private static void addMessageItem(MessageContextObject item)
    {
        log.info("Loading context item (message): " + item.getName());
        loadedMessageItems.put(item.getName(), item);
    }

    private static void addUserItem(UserContextObject item)
    {
        log.info("Loading context item (user): " + item.getName());
        loadedUserItems.put(item.getName(), item);
    }
}
