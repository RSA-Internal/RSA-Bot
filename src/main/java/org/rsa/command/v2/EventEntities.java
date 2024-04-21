package org.rsa.command.v2;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.rsa.exception.ValidationException;

import java.util.Objects;

@Getter
public class EventEntities<T> {

    @NotNull
    private final T event;

    @NotNull
    private final Guild guild;

    @NotNull
    private final Member requester;

    public EventEntities(@NotNull T event) throws ValidationException {
        this.event = event;

        GenericCommandInteractionEvent genericEvent = (GenericCommandInteractionEvent) event;

        Guild guild = genericEvent.getGuild();
        Member requester = genericEvent.getMember();

        if (Objects.isNull(guild)) {
            throw new ValidationException("guild", "Guild was null in event provided to EventEntities.");
        }

        if (Objects.isNull(requester)) {
            throw new ValidationException("requester", "Member (requester) was null in event provided to EventEntities.");
        }

        this.guild = guild;
        this.requester = requester;
    }
}