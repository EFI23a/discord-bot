package de.efi23a.bot.features.mail;

import de.efi23a.bot.features.mail.exception.MailTargetChannelNotFoundException;
import de.efi23a.bot.features.mail.exception.MailVariableNotFoundException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailEnvironment {

  public static final String MAIL_URL_VARIABLE = "MAIL_URL";
  public static final String MAIL_CHANNEL_ID = "MAIL_CHANNEL_ID";

  private final JDA jda;

  @NotNull
  @Contract(pure = true)
  public String fetchMailVariable(String mailVariable) throws MailVariableNotFoundException {
    String mailUrlVariable = System.getenv(mailVariable);
    if (mailUrlVariable == null) {
      throw new MailVariableNotFoundException(mailVariable);
    }
    return mailUrlVariable;
  }

  public TextChannel fetchTextChannel(String mailChannelId)
      throws MailTargetChannelNotFoundException {
    TextChannel textChannel = jda.getTextChannelById(mailChannelId);
    if (textChannel == null) {
      throw new MailTargetChannelNotFoundException(MAIL_CHANNEL_ID);
    }
    return textChannel;
  }

}
