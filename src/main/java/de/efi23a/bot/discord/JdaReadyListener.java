package de.efi23a.bot.discord;

import de.efi23a.bot.discord.api.DiscordBot;
import de.efi23a.bot.discord.api.event.JdaReadyEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdaReadyListener {

  private final DiscordBot discordBot;

  @EventListener
  public void handle(JdaReadyEvent event) {
    final Presence presence = discordBot.getJda().getPresence();
    presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.competing("efi23a on top"));
  }

}
