package de.efi23a.bot.features.mail;

import de.efi23a.bot.features.mail.exception.MailForwardingException;
import de.efi23a.bot.features.mail.exception.MailMessageParameterException;
import de.efi23a.bot.features.mail.protocol.MailMessage;
import de.efi23a.bot.features.mail.protocol.MailProtocol;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailForwardingFeature {

  private final MailEnvironment mailEnvironment;
  private final MailProtocol mailProtocol;

  @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
  public void runScheduledTask()
      throws MailForwardingException {
    log.info("Running mail task");

    String mailChannelId = mailEnvironment.fetchMailVariable(MailEnvironment.MAIL_CHANNEL_ID);
    String url = mailEnvironment.fetchMailVariable(MailEnvironment.MAIL_URL_VARIABLE);
    TextChannel textChannel = mailEnvironment.fetchTextChannel(mailChannelId);

    handleMailForwarding(url, textChannel);

    log.info("Finished mail task");
  }

  public void handleMailForwarding(@NotNull String url,
                                    @NotNull TextChannel textChannel)
      throws MailForwardingException {

    for (MailMessage mailMessage : mailProtocol.fetchNewMessages(url)) {
      try {
        MessageEmbed messageEmbed = buildEmbed(mailMessage);
        textChannel.sendMessageEmbeds(messageEmbed).queue();
      } catch (MailMessageParameterException e) {
        throw new MailForwardingException(e);
      }
    }

  }

  @NotNull
  private MessageEmbed buildEmbed(@NotNull MailMessage message) throws
      MailMessageParameterException {
    return new EmbedBuilder()
        .setTitle(message.getSubject())
        .setAuthor(message.getSender())
        .setDescription(message.getBody())
        .build();
  }

}
