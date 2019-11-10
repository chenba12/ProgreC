package com.progresee.app.firebase;

import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {


	@Bean
    public Firestore fireStore() {
        return FirestoreClient.getFirestore();
    }


	@Value("${progresee.path}")
	String path;

	@Value("${progresee.db.url}")
	String dbUrl;

	@PostConstruct
	public void init() throws IOException {

		FileInputStream refreshToken = new FileInputStream(path);

		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(GoogleCredentials.fromStream(refreshToken))
		    .setDatabaseUrl(dbUrl)
		    .build();

		FirebaseApp.initializeApp(options);
	}
}
