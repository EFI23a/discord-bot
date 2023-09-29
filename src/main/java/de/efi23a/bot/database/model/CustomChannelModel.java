package de.efi23a.bot.database.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashMap;
import java.util.UUID;

@Setter
@Getter
@Document("customChannel")
public class CustomChannelModel {

  @Id
  private String id;

  private String ownerId;
  private String channelId;
  private String channelDescription;
  private HashMap<String, Integer> grantedUserPerms;

  public CustomChannelModel(String channelId, String channelDescription, String ownerId) {
    this.id = UUID.randomUUID().toString();
    this.ownerId = ownerId;
    this.channelId = channelId;
    this.channelDescription = channelDescription;
    this.grantedUserPerms = new HashMap<>();
    this.grantedUserPerms.put(ownerId, 2);
  }

}
