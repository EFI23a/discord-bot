package de.efi23a.bot.features.mail.protocol;

import de.efi23a.bot.features.mail.exception.MailMessageParameterException;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ImapMailMessage implements MailMessage {

  private final Message imapMessage;
  private String cachedBody;

  @Override
  public String getSubject() throws MailMessageParameterException {
    try {
      return imapMessage.getSubject();
    } catch (MessagingException e) {
      throw new MailMessageParameterException(e);
    }
  }

  @Override
  public String getSender() throws MailMessageParameterException {
    try {
      return Arrays.stream(imapMessage.getFrom())
          .map(Address::toString)
          .collect(Collectors.joining(", "));
    } catch (MessagingException e) {
      throw new MailMessageParameterException(e);
    }
  }

  @Override
  public String getBody() throws MailMessageParameterException {
    if (cachedBody == null) {
      try {
        cachedBody = buildBody(imapMessage.getContent());
      } catch (IOException | MessagingException e) {
        throw new MailMessageParameterException(e);
      }
    }
    return cachedBody;
  }

  private String buildBody(Object content) throws MailMessageParameterException {
    StringBuilder stringBuilder = new StringBuilder();

    Deque<Object> stack = new LinkedList<>();
    stack.push(content);

    try {
      while (!stack.isEmpty()) {
        Object currentContent = stack.pop();

        if (currentContent instanceof MimeMultipart mimeMultipart) {
          for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            stack.push(bodyPart.getContent());
          }
        } else if (currentContent instanceof String string) {
          stringBuilder.append(string);
        }
      }
    } catch (MessagingException | IOException e) {
      throw new MailMessageParameterException(e);
    }

    return stringBuilder.toString();
  }
}
