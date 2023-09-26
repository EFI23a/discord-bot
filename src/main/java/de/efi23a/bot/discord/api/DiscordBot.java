package de.efi23a.bot.discord.api;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordBot {

  private final ApplicationEventPublisher  applicationEventPublisher;
  private static final String TOKEN_ENV_VARIABLE = "BOT_TOKEN";

  private JDA jda;

  @Bean
  @SneakyThrows
  private JDA buildJda() {
    String botToken = System.getenv(TOKEN_ENV_VARIABLE);

    JDABuilder jdaBuilder = JDABuilder.createDefault(botToken)
        .disableCache(CacheFlag.ACTIVITY)
        .setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.VOICE))
        .setChunkingFilter(ChunkingFilter.NONE)
        .disableIntents(
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.DIRECT_MESSAGE_TYPING
        ).enableIntents(GatewayIntent.GUILD_MEMBERS);
    return jdaBuilder.build().awaitReady();
  }

  @PreDestroy
  public void applicationStopped() {
    this.jda.shutdownNow();
  }

  public long getSelfUserId() {
    return this.jda.getSelfUser().getIdLong();
  }

}
