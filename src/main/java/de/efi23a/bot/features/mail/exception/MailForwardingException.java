package de.efi23a.bot.features.mail.exception;

public class MailForwardingException extends Exception {

  public MailForwardingException(Throwable cause) {
    super(cause);
  }

  public MailForwardingException(String message) {
    super(message);
  }

}
