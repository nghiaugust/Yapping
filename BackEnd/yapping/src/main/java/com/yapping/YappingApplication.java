package com.yapping;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
public class YappingApplication {

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
				new ClassPathResource("firebase-service-account.json").getInputStream());

		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials)
				.build();

		FirebaseApp app;
		if (FirebaseApp.getApps().isEmpty()) {
			app = FirebaseApp.initializeApp(firebaseOptions, "nexus-app");
		} else {
			app = FirebaseApp.getInstance("nexus-app");
		}

		return FirebaseMessaging.getInstance(app);
	}
	public static void main(String[] args) {
		SpringApplication.run(YappingApplication.class, args);
	}
}
