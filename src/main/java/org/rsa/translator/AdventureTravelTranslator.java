package org.rsa.translator;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.model.Activity;
import org.rsa.adventure.model.ActivityResponse;
import org.rsa.entity.adventure.ActivityEntity;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.ArrayList;
import java.util.List;

import static org.rsa.util.StringUtil.appendElement;

public class AdventureTravelTranslator {

    public static List<ItemComponent> getTravelComponents(UserAdventureProfile adventureProfile, ZoneEntity zone) {
        List<ItemComponent> components = new ArrayList<>();

        for (ActivityEntity activity : zone.getActivities()) {
            ActivityResponse canPerform = activity.userCanPerformActivity(adventureProfile, true);
            String label = activity.getName();
            if (!canPerform.isResult()) {
                label = activity.getName() + " [" + canPerform.getResponse() + "]";
            }

            Button button;

            if (activity.getId().equals(Activity.LEAVE.getId())) {
                button = Button.primary("travel_" + activity.getId(), activity.getName());
            } else {
                button = Button
                    .success("travel_" + activity.getId(), label)
                    .withDisabled(!canPerform.isResult());
            }

            components.add(button);
        }

        return components;
    }

    @NotNull
    public static StringBuilder getTravelStringBuilder(String requiredLevels, String requiredItems, Integer experienceBound, String possibleItems) {
        StringBuilder requiredDisplay = new StringBuilder();

        appendElement(requiredDisplay, "Required Levels", requiredLevels);
        appendElement(requiredDisplay, "Required Items", requiredItems);
        requiredDisplay.append("\n~~---------------------~~\n**Experience Gain**:\n");
        requiredDisplay.append("- ");
        if (experienceBound > 1) {
            requiredDisplay.append("1 - ");
            requiredDisplay.append(experienceBound);
            requiredDisplay.append(" xp");
        } else {
            requiredDisplay.append("None");
        }
        requiredDisplay.append("\n");
        appendElement(requiredDisplay, "Possible Items", possibleItems);
        return requiredDisplay;
    }
}
