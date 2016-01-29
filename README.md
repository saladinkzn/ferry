# RestClient-repo

Library for creating spring-data style repositories but for rest apis.

Usage:
-------------------------------------------------------------------

Add dependency:
```groovy
dependencies {
    compile 'ru.shadam.restclient:restclient:1.0.0'
}
```

Create repository:
```java
@Url("https://api.vk.com/methods/photos.")
public interface PhotoOperations {
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
```

Then you can use it:
```java
    ClientImplFactory clientImplFactory = new ClientImplFactory(
        HttpClientBuilder.create().build(),
        new ObjectMapper()
    );
    clientImplFactory.getInterfaceImplementation(PhotoOperations.class)
```


How to build:
--------------------------------
Checkout the repo then use gradle wrapper script (gradlew or gradlew.bat)