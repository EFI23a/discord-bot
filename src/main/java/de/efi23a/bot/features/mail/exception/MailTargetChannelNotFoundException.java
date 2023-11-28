package de.efi23a.bot.features.mail.exception;

public class MailTargetChannelNotFoundException extends MailForwardingException {

  public MailTargetChannelNotFoundException(String targetChannel) {
    super("Target channel " + targetChannel + " not found");
  }
}
