/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.voting;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.jdautil.botlist.BotList;
import de.chojo.jdautil.botlist.modules.voting.post.VoteData;
import de.chojo.jdautil.botlist.modules.voting.post.VoteReceiver;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import net.dv8tion.jda.api.entities.User;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class VoteService {
    private static final Logger log = getLogger(VoteService.class);
    private final Javalin voteReceiver;
    private final Consumer<VoteData> voteNotifier;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final List<BotList> checker = new ArrayList<>();

    public VoteService(Javalin voteReceiver, Consumer<VoteData> voteNotifier) {
        this.voteReceiver = voteReceiver;
        this.voteNotifier = voteNotifier;
    }

    public void register(BotList botList) {
        if (botList.voteReceiver() != null) {
            registerWeebhook(botList);
        }

        if (botList.voteChecker() != null) {
            registerVoteChecker(botList);
        }
    }

    private void registerVoteChecker(BotList botList) {
        checker.add(botList);
    }

    private void registerWeebhook(BotList botList) {
        var voteReceiver = botList.voteReceiver();
        var name = botList.name().toLowerCase(Locale.ROOT).replace(".", "");
        var route = "voting/" + name;
        log.info("Registering vote route {} for {}", route, botList.name());
        this.voteReceiver.post(route, getHandler(botList, voteReceiver));
    }

    private Handler getHandler(BotList botList, VoteReceiver<?> voteReceiver) {
        return OpenApiBuilder.documented(
                OpenApiBuilder.document()
                        .operation(op -> {
                            op.summary("Submit a vote for " + botList.name());
                        })
                        .body(voteReceiver.payload(), ContentType.JSON)
                        .header(voteReceiver.auth().name(), String.class)
                        .result("200")
                        .result("401", String.class, p -> p.setDescription("When the token in " + voteReceiver.auth().name() + " is invalid.")),
                ctx -> {
                    if (!voteReceiver.isAuthorized(ctx)) {
                        ctx.status(HttpStatus.UNAUTHORIZED_401);
                        return;
                    }
                    var data = OBJECT_MAPPER.readValue(ctx.body(), voteReceiver.payload());
                    var voteData = voteReceiver.mapToVoteData(data);
                    voted(voteData);
                    ctx.status(HttpStatus.OK_200);
                });
    }

    /**
     * Queries all botlists and returns all botlists, where the user has recent votes.
     *
     * @param user suer to check
     * @return list of botlist where the user has voted
     */
    public List<BotList> hasVoted(User user) {
        List<BotList> result = new ArrayList<>();
        for (var botList : checker) {
            if (botList.hasVoted(user)) {
                result.add(botList);
            }
        }
        return result;
    }

    public void voted(VoteData data) {
        voteNotifier.accept(data);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Consumer<VoteData> voteNotifier;
        private Javalin voteReceiver;

        public Builder withVoteWeebhooks(String host, int port) {
            voteReceiver = Javalin.create().start(host, port);
            return this;
        }

        public Builder withVoteWeebhooks(Javalin javalin) {
            voteReceiver = javalin;
            return this;
        }

        public Builder onVote(Consumer<VoteData> voteNotifier) {
            this.voteNotifier = voteNotifier;
            return this;
        }

        public VoteService build() {
            return new VoteService(voteReceiver, voteNotifier);
        }
    }
}
