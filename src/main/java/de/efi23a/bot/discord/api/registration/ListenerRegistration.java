package de.efi23a.bot.discord.api.registration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/*
* Verantwortlich dafür, dass JDA Listener automatisch erkannt und registriert werden.
* */
@Component
@RequiredArgsConstructor
public class ListenerRegistration {

  private final ApplicationContext context;
  private final JDA jda;

  @PostConstruct
  public void postConstruct() {
    context.getBeansOfType(ListenerAdapter.class).forEach(
        (unused, listenerAdapter) -> jda.addEventListener(listenerAdapter)
    );
  }

}
