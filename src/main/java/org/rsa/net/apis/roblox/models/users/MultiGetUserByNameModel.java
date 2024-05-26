package org.rsa.net.apis.roblox.models.users;

import java.util.List;

public record MultiGetUserByNameModel(List<User> data) {
    public record User(String requestedUsername, boolean hasVerifiedBadge, Long id, String name, String displayName) {}
}
