package org.rsa.net.apis.discourse.transformers;

import org.rsa.net.apis.discourse.domain.Category;
import org.rsa.net.apis.discourse.models.CategoryModel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryTransformer {

    public Map<String, Category> fromResponse(CategoryModel response) {
        Map<String, Category> categoryDetailsList = new HashMap<>();
        for (CategoryModel.Category category : response.categories()) {
            categoryDetailsList.put(category.id(), new Category(category.id(), category.name(), Color.decode("#" + category.color())));
        }
        return categoryDetailsList;
    }
}
