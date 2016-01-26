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
@Repository
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

Configure your app:
```java
    @Configure
    @EnableRestClients
    class AppConfig {
        @Bean
        public ObjectMapper objectMapper() { return new ObjectMapper(); }

        @Bean
        public HttpClient httpClient() { return new HttpClient(); }
    }
```


Now you can use your interface in spring context.
For example:
```java
    @Controller
    public class PhotoController {
        @Autowired
        private PhotoOperations photoOperations;

        @RequestMapping("/photos")
        public String renderPhotos() {
            List<Photo> photos = photoOperations.getPhotos(1L, -1, null);
            for(Photo photo: photos) {
                System.out.println(photo.getUrl);
            }
        }
    }
```

How to build:
--------------------------------
Checkout the repo then use gradle wrapper script (gradlew or gradlew.bat)