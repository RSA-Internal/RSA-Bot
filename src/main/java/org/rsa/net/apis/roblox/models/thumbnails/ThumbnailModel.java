package org.rsa.net.apis.roblox.models.thumbnails;

import java.util.List;

public record ThumbnailModel(List<ThumbnailResult> data) {
    public record ThumbnailResult(Long targetId, String state, String imageUrl) {}
}
