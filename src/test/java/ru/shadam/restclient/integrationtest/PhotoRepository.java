package ru.shadam.restclient.integrationtest;

import ru.shadam.restclient.annotations.ImplicitParam;
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
    @ImplicitParam(paramName = "v", constValue = "5.41")
    public List<Photo> getPhotos(@Param("owner_id") Long ownerId,
                                 @Param("album_id") Long albumId,
                                 @Param("photo_ids") String photoIds);

    @Url("getAlbums")
    @ImplicitParam(paramName = "access_token", providerName = "accessTokenProvider")
    public List<Album> getAlbums(@Param("owner_id") Long ownerId,
                                 @Param("album_ids") String albumIds,
                                 @Param("offset") int offset,
                                 @Param("count") int count);

}
