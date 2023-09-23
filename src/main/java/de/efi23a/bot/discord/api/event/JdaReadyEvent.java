package de.efi23a.bot.discord.api.event;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

@Getter
public class JdaReadyEvent extends ApplicationEvent {

  private final JDA jda;
  private final ApplicationContext context;

  public JdaReadyEvent(JDA jda, ApplicationContext context) {
    super(context);
    this.jda = jda;
    this.context = context;
  }

}