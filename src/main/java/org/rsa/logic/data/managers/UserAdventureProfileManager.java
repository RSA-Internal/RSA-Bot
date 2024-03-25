package org.rsa.logic.data.managers;

import org.rsa.aws.RequestsManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

public class UserAdventureProfileManager {
    private final static String TABLE_NAME = "user_adventure_profiles";
    private final static RequestsManager<UserAdventureProfile> requestsManager = new RequestsManager<>(TABLE_NAME, UserAdventureProfile.class);

    public static UserAdventureProfile fetch(String guildId, String userId) {
        QueryConditional queryConditional = QueryConditional
            .keyEqualTo(Key.builder()
                .partitionValue(guildId)
                .sortValue(userId)
                .build());
        Optional<Page<UserAdventureProfile>> optionalUserAdventureProfile = requestsManager.fetchSingleItem(queryConditional);
        if (optionalUserAdventureProfile.isEmpty() || optionalUserAdventureProfile.get().items().isEmpty()) {
            UserAdventureProfile newProfile = new UserAdventureProfile(guildId, userId);
            update(newProfile);
            return newProfile;
        }

        UserAdventureProfile adventureProfile = optionalUserAdventureProfile.get().items().get(0);
        if (adventureProfile.validateProfile()) {
            update(adventureProfile);
        }
        return adventureProfile;
    }

    public static void update(UserAdventureProfile userAdventureProfile) {
        requestsManager.enqueueItemUpdate(userAdventureProfile);
    }
}
