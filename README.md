# Ferry

Library for creating spring-data style repositories but for rest apis.

Usage:
-------------------------------------------------------------------

Add dependency:
```groovy
dependencies {
    compile 'ru.shadam.ferry:ferry:0.1.0-SNAPSHOT'
}
```

Create repository:
```java
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
```

Then you can use it:
```java
    ClientImplFactory clientImplFactory = new ClientImplFactory(
        HttpClientBuilder.create().build(),
        new ObjectMapper()
    );
    clientImplFactory.registerImplicitParameterProvider("accessTokenProvider",
        new ImplicitParameterProvider() {
            @Override
            public String provideValue() {
                return "ACCESS_TOKEN";
            }
    });
    clientImplFactory.getInterfaceImplementation(PhotoOperations.class)
```


How to build:
--------------------------------
Checkout the repo then use gradle wrapper script (gradlew or gradlew.bat)