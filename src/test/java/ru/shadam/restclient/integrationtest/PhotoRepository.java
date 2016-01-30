package ru.shadam.restclient.integrationtest;

import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.Url;
import ru.shadam.restclient.integrationtest.dto.Album;
import ru.shadam.restclient.integrationtest.dto.Photo;

import java.util.List;

/**
 * @author sala
 */
@Url("https://api.vk.com/methods/photos.")
public interface PhotoRepository {
    @Url("get")
    public List<Photo> getPhotos(@Param("owner_id") Long ownerId,
                                 @Param("album_id") Long albumId,
                                 @Param("photo_ids") String photoIds);

    @Url("getAlbums")
    public List<Album> getAlbums(@Param("owner_id") Long ownerId,
                                 @Param("ablum_ids") String albumIds,
                                 int offset,
                                 int count);
}
