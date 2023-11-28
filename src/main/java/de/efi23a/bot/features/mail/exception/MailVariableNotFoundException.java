package de.efi23a.bot.features.mail.exception;

public class MailVariableNotFoundException extends MailForwardingException {

  public MailVariableNotFoundException(String variableName) {
    super("Variable " + variableName + " not found");
  }
}
