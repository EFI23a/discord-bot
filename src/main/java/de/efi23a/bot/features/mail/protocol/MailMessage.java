package de.efi23a.bot.features.mail.protocol;

import de.efi23a.bot.features.mail.exception.MailMessageParameterException;
import org.jetbrains.annotations.Nullable;

public interface MailMessage {

  @Nullable
  String getSubject() throws MailMessageParameterException;

  @Nullable
  String getSender() throws MailMessageParameterException;

  @Nullable
  String getBody() throws MailMessageParameterException;

}