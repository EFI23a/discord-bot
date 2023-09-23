package de.efi23a.bot.discord.api.registration;

import de.efi23a.bot.discord.api.event.JdaReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerRegistration {

  /*
  * registers all JDA listeners when JdaReadyEvent is fired
  * */
  @EventListener
  public void registerListeners(JdaReadyEvent event) {
    ApplicationContext context = event.getContext();
    context.getBeansOfType(ListenerAdapter.class).forEach((s, listenerAdapter) -> event.getJda().addEventListener(listenerAdapter));
  }

}
