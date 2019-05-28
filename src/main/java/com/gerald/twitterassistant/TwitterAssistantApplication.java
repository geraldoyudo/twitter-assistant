package com.gerald.twitterassistant;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

/*
    Get your twitter user ID from here:
	http://gettwitterid.com
	change the USER_ID field
 */
@SpringBootApplication
public class TwitterAssistantApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(TwitterAssistantApplication.class);

    private static final String BASE_TWITTER_URL = "https://stream.twitter.com";
    private static final String USER_ID = "287651068";

    @Bean
    WebClient getWebClient() {
        return WebClient.create(BASE_TWITTER_URL);
    }

    @Autowired
    private WebClient client;

    @Override
    public void run(String... args) throws Exception {
        String path = "/1.1/statuses/filter.json?follow=" + USER_ID;
        client.get()
                .uri(path)
                .header("Authorization", getAuthorizationHeader(path))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .filter(jsonNode -> !jsonNode.has("limit"))
                .map(TwitterAssistantApplication::getText)
                .filter(Objects::nonNull)
                .filter(TwitterAssistantApplication::filterTweet)
                .subscribe(logger::info);
    }

    private static String getText(JsonNode jsonNode) {
        if (jsonNode.has("text")) {
            return jsonNode.get("text").asText("");
        }
        return null;
    }

    private static boolean filterTweet(String text) {
        /*
        Modify this code to filter what kind of tweets the twitter api returns using text
		matching operations

		The code below filters tweets containing "my workshop" or "my-workshop"
		 */
        String lowerCase = text.toLowerCase();
        return lowerCase.contains("my workshop") || lowerCase.contains("my-workshop");
    }

    private static final String getAuthorizationHeader(String path) throws Exception {
        String completeURL = BASE_TWITTER_URL + path;
        Map<String, String> headers = TwitterSecurity.getTwitterSecurityDetails(completeURL);
        return headers.get("Authorization");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TwitterAssistantApplication.class, args);
        Thread.currentThread().join();
    }

}
