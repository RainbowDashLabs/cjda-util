/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.slash.structure.builder;

import de.chojo.jdautil.command.slash.Argument;
import de.chojo.jdautil.command.slash.ArgumentBuilder;
import de.chojo.jdautil.command.slash.SlashHandler;
import de.chojo.jdautil.command.slash.structure.Command;
import de.chojo.jdautil.command.slash.structure.Group;
import de.chojo.jdautil.command.slash.structure.SubCommand;
import de.chojo.jdautil.command.slash.structure.builder.components.ExtendableRootBuilder;
import de.chojo.jdautil.command.slash.structure.builder.components.RootArgumentBuilder;
import de.chojo.jdautil.command.slash.structure.builder.components.RootMetaBuilder;
import de.chojo.jdautil.command.slash.structure.meta.CommandMeta;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class which allows to create grouped commands.
 * The limitations are implemented as defined
 * <a href="https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups">here</a>
 */
public class CommandBuilder implements RootArgumentBuilder, ExtendableRootBuilder, RootMetaBuilder, BuildableMetaBuilder {
    private final String name;
    private final String description;
    private DefaultMemberPermissions permission;
    private boolean guildOnly;
    private SlashHandler handler;
    private final List<Group> groups = new ArrayList<>();
    private final List<SubCommand> leaves = new ArrayList<>();
    private final List<Argument> arguments = new ArrayList<>();

    public CommandBuilder(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public ExtendableRootBuilder group(GroupBuilder builder) {
        groups.add(builder.build());
        return this;
    }

    @Override
    public ExtendableRootBuilder subCommand(SubCommandBuilder builder) {
        leaves.add(builder.build());
        return this;
    }

    @Override
    public RootArgumentBuilder command(SlashHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public RootArgumentBuilder argument(ArgumentBuilder argument) {
        arguments.add(argument.build());
        return this;
    }

    @Override
    public RootMetaBuilder withPermission(Permission permission, Permission... permissions) {
        var collect = Arrays.stream(permissions).collect(Collectors.toCollection(HashSet::new));
        collect.add(permission);
        this.permission = DefaultMemberPermissions.enabledFor(collect);
        return this;
    }

    /**
     * Marks a command as public command.
     * <p>
     * This is the default value.
     *
     * @return builder
     */
    @Override
    public RootMetaBuilder publicCommand() {
        this.permission = DefaultMemberPermissions.ENABLED;
        return this;
    }

    /**
     * Marks a command as admin command.
     * <p>
     * The command will only be accessable for administrators of a guild.
     *
     * @return builder
     */
    @Override
    public RootMetaBuilder adminCommand() {
        this.permission = DefaultMemberPermissions.DISABLED;
        return this;
    }

    @Override
    public RootMetaBuilder guildOnly() {
        this.guildOnly = true;
        return this;
    }

    @Override
    public Command build() {
        var meta = new CommandMeta(name, description, permission, guildOnly);
        return new Command(meta, handler, groups, leaves, arguments);
    }
}