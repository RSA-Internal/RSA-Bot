package org.rsa.net.apis.discourse.models;

import java.util.List;

public record CategoryTopicsModel(TopicList topicList) {
    public record TopicList(List<Topic> topics) {
        public record Topic(String id, boolean pinned) {}
    }
}
