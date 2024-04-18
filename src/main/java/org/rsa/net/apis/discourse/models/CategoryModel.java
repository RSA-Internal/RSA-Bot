package org.rsa.net.apis.discourse.models;

import java.util.List;

public record CategoryModel(List<Category> categories) {
    public record Category(String id, String name, String color) {}
}
