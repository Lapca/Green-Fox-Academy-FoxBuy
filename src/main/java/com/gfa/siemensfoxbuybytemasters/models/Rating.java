package com.gfa.siemensfoxbuybytemasters.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue
    private long id;

    @Min(1)
    @Max(5)
    @JsonProperty("Rating")
    private int rating;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("Comment")
    private String comment;
    @Column(columnDefinition = "TEXT")
    @JsonProperty("Reaction")
    private String reaction;

    private UUID authorID;

    @ManyToOne
    private User user;

    public Rating() {
    }

    public Rating(int rating, String comment, UUID authorID, User user) {
        this.rating = rating;
        this.comment = comment;
        this.authorID = authorID;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getAuthorID() {
        return authorID;
    }

    public void setAuthorID(UUID authorID) {
        this.authorID = authorID;
    }
}
