package org.rsa.logic.constants;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum GuildConfigurationConstant {
    UPVOTE_EMOJI("upvote_emoji", "Emoji to add an upvote", "👍"),
    DOWNVOTE_EMOJI("downvote_emoji", "Emoji to add a downvote", "👎"),
    MODERATE_EMOJI("moderate_emoji", "Emoji to moderate an emoji", "🚨"),
    ACCEPT_EMOJI("accept_emoji", "Emoji to mark an answer as accepted", "✅"),
    MODERATOR_ROLE("moderator_role", "Role for moderators", ""),
    HELPER_ROLE("helper_role", "Role for helpers", ""),
    RESOLVER_ROLE("resolver_role", "Role for resolvers", ""),
    HELP_CHANNEL("help_channel", "Channel for the designated help channel", ""),
    REQUIRED_CHARACTERS("required_characters", "Number of characters required to create a thread in the help channel", "50"),
    HELP_TITLE_LENGTH("help_title_length", "Length of message to use as thread title", "80"),
    RESOLVER_REPUTATION("resolver_reputation", "Reputation to provide when a user resolves a question", "1"),
    HELPER_REPUTATION("helper_reputation", "Reputation to provide evenly among helps when a question is resolved", "10"),
    UPVOTE_RECEIVED("upvote_received", "Reputation to provide when an upvote is received", "5"),
    DOWNVOTE_RECEIVED("downvote_received", "Reputation to provide when an downvote is received", "-2"),
    DOWNVOTE_GIVEN("downvote_given", "Reputation to provide when a downvote is given", "-1"),
    ANSWER_ACCEPTED("answer_accepted", "Reputation to provide to an asker upon accepting an answer", "2"),
    ACCEPTED_ANSWER("accepted_answer", "Reputation to provide to a helper upon answer being accepted", "15"),
    QUESTION_MODERATED("question_moderated", "Reputation to provide when a question is moderated", "-5"),
    FLAGGED_SPAM("flagged_spam", "Reputation to provide when a question is marked as spam", "-50");


    public static final List<GuildConfigurationConstant> EMOJI = new ArrayList<>() {{
        add(UPVOTE_EMOJI);
        add(DOWNVOTE_EMOJI);
        add(MODERATE_EMOJI);
        add(ACCEPT_EMOJI);
    }};
    public static final List<GuildConfigurationConstant> ROLE = new ArrayList<>() {{
       add(MODERATOR_ROLE);
       add(HELPER_ROLE);
       add(RESOLVER_ROLE);
    }};

    public static final List<GuildConfigurationConstant> CHANNEL = new ArrayList<>() {{
        add(HELP_CHANNEL);
    }};

    public static final List<GuildConfigurationConstant> OPTION = new ArrayList<>() {{
        add(REQUIRED_CHARACTERS);
        add(HELP_TITLE_LENGTH);
    }};

    public static final List<GuildConfigurationConstant> REPUTATION = new ArrayList<>() {{
        add(RESOLVER_REPUTATION);
        add(HELPER_REPUTATION);
        add(UPVOTE_RECEIVED);
        add(DOWNVOTE_RECEIVED);
        add(DOWNVOTE_GIVEN);
        add(ANSWER_ACCEPTED);
        add(ACCEPTED_ANSWER);
        add(QUESTION_MODERATED);
        add(FLAGGED_SPAM);
    }};

    public static final String EMOJI_LIST_KEY = "Emoji Settings";
    public static final String ROLE_LIST_KEY = "Role Settings";
    public static final String CHANNEL_LIST_KEY = "Channel Settings";
    public static final String OPTION_LIST_KEY = "Guild Options";
    public static final String REPUTATION_LIST_KEY = "Reputation Settings";

    public static final Map<String, List<GuildConfigurationConstant>> LIST = new HashMap<>() {{
        put(EMOJI_LIST_KEY, EMOJI);
        put(ROLE_LIST_KEY, ROLE);
        put(CHANNEL_LIST_KEY, CHANNEL);
        put(OPTION_LIST_KEY, OPTION);
        put(REPUTATION_LIST_KEY, REPUTATION);
    }};

    private final String key;
    private final String localization;
    private final String defaultValue;

    GuildConfigurationConstant(String key, String localization, String defaultValue) {
        this.key = key;
        this.localization = localization;
        this.defaultValue = defaultValue;
    }
}
