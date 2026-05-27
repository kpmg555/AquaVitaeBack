package com.aquavitae.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;

@Startup
@ApplicationScoped
public class FirebaseConfig {

    @ConfigProperty(name = "firebase.credentials")
    String path;

    void onStart(@Observes StartupEvent ev) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                try (InputStream serviceAccount = openCredentials()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error initializing Firebase Admin SDK", e);
        }
    }

    private InputStream openCredentials() throws Exception {
        // 1. Intentar como archivo (funciona en local con quarkus:dev)
        File file = new File(path);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        // 2. En Cloud Run: credenciales como JSON base64 en variable de entorno
        String b64 = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (b64 != null && !b64.isEmpty()) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(b64));
        }
        // 3. Classpath (recursos del JAR)
        String name = file.getName();
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);
        if (is != null) return is;

        throw new FileNotFoundException("Firebase credentials no encontrado en: " + path + ", FIREBASE_CREDENTIALS_JSON ni classpath");
    }
}