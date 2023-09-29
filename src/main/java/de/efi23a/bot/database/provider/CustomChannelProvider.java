package de.efi23a.bot.database.provider;

import de.efi23a.bot.database.model.CustomChannelModel;
import de.efi23a.bot.database.repository.CustomChannelRepository;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.EnumSet;
import java.util.Map;

@Getter
@Component
public class CustomChannelProvider {

  private final Log log = LogFactory.getLog(getClass().getName());

  private final CustomChannelRepository customChannelRepository;
  private final JDA jda;

  @Autowired // autowired automatically gets customChannelRepository and Discord JDA
  public CustomChannelProvider(CustomChannelRepository customChannelRepository, JDA jda) {
    this.customChannelRepository = customChannelRepository;
    this.jda = jda;
  }

  @SneakyThrows
  public TextChannel createChannel(User owner, Guild guild, String channelTitle, String channelDescription) {
    Category category = guild.getCategoriesByName("custom channels", true).stream().findAny().orElse(null);
    if (category == null) {
      category = guild.createCategory("custom channels").complete();
    }

    TextChannel channel = guild.createTextChannel(channelTitle, category).complete();
    customChannelRepository.save(new CustomChannelModel(channel.getId(), channelDescription, owner.getId()));

    return channel;
  }

  @SneakyThrows
  public void updateUserInChannel(String channelId, User target, int permissions) {
    CustomChannelModel customChannelModel = customChannelRepository.findCustomChannelByChannelId(channelId);
    customChannelModel.getGrantedUserPerms().put(target.getId(), permissions);
    customChannelRepository.save(customChannelModel);
  }

  @SneakyThrows
  public void deleteChannel(String channelId) {
    CustomChannelModel customChannelModel = customChannelRepository.findCustomChannelByChannelId(channelId);
    customChannelRepository.deleteById(customChannelModel.getId());
  }

  @SneakyThrows
  public void updatePermissions(TextChannel channel, boolean forceUpdate) {
    if (forceUpdate)
      channel.getManager()
          .clearOverridesAdded()
          .putPermissionOverride(channel.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL))
          .complete();

    CustomChannelModel customChannelModel = customChannelRepository.findCustomChannelByChannelId(channel.getId());
    for (Map.Entry<String, Integer> entry : customChannelModel.getGrantedUserPerms().entrySet()) {
      final String targetId = entry.getKey();
      final Member target = channel.getGuild().retrieveMemberById(targetId).complete();
      if (target == null) continue;

      PermissionOverride permissionOverride = channel.getPermissionOverride(target);
      if (permissionOverride == null)
        permissionOverride = channel.upsertPermissionOverride(target).complete();

      if (entry.getValue() > 0) {
        permissionOverride.getManager()
            .deny(Permission.ALL_TEXT_PERMISSIONS)
            .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
            .complete();
      } else {
        permissionOverride.getManager()
            .deny(Permission.ALL_TEXT_PERMISSIONS)
            .deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
            .complete();
      }
    }
  }
}
