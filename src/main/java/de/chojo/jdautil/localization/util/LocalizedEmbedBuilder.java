package de.chojo.jdautil.localization.util;

import de.chojo.jdautil.localization.Localizer;
import de.chojo.jdautil.wrapper.MessageEventWrapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;

/**
 * Wrapper for auto localization of embeds.
 */
public class LocalizedEmbedBuilder extends EmbedBuilder {
    private final Guild guild;
    private final Localizer localizer;

    /**
     * Creates a new localized embed builder.
     *
     * @param messageContext message context for guild and language detection
     */
    public LocalizedEmbedBuilder(Localizer localizer, MessageEventWrapper messageContext) {
        this.localizer = localizer;
        this.guild = messageContext != null ? messageContext.getGuild() : null;
    }

    public LocalizedEmbedBuilder(Localizer localizer, @Nullable Guild guild) {
        this.guild = guild;
        this.localizer = localizer;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addField(@Nullable String name, @Nullable String value, boolean inline) {
        super.addField(localizer.localize(name, guild), localizer.localize(value, guild), inline);
        return this;
    }

    @Nonnull
    @Override
    @Deprecated
    public LocalizedEmbedBuilder addField(@Nullable MessageEmbed.Field field) {
        super.addField(field);
        return this;
    }

    /**
     * Add a localized field.
     *
     * @param field localized field to add.
     *
     * @return the builder after the field has been set
     */
    public LocalizedEmbedBuilder addField(LocalizedField field) {
        return addField(field.getField());
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title) {
        super.setTitle(localizer.localize(title, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
        super.setTitle(localizer.localize(title, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text) {
        super.setFooter(localizer.localize(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
        super.setFooter(localizer.localize(text, guild), iconUrl);
        return this;
    }

    /**
     * Set the description with auto translation.
     *
     * @param text text to set
     *
     * @return the builder after the description has been set
     */
    public LocalizedEmbedBuilder setDescription(String text) {
        super.setDescription(localizer.localize(text, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder appendDescription(@Nonnull CharSequence description) {
        super.appendDescription(localizer.localize(description.toString(), guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clear() {
        super.clear();
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
        super.setTimestamp(temporal);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(@Nullable Color color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setColor(int color) {
        super.setColor(color);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setThumbnail(@Nullable String url) {
        super.setThumbnail(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setImage(@Nullable String url) {
        super.setImage(url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name) {
        super.setAuthor(localizer.localize(name, guild));
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
        super.setAuthor(localizer.localize(name, guild), url);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        super.setAuthor(localizer.localize(name, guild), url, iconUrl);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder addBlankField(boolean inline) {
        super.addBlankField(inline);
        return this;
    }

    @Nonnull
    @Override
    public LocalizedEmbedBuilder clearFields() {
        super.clearFields();
        return this;
    }

}
