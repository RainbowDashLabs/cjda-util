/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command;

import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.Language;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMeta {
    private final String name;
    private final String description;
    private final SimpleArgument[] argument;
    private final SimpleSubCommand[] subCommands;
    private final boolean defaultEnabled;

    CommandMeta(String name, String description, SimpleArgument[] argument, SimpleSubCommand[] subCommands, boolean defaultEnabled) {
        this.name = name;
        this.description = description;
        this.argument = argument;
        this.subCommands = subCommands;
        this.defaultEnabled = defaultEnabled;
    }

    public static CommandMetaBuilder builder(String name, String description) {
        return new CommandMetaBuilder(name, description);
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public SimpleArgument[] argument() {
        return argument;
    }

    public SimpleSubCommand[] subCommands() {
        return subCommands;
    }

    public boolean defaultEnabled() {
        return defaultEnabled;
    }

    public CommandData toCommandData(ILocalizer localizer, Language lang) {
        var commandData = Commands.slash(name, localizer.localize(description, lang));
        if (subCommands() != null) {
            List<SubcommandData> subcommands = new ArrayList<>(subCommands().length);
            for (var subCommand : subCommands()) {
                var subCmdData = new SubcommandData(subCommand.name(), localizer.localize(subCommand.description(), lang));
                for (var arg : subCommand.args()) {
                    subCmdData.addOption(arg.type(), arg.name(), localizer.localize(arg.description(), lang), arg.isRequired());
                }
                subcommands.add(subCmdData);
            }
            commandData.addSubcommands(subcommands);
        } else if (argument() != null) {
            for (var arg : argument()) {
                commandData.addOption(arg.type(), arg.name(), localizer.localize(arg.description(), lang), arg.isRequired(), arg.autoComplete());
            }
        }

        if (argument() != null && subCommands() != null) {
            throw new IllegalStateException("Commands can't have subcommands and arguments... Sorry.");
        }

        commandData.setDefaultEnabled(!defaultEnabled());

        return commandData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandMeta)) return false;

        CommandMeta that = (CommandMeta) o;

        if (defaultEnabled != that.defaultEnabled) return false;
        if (!name.equals(that.name)) return false;
        if (!description.equals(that.description)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(argument, that.argument)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(subCommands, that.subCommands);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + Arrays.hashCode(argument);
        result = 31 * result + Arrays.hashCode(subCommands);
        result = 31 * result + (defaultEnabled ? 1 : 0);
        return result;
    }
}
