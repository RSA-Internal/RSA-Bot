package org.rsa.aws.bedrock;

import software.amazon.awssdk.services.bedrockruntime.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ConverseContext {
    List<Message> messages = new ArrayList<>();
    long lastAccess;
    String personality = null;

    public void addMessage(Message message) {
        messages.add(message);
    }
}
