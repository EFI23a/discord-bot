package de.efi23a.bot.database.repository;

import de.efi23a.bot.database.model.CustomChannelModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CustomChannelRepository extends MongoRepository<CustomChannelModel, String> {

  @Query("{id:'?0'}")
  CustomChannelModel findCustomChannelById(String id);

  @Query("{channelId:'?0'}")
  CustomChannelModel findCustomChannelByChannelId(String channelId);

  long count();

}
