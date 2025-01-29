package com.mqttSubscribe.demo.config;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.io.IOException;

@Configuration
public class MqttConfig {

    @Value("${mqtt.aws.mqttURL}")
    private String mqttURL;

    @Value("${mqtt.aws.mqttPort}")
    private int mqttPort;

    @Value("${mqtt.aws.mqttKey}")
    private String mqttKey;

    @Value("${mqtt.aws.mqttClientId}")
    private String mqttClientId;

    @Value("${mqtt.aws.mqttCertCa}")
    private String mqttCertCa;

    @Value("${mqtt.aws.mqttCertPriv}")
    private String mqttCertPriv;

    @Value("${mqtt.aws.mqttCertCert}")
    private String mqttCertCert;

    @Bean
    public MqttClient mqttClient() throws Exception {
        String mqttBrokerUrl = "ssl://" + mqttURL + ":" + mqttPort;
        MqttClient mqttClient = new MqttClient(mqttBrokerUrl, mqttClientId, new MemoryPersistence());

        MqttConnectOptions mqttOptions = new MqttConnectOptions();
        mqttOptions.setCleanSession(true);  //To retain undelivered message when connection lost.
        mqttOptions.setConnectionTimeout(10);
        mqttOptions.setKeepAliveInterval(30);
        mqttOptions.setSocketFactory(getSocketFactory());

        mqttClient.connect(mqttOptions);
        return mqttClient;
    }

    private SSLSocketFactory getSocketFactory() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] password = mqttKey.toCharArray();
        keyStore.load(null, password);

        // Load the certificates
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        FileInputStream caFile = new FileInputStream(mqttCertCa);
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(caFile);
        keyStore.setCertificateEntry("caCert", caCert);

        FileInputStream certFile = new FileInputStream(mqttCertCert);
        X509Certificate cert = (X509Certificate) cf.generateCertificate(certFile);
        keyStore.setCertificateEntry("clientCert", cert);

        FileInputStream keyFile = new FileInputStream(mqttCertPriv);
        PrivateKey privateKey = getPrivateKeyFromPem(keyFile);
        keyStore.setKeyEntry("privateKey", privateKey, password, new java.security.cert.Certificate[]{cert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return context.getSocketFactory();
    }

    private PrivateKey getPrivateKeyFromPem(FileInputStream pemFile) throws IOException {
        try {
            PEMParser pemParser = new PEMParser(new InputStreamReader(pemFile));
            PEMKeyPair keyPair = (PEMKeyPair) pemParser.readObject();

            byte[] privateKeyBytes = keyPair.getPrivateKeyInfo().getEncoded();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IOException("Error while parsing PEM file.", e);
        }
    }

}
