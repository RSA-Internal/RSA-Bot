package org.rsa.net.apis.discourse.transformers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rsa.net.apis.discourse.domain.Topic;
import org.rsa.net.apis.discourse.models.TopicModel;

import java.time.Instant;
import java.time.temporal.Temporal;

public class TopicTransformer {
    public static Topic fromResponse(TopicModel response) {
        String content = response.post_stream().posts().get(0).cooked();
        String parsed = parseHtml(content);
        Temporal timestamp = Instant.parse(response.created_at());

        return new Topic(
                timestamp,
                response.id(),
                response.title(),
                response.details().created_by().username(),
                response.details().created_by().avatar_template(),
                parsed
        );
    }

    private static String parseHtml(String html) {
        Document document = Jsoup.parse(html);
        Elements paragraphs = document.select("p");
        StringBuilder sb = new StringBuilder();
        for (Element paragraph : paragraphs) {
            sb.append(paragraph.text()).append("\n\n");
        }
        return sb.toString().trim().replaceAll("\\n\\z", "");
    }
}
