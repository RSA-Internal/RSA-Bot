package org.rsa.logic.data.models;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import org.rsa.logic.constants.GuildConfigurationConstants;
import org.rsa.logic.constants.GuildConfigurationDefaults;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.rsa.logic.constants.GuildConfigurationConstants.*;

@DynamoDbBean
@Getter
@Setter
public class GuildConfiguration {
    Integer schema_version = 5;
    String guildid; // Partition key

    // Emojis
    String upvote_emoji = "";
    String downvote_emoji = "";
    String moderate_emoji = "";
    String accept_emoji = "";

    // Role IDs
    String moderator_role_id = "";
    String helper_role_id = "";
    String resolver_role_id = "";

    // Channel IDs
    String help_channel_id = "";

    // Options
    String req_chars_for_help_thread = "";
    String help_thread_title_length = "";
    String reputation_for_resolve = "";
    String reputation_for_helping = "";

    // Reputation
    String upvote_received = "";
    String downvote_received = "";
    String downvote_given = "";
    String answer_accepted = "";
    String accepted_answer = "";
    String question_moderated = "";
    String flagged_spam = "";

    public GuildConfiguration() { }
    public GuildConfiguration(String guildId) { guildid = guildId; }

    @DynamoDbPartitionKey
    public String getGuildid()
    {
        return guildid;
    }

    private Map<String, Consumer<String>> setters = new HashMap<>() {{
       put(UPVOTE_EMOJI_KEY, GuildConfiguration.this::setUpvote_emoji);
       put(DOWNVOTE_EMOJI_KEY, GuildConfiguration.this::setDownvote_emoji);
       put(MODERATE_EMOJI_KEY, GuildConfiguration.this::setModerate_emoji);
       put(ACCEPT_EMOJI_KEY, GuildConfiguration.this::setAccept_emoji);
       put(MODERATOR_ROLE_KEY, GuildConfiguration.this::setModerator_role_id);
       put(HELPER_ROLE_KEY, GuildConfiguration.this::setHelper_role_id);
       put(RESOLVER_ROLE_KEY, GuildConfiguration.this::setResolver_role_id);
       put(HELP_CHANNEL_KEY, GuildConfiguration.this::setHelp_channel_id);
       put(REQUIRED_CHARACTERS_KEY, GuildConfiguration.this::setReq_chars_for_help_thread);
       put(MESSAGE_CHARACTERS_AS_TITLE_KEY, GuildConfiguration.this::setHelp_thread_title_length);
       put(REPUTATION_FOR_RESOLVE_KEY, GuildConfiguration.this::setReputation_for_resolve);
       put(REPUTATION_FOR_HELPING_KEY, GuildConfiguration.this::setReputation_for_helping);
       put(UPVOTE_RECEIVED_KEY, GuildConfiguration.this::setUpvote_received);
       put(DOWNVOTE_RECEIVED_KEY, GuildConfiguration.this::setDownvote_received);
       put(DOWNVOTE_GIVEN_KEY, GuildConfiguration.this::setDownvote_received);
       put(ANSWER_ACCEPTED_KEY, GuildConfiguration.this::setAnswer_accepted);
       put(ACCEPTED_ANSWER_KEY, GuildConfiguration.this::setAccepted_answer);
       put(QUESTION_MODERATED_KEY, GuildConfiguration.this::setQuestion_moderated);
       put(FLAGGED_SPAM_KEY, GuildConfiguration.this::setFlagged_spam);
    }};

    private Map<String, Supplier<String>> getters = new HashMap<>() {{
        put(UPVOTE_EMOJI_KEY, GuildConfiguration.this::getUpvote_emoji);
        put(DOWNVOTE_EMOJI_KEY, GuildConfiguration.this::getDownvote_emoji);
        put(MODERATE_EMOJI_KEY, GuildConfiguration.this::getModerate_emoji);
        put(ACCEPT_EMOJI_KEY, GuildConfiguration.this::getAccept_emoji);
        put(MODERATOR_ROLE_KEY, GuildConfiguration.this::getModerator_role_id);
        put(HELPER_ROLE_KEY, GuildConfiguration.this::getHelper_role_id);
        put(RESOLVER_ROLE_KEY, GuildConfiguration.this::getResolver_role_id);
        put(HELP_CHANNEL_KEY, GuildConfiguration.this::getHelp_channel_id);
        put(REQUIRED_CHARACTERS_KEY, GuildConfiguration.this::getReq_chars_for_help_thread);
        put(MESSAGE_CHARACTERS_AS_TITLE_KEY, GuildConfiguration.this::getHelp_thread_title_length);
        put(REPUTATION_FOR_RESOLVE_KEY, GuildConfiguration.this::getReputation_for_resolve);
        put(REPUTATION_FOR_HELPING_KEY, GuildConfiguration.this::getReputation_for_helping);
        put(UPVOTE_RECEIVED_KEY, GuildConfiguration.this::getUpvote_received);
        put(DOWNVOTE_RECEIVED_KEY, GuildConfiguration.this::getDownvote_received);
        put(DOWNVOTE_GIVEN_KEY, GuildConfiguration.this::getDownvote_received);
        put(ANSWER_ACCEPTED_KEY, GuildConfiguration.this::getAnswer_accepted);
        put(ACCEPTED_ANSWER_KEY, GuildConfiguration.this::getAccepted_answer);
        put(QUESTION_MODERATED_KEY, GuildConfiguration.this::getQuestion_moderated);
        put(FLAGGED_SPAM_KEY, GuildConfiguration.this::getFlagged_spam);
    }};

    @DynamoDbIgnore
    public void setSetters(Map<String, Consumer<String>> setters) {
        this.setters = setters;
    }

    @DynamoDbIgnore
    public Map<String, Consumer<String>> getSetters() {
        return setters;
    }

    @DynamoDbIgnore
    public void setGetters(Map<String, Supplier<String>> getters) {
        this.getters = getters;
    }

    @DynamoDbIgnore
    public Map<String, Supplier<String>> getGetters() {
        return getters;
    }

    public boolean updateField(String fieldName, String value) {
        Consumer<String> setter = setters.get(fieldName);
        if (setter != null) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * Attempts to retrieve the value for the provided fieldName or null if the field is not defined.
     * @param fieldName Field name as defined by {@link GuildConfigurationConstants}
     * @return String value of field or null.
     */
    public String getValue(String fieldName) {
        Supplier<String> getter = getters.get(fieldName);
        if (getter != null) {
            return getter.get();
        }
        return null;
    }

    public String setDefault(Guild guild, String fieldName) {
        String defaultValue = GuildConfigurationDefaults.DEFAULTS.getOrDefault(fieldName, "");
        if (!defaultValue.isEmpty() && !defaultValue.isBlank()) {
            GuildConfigurationManager.processUpdate(guild, fieldName, defaultValue);
        }
        return defaultValue;
    }
}
