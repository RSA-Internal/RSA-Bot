package org.rsa.discourse.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CategoryDetailsModel {
    private String id;
    private String name;
    private String hexColor;

    public CategoryDetailsModel(String id, String name, String hexColor) {
        this.id = id;
        this.name = name;
        this.hexColor = hexColor;
    }

    public static class CategoriesResponse {
        List<Category> categories;

        static class Category {
            String id;
            String name;
            String color;
        }
    }

    public static Map<String, CategoryDetailsModel> fromCategoriesResponse(CategoriesResponse response) {
        Map<String, CategoryDetailsModel> categoryDetailsList = new HashMap<>();
        for (CategoriesResponse.Category category: response.categories) {
            categoryDetailsList.put(category.id, new CategoryDetailsModel(category.id, category.name, category.color));
        }

        return categoryDetailsList;
    }
}
