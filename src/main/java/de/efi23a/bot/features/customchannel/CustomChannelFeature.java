package de.efi23a.bot.features.customchannel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomChannelFeature {

  private final JDA jda;

  @PostConstruct
  void postConstruct() {

    var cmd = Commands.slash("customchannel", "manage your own customchannels")
        .addSubcommands(
            new SubcommandData("create", "create a customchannel")
                .addOption(OptionType.STRING, "title", "channel title", true)
                .addOption(OptionType.STRING, "description", "channel description (topic)", false),
            new SubcommandData("delete", "delete channel"),
            new SubcommandData("adduser", "add/update customchannel user")
                .addOption(OptionType.USER, "user", "user", true)
                .addOption(OptionType.INTEGER, "permissions", "1 = Zugriff, 2 = Management Rechte", false),
            new SubcommandData("remuser", "remove user from channel")
                .addOption(OptionType.USER, "user", "user", true)
        );

    jda.updateCommands().addCommands(cmd).queue();

  }

}
