package de.efi23a.bot.features.mail.protocol;

import de.efi23a.bot.features.mail.exception.MailMessageFetchingException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface MailProtocol {

  @NotNull
  Collection<MailMessage> fetchNewMessages(String url) throws MailMessageFetchingException;

}
