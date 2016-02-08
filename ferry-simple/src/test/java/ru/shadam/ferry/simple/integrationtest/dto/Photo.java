package ru.shadam.ferry.simple.integrationtest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author sala
 */
public class Photo {
    long id;
    @JsonProperty("album_id")
    long albumId;
    @JsonProperty("owner_id")
    long ownerId;
    @JsonProperty("user_id")
    long userId;
    String text;
    long date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
