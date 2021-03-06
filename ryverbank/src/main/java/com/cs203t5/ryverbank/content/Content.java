package com.cs203t5.ryverbank.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * Content class for content management. 
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Title cannot be null")
    @Column(name = "title")
    private String title;

    @NotNull(message = "Summary cannot be null")
    @Column(name = "summary")
    private String summary;

    @NotNull(message = "Content cannot be null")
    @Column(name = "content")
    @JsonProperty("content")
    private String newsContent;

    @Column(name = "link")
    @JsonProperty("link")
    private String link;

    @Column(name = "approved")
    @JsonProperty("approved")
    private boolean approved = false;

    /**
     * Constructs a content with the following parameters.
     * 
     * @param title The title of the content.
     * @param summary The summary of the content.
     * @param newsContent The actual content.
     * @param link The link to the content.
     */
    public Content (String title, String summary, String newsContent, String link){
        this.title = title;
        this.summary = summary;
        this.newsContent = newsContent;
        this.link = link;
    }

    //This constructor is only used for testing purposes
    public Content (String title, String summary, String newsContent, String link, Boolean approved){
        this.title = title;
        this.summary = summary;
        this.newsContent = newsContent;
        this.link = link;
        this.approved = approved;
    }


}
