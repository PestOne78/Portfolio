package pestone.wallpaperfootbal.Structure;

// Структура данных в FirebaseDatabase, конструкторы, геттеры, сеттеры
public class Wallpaper {

    private String uri_key, photo_name;
    private int width, height;

    public Wallpaper() {
    }

    public Wallpaper(String photo_name, String uri_key, int width, int height) {
        this.photo_name = photo_name;
        this.uri_key = uri_key;
        this.width = width;
        this.height = height;
    }

    public String getUri_key() {
        return uri_key;
    }

    public void setUri_key(String uri_key) {
        this.uri_key = uri_key;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
