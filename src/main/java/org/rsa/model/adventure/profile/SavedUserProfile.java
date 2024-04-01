package org.rsa.model.adventure.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@DynamoDbBean
@Getter
@Setter
@AllArgsConstructor
public class SavedUserProfile {
    Integer schema_version = 1;

    String guildId;
    String userId;

    /* Wallet -> CurrentId: Quantity */
    Map<Integer, BigInteger> wallet;

    /* Backpack ->
       key: "S" -> itemId + "_" + rarity
       value: "M" -> {
          "Quantity": "24",
          "Durability": "-1",
          "otherData": "otherValue"
       }
     */
    Map<String, Map<String, String>> backpack;

    /* Equipment ->
        key: "N" -> slotId
        value: "M" -> {
            "ItemId": "4",
            "Rarity": "Common",
            "Durability": "-1",
            "otherData": "otherValue"
        }
     */
    Map<Integer, Map<String, String>> equipment;

    /* Skill ->
        key: "N" -> skillId,
        value: "M" -> {
            "Level": 0,
            "Experience": 0
        }
     */
    Map<Integer, Map<String, Integer>> skills;

    List<Integer> unlockedAchievements;
    List<Integer> unlockedZones;
    Map<Integer, BigInteger> activitiesPerformed;

    /* Options
        key: "S" -> optionName
        value: "S" -> optionValue
     */
    Map<String, String> userOptions;
}
