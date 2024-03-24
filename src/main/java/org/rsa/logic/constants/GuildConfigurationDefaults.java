package org.rsa.logic.constants;

import java.util.HashMap;
import java.util.Map;

import static org.rsa.logic.constants.GuildConfigurationConstants.*;
import static org.rsa.logic.constants.ReputationChanges.*;

public class GuildConfigurationDefaults {

    public static final Map<String, String> DEFAULTS = new HashMap<>() {{
       put(UPVOTE_RECEIVED_KEY, String.valueOf(POST_UPVOTE_RECEIVED));
       put(DOWNVOTE_RECEIVED_KEY, String.valueOf(POST_DOWNVOTE_RECEIVED));
       put(DOWNVOTE_GIVEN_KEY, String.valueOf(POST_DOWNVOTE_GIVEN));
       put(ANSWER_ACCEPTED_KEY, String.valueOf(ANSWER_ACCEPT_RECEIVED));
       put(ACCEPTED_ANSWER_KEY, String.valueOf(ANSWER_ACCEPT_GIVEN));
       put(QUESTION_MODERATED_KEY, String.valueOf(POST_MODERATED));
       put(FLAGGED_SPAM_KEY, String.valueOf(POST_CLOSED_FOR_SPAM));
       put(MESSAGE_CHARACTERS_AS_TITLE_KEY, "80");
       put(REQUIRED_CHARACTERS_KEY, "50");
    }};
}
