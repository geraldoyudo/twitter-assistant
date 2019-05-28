package com.gerald.twitterassistant;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class TwitterSecurity {

    public static final String TWITTER_CONSUMER_API_KEY = "goibHQMKUkas1Or9g0N6YjOyv";
    public static final String TWITTER_CONSUMER_API_SECRET = "N2FrYcleaj7vdGxcmA4trBKjuV8ZAO8CjU34mM45NTewstS9SP";

    private TwitterSecurity() {
    }

    public static Map<String, String> getTwitterSecurityDetails(String resourceUrl)
            throws IOException, InterruptedException, ExecutionException {
        final OAuth10aService service = new ServiceBuilder(TWITTER_CONSUMER_API_KEY)
                .apiSecret(TWITTER_CONSUMER_API_SECRET)
                .build(TwitterApi.instance());
        final Scanner in = new Scanner(System.in);

        System.out.println("=== Twitter's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        final OAuth1RequestToken requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        final String oauthVerifier = in.nextLine();
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Access granted to Twitter API.");
        final OAuthRequest request = new OAuthRequest(Verb.GET, resourceUrl);
        service.signRequest(accessToken, request);
        return request.getHeaders();
    }
}