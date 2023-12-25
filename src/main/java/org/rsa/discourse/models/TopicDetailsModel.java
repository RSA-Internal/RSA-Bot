package org.rsa.discourse.models;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

@Getter
@Setter
public class TopicDetailsModel {
    private String createdAt;
    private String id;
    private String title;
    private String author;
    private String avatarUrl;
    private String parsedContent;

    public TopicDetailsModel(String createdAt, String id, String title, String author, String avatarTemplate, String content) {
        this.createdAt = createdAt;
        this.id = id;
        this.title = title;
        this.author = author;
        this.avatarUrl = avatarTemplate;
        this.parsedContent = content;
    }

    public static class TopicResponse {
        String created_at;
        String id;
        String title;
        Details details;
        PostStream post_stream;

        static class PostStream {
            List<Post> posts;
        }

        static class Post {
            String cooked;
        }

        static class Details {
            CreatedBy created_by;

            static class CreatedBy {
                String username;
                String avatar_template;
            }
        }
    }

    public static TopicDetailsModel fromTopicResponse(TopicResponse response) {
        String content = response.post_stream.posts.get(0).cooked;

        Document document = Jsoup.parse(content);
        Elements paragraphs = document.select("p");

        StringBuilder sb = new StringBuilder();
        for (Element paragraph : paragraphs) {
            sb.append(paragraph.text());
            sb.append("\n\n");
        }

        content = sb.toString().replaceAll("\\n\\z", "").trim();

        return new TopicDetailsModel(response.created_at, response.id, response.title,
                response.details.created_by.username, response.details.created_by.avatar_template, content);
    }
}