/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash;

import de.chojo.jdautil.interactions.slash.structure.Route;
import de.chojo.jdautil.interactions.slash.structure.builder.GroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.builder.components.PartialGroupBuilder;
import de.chojo.jdautil.interactions.slash.structure.meta.RouteMeta;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Group implements Route<RouteMeta> {
    private static final Logger log = getLogger(Group.class);
    private final RouteMeta meta;
    private final List<SubCommand> subCommands;

    public Group(RouteMeta routeMeta, List<SubCommand> subCommands) {
        meta = routeMeta;
        this.subCommands = subCommands;
    }

    public static PartialGroupBuilder of(String name, String description) {
        return GroupBuilder.of(name, description);
    }

    @Override
    public RouteMeta meta() {
        return meta;
    }

    public void onSlashCommand(SlashCommandInteractionEvent event, EventContext context) {
        var commandPath = event.getCommandPath().split("/?%s/?".formatted(meta.name()));

        if (commandPath.length != 2) {
            log.warn("end of route is reached on a branch at {}", event.getCommandPath());
            return;
        }

        for (var route : subCommands) {
            if (commandPath[1].equalsIgnoreCase(route.meta().name())) {
                route.onSlashCommand(event, context);
                return;
            }
        }
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, EventContext context) {
        var commandPath = event.getCommandPath().split("/");
        for (var route : subCommands) {
            if (commandPath[1].equalsIgnoreCase(route.meta().name())) {
                route.onAutoComplete(event, context);
                return;
            }
        }
    }

    @Override
    public Collection<Collection<? extends Route<RouteMeta>>> routes() {
        return Collections.singletonList(subCommands);
    }

    public SubcommandGroupData data(ILocalizer localizer) {
        return new SubcommandGroupData(meta.name(), localizer.localize(meta.description(), LocaleProvider.empty()))
                .addSubcommands(subCommands.stream().map(s -> s.data(localizer)).toList());
    }
}
