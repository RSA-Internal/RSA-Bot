package org.rsa.net.apis.discourse.models;

import java.util.List;

public record TopicModel(String created_at, String id, String title, Details details, PostStream post_stream) {
    public record PostStream(List<Post> posts) {}
    public record Post(String cooked) {}
    public record Details(CreatedBy created_by) {
        public record CreatedBy(String username, String avatar_template) {}
    }
}
