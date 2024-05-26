package org.rsa.net.apis.discourse.domain;

import java.time.temporal.Temporal;

public record Topic(Temporal createdAt, Integer id, String title, String author, String parsedContent, String imageUrl) {}
