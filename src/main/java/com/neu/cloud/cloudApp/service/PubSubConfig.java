package com.neu.cloud.cloudApp.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PubSubConfig {

    @Bean
    public Publisher publisher() throws IOException {
        // Replace "your-gcp-project-id" with your actual project ID
        String projectId = "csye6225csye";
        String topicId = "verify_email";

        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        return Publisher.newBuilder(topicName).build();
    }
}

