package org.rsa.net.apis.discourse.domain;

import java.time.temporal.Temporal;

public record Topic(Temporal createdAt, String id, String title, String author, String authorAvatarTemplate, String parsedContent) { }
