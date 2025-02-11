package hr.projekt.app.ProjektJavaApp.Genericsi;

import hr.projekt.app.ProjektJavaApp.Enum.MediaType;
import hr.projekt.app.ProjektJavaApp.Model.Reviewable;

public class Review<T extends Reviewable> {
    private Integer id;
    private MediaType mediaType;
    private String username;
    private T media;
    private String comment;

    public Review(Integer id, MediaType mediaType, String username, T media, String comment) {
        this.id = id;
        this.mediaType = mediaType;
        this.username = username;
        this.media = media;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public T getMedia() {
        return media;
    }

    public void setMedia(T media) {
        this.media = media;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Review{" +
                "username='" + username + '\'' +
                ", media=" + media +
                ", comment='" + comment + '\'' +
                '}';
    }
}
