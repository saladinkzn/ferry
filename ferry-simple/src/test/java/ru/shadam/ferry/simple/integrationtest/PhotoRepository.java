package ru.shadam.ferry.simple.integrationtest;

import ru.shadam.ferry.annotations.*;
import ru.shadam.ferry.simple.integrationtest.dto.Album;
import ru.shadam.ferry.simple.integrationtest.dto.Photo;

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


    @Url("getAlbums")
    @ImplicitParam(paramName = "", providerName = "accessTokenProvider")
    List<Album> getAlbums2();

    @Url("upload")
    @RequestMethod("POST")
    void uploadPhoto(@RequestBody String photoEntity);
}
