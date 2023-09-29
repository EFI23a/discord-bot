package de.efi23a.bot.features.customchannel;

import de.efi23a.bot.database.model.CustomChannelModel;
import de.efi23a.bot.database.provider.CustomChannelProvider;
import de.efi23a.bot.database.repository.CustomChannelRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomChannelListener extends ListenerAdapter {

  private final JDA jda;
  private final CustomChannelFeature customChannelFeature;
  private final CustomChannelProvider customChannelProvider;
  private final CustomChannelRepository customChannelRepository;

  @PostConstruct
  public void postConstruct() {
    jda.addEventListener(this);
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    var cmd = event.getName();
    var subcommand = event.getSubcommandName();

    Guild guild = Objects.requireNonNull(event.getGuild());
    User user = event.getUser();

    if (!cmd.equalsIgnoreCase("cc"))
      return;
    if (subcommand == null)
      return;

    if (subcommand.equalsIgnoreCase("create")) {
      String channelTitle = event.getOption("title").getAsString();
      String channelDescription = "";
      OptionMapping channelDescriptionOption = event.getOption("description");
      if (channelDescriptionOption != null)
        channelDescription = channelDescriptionOption.getAsString();

      TextChannel channel =
          customChannelProvider.createChannel(user, guild, channelTitle, channelDescription);
      channel.getManager().setTopic(channelDescription).queue();

      customChannelProvider.updatePermissions(channel, true);
      event.reply("created channel " + channel.getAsMention()).queue();
      return;
    }

    if (subcommand.equalsIgnoreCase("adduser")) {
      TextChannel channel = event.getChannel().asTextChannel();
      String channelId = channel.getId();

      if (!checks(event, channelId, user.getId()))
        return;

      User target = event.getOption("user").getAsUser();
      boolean targetIsOwner = customChannelRepository.findCustomChannelByChannelId(channelId).getOwnerId().equalsIgnoreCase(target.getId());
      if (targetIsOwner) { // kann sein dass es auch nicht gereached wird (nicht getestet)
        event.getInteraction().reply("custom channel creator cannot be updated").queue();
        return;
      }

      int permissions = 1;

      OptionMapping permissionOption = event.getOption("permissions");
      if (permissionOption != null)
        permissions = permissionOption.getAsInt();

      customChannelProvider.updateUserInChannel(channelId, target, permissions);
      customChannelProvider.updatePermissions(channel, false);

      if (channelContains(event, channelId, target.getId())) {
        event.reply("updated " + target.getAsMention() + " permission-level to " + permissions).queue();
      } else { // wird irgendwie nicht gereached (nicht getestet)
        event.reply("added " + target.getAsMention() + " to channel with permission-level of " + permissions).queue();
      }
      return;
    }

    if (subcommand.equalsIgnoreCase("remuser")) {
      TextChannel channel = event.getChannel().asTextChannel();
      String channelId = channel.getId();

      if (!checks(event, channelId, user.getId()))
        return;

      User target = event.getOption("user").getAsUser();
      boolean targetIsOwner = customChannelRepository.findCustomChannelByChannelId(channelId).getOwnerId().equalsIgnoreCase(target.getId());
      if (targetIsOwner) {
        event.getInteraction().reply("custom channel creator cannot be kicked").queue();
        return;
      }

      if (!channelContains(event, channelId, target.getId())) {
        event.getInteraction().reply("user not found in custom channel").queue();
        return;
      }

      customChannelProvider.updateUserInChannel(channelId, target, -1);
      customChannelProvider.updatePermissions(channel, false);

      event.getInteraction().reply("user " + target.getAsMention() + " was kicked!").queue();
      return;
    }

    if (subcommand.equalsIgnoreCase("delete")) {
      TextChannel channel = event.getChannel().asTextChannel();
      String channelId = channel.getId();

      if (!checks(event, channelId, user.getId()))
        return;

      customChannelProvider.deleteChannel(channelId);
      channel.delete().queue();
      return;
    }
  }

  private boolean checks(SlashCommandInteractionEvent event, String channelId, String userId) {
    CustomChannelModel customChannelModel = customChannelRepository.findCustomChannelByChannelId(channelId);
    if (customChannelModel == null) {
      event.getInteraction().reply("invalid custom channel error").queue();
      return false;
    }

    int userPermission = customChannelModel.getGrantedUserPerms().getOrDefault(userId, -1);
    if (userPermission <= 1) {
      event.getInteraction().reply("permission error").queue();
      return false;
    }

    return true;
  }

  private boolean channelContains(SlashCommandInteractionEvent event, String channelId, String userId) {
    CustomChannelModel customChannelModel = customChannelRepository.findCustomChannelByChannelId(channelId);
    if (customChannelModel == null)
      return false;

    boolean toReturn = customChannelModel.getGrantedUserPerms().containsKey(userId);
    if (toReturn) {
      boolean isKicked = customChannelModel.getGrantedUserPerms().entrySet().stream()
          .filter(x -> x.getKey().equalsIgnoreCase(userId)).allMatch(x -> x.getValue() <= 0);
      toReturn = !isKicked;
    }

    return toReturn;
  }
}
