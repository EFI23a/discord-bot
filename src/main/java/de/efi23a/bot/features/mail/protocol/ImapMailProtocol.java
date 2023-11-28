package de.efi23a.bot.features.mail.protocol;

import de.efi23a.bot.features.mail.exception.MailMessageFetchingException;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.URLName;
import jakarta.mail.search.FlagTerm;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ImapMailProtocol implements MailProtocol {

  private static final String INBOX_FOLDER = "INBOX";

  @Override
  public @NotNull Collection<MailMessage> fetchNewMessages(String url)
      throws MailMessageFetchingException {
    Session session = createSession();

    try (Store store = openStore(session, url); Folder folder = openFolder(store)) {
      return getNewMails(folder).stream()
          .map(ImapMailMessage::new)
          .collect(Collectors.toCollection(HashSet::new));
    } catch (MessagingException e) {
      throw new MailMessageFetchingException(e);
    }
  }

  @NotNull
  public Session createSession() {
    Properties properties = new Properties();
    properties.put("mail.store.protocol", "imap");
    properties.put("mail.imap.starttls.enable", "true");
    return Session.getInstance(properties);
  }

  @NotNull
  public Store openStore(@NotNull Session session, @NotNull String url) throws MessagingException {
    Store store = session.getStore(new URLName(url));
    store.connect();
    return store;
  }

  @NotNull
  public Folder openFolder(@NotNull Store store) throws MessagingException {
    Folder folder = store.getFolder(INBOX_FOLDER);
    folder.open(Folder.READ_WRITE);
    return folder;
  }

  @NotNull
  public List<Message> getNewMails(@NotNull Folder emailFolder) throws MessagingException {
    return Arrays.asList(emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false)));
  }
}
