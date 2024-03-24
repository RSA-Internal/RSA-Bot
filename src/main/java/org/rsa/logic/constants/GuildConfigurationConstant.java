package org.rsa.logic.constants;

import lombok.Getter;

@Getter
public enum GuildConfigurationConstant {
    UPVOTE_EMOJI("upvote_emoji", "Emoji to add an upvote", "👍"),
    DOWNVOTE_EMOJI("downvote_emoji", "Emoji to add a downvote", "👎"),
    MODERATE_EMOJI("moderate_emoji", "Emoji to moderate an emoji", "🚨"),
    ACCEPT_EMOJI("accept_emoji", "Emoji to mark an answer as accepted", "✅"),
    MODERATOR_ROLE("moderator_role_id", "Role id for moderators", ""),
    HELPER_ROLE("helper_role_id", "Role id for helpers", ""),
    RESOLVER_ROLE("resolver_role_id", "Role id for resolvers", ""),
    HELP_CHANNEL("help_channel_id", "Channel id for the designated help channel", ""),
    REQUIRED_CHARACTERS("req_chars_for_help_thread", "Number of characters required to create a thread in the help channel", "50"),
    HELP_TITLE_LENGTH("help_thread_title_length", "Length of message to use as thread title", "80"),
    RESOLVER_REPUTATION("reputation_for_resolve", "Reputation to provide when a user resolves a question", "1"),
    HELPER_REPUTATION("reputation_for_helping", "Reputation to provide evenly among helps when a question is resolved", "10"),
    UPVOTE_RECEIVED("upvote_received", "Reputation to provide when an upvote is received", "5"),
    DOWNVOTE_RECEIVED("downvote_received", "Reputation to provide when an downvote is received", "-2"),
    DOWNVOTE_GIVEN("downvote_given", "Reputation to provide when a downvote is given", "-1"),
    ANSWER_ACCEPTED("answer_accepted", "Reputation to provide to an asker upon accepting an answer", "2"),
    ACCEPTED_ANSWER("accepted_answer", "Reputation to provide to a helper upon answer being accepted", "15"),
    QUESTION_MODERATED("question_moderated", "Reputation to provide when a question is moderated", "-5"),
    FLAGGED_SPAM("flagged_spam", "Reputation to provide when a question is marked as spam", "-50");

    private final String key;
    private final String localization;
    private final String defaultValue;

    GuildConfigurationConstant(String key, String localization, String defaultValue) {
        this.key = key;
        this.localization = localization;
        this.defaultValue = defaultValue;
    }
}
