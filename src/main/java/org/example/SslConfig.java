package org.example;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Slf4j
public class SslConfig {

    public static SslContext createSslContextWithCustomCert() {
        try {
            // Загружаем сертификат из ресурсов
            InputStream certStream = SslConfig.class
                    .getClassLoader()
                    .getResourceAsStream("certs/sberbank.cer");

            if (certStream == null) {
                log.warn("⚠️ Certificate not found in resources, using default trust store");
                return SslContextBuilder.forClient().build();
            }

            // Создаём кастомный KeyStore с нашим сертификатом
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certStream);
            keyStore.setCertificateEntry("sberbank-gigachat", cert);

            // Создаём TrustManagerFactory с нашим хранилищем
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            log.info("✅ Custom SSL context created with Sberbank certificate");

            return SslContextBuilder.forClient()
                    .trustManager(tmf)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to create SSL context with custom cert", e);
            // Fallback: используем стандартный (может не работать)
            try {
                return SslContextBuilder.forClient().build();
            } catch (Exception ex) {
                throw new RuntimeException("SSL setup failed", ex);
            }
        }
    }
}