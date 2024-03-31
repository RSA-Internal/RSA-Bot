package org.rsa.entity.recipe;

import lombok.Getter;
import org.rsa.entity.BaseEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.model.adventure.recipe.Recipe;
import org.rsa.model.adventure.recipe.RecipeEntry;
import org.rsa.register.adventure.EntityManagerRegister;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RecipeEntity extends BaseEntity {

    private final List<RecipeEntry> inputs;
    private final List<RecipeEntry> outputs;

    public static RecipeEntity fromEnum(Recipe recipe) {
        return new RecipeEntity(recipe.getId(), recipe.getName(), recipe.getInputs(), recipe.getOutputs());
    }

    public RecipeEntity(Integer id, String name, List<RecipeEntry> inputs, List<RecipeEntry> outputs) {
        super(id, name);
        this.inputs = inputs;
        this.outputs = outputs;
        EntityManagerRegister.recipeManager.addEntity(this);
    }

    @Override
    public String getAsDetails() {
        return "Not yet implemented.";
    }

    public boolean canCraft(UserAdventureProfile profile) {
        for (RecipeEntry entry : inputs) {
            if (entry.getCount() > profile.queryBackpack(entry.getItem().getId())) return false;
        }

        return true;
    }

    public boolean performCraft(UserAdventureProfile profile) {
        boolean cont = canCraft(profile);
        if (cont) {
            List<RecipeEntry> success = new ArrayList<>();
            boolean failed = false;

            for (RecipeEntry entry : inputs) {
                if (!profile.updateBackpack(entry.getItem().getId(), -entry.getCount())) {
                    failed = true;
                } else {
                    success.add(entry);
                }
            }

            if (failed) {
                for (RecipeEntry refundItem : success) {
                    profile.updateBackpack(refundItem.getItem().getId(), refundItem.getCount());
                }
            } else {
                for (RecipeEntry outputItem : outputs) {
                    profile.updateBackpack(outputItem.getItem().getId(), outputItem.getCount());
                }
                UserAdventureProfileManager.update(profile);
            }

            return !failed;
        }

        return false;
    }

    public boolean containsQuery(String query) {
        return false;
    }
}
