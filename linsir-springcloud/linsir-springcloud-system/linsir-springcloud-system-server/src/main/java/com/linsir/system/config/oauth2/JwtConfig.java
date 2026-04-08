package com.linsir.system.config.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * JWT 配置类
 * Spring Security 7.0.4 版本
 * 配置 RSA 密钥对和 JWT 编码/解码器
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class JwtConfig {

    private static final String KEYS_DIR = "keys";
    private static final String PRIVATE_KEY_FILE = KEYS_DIR + "/private.key";
    private static final String PUBLIC_KEY_FILE = KEYS_DIR + "/public.key";

    /**
     * RSA 密钥对
     */
    private RSAKey rsaKey;

    /**
     * JWK 源配置
     * 从文件加载或生成 RSA 密钥对
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey rsaKey = loadOrGenerateRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * JWT 编码器
     * 使用 NimbusJwtEncoder 创建
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * JWT 解码器
     * 使用 NimbusJwtDecoder 创建
     */
    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // 获取 RSA 公钥
        RSAKey rsaKey = loadOrGenerateRsaKey();
        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * 加载或生成 RSA 密钥对
     * 如果文件存在则加载，否则生成并保存
     */
    private RSAKey loadOrGenerateRsaKey() throws Exception {
        if (rsaKey != null) {
            return rsaKey;
        }

        ClassPathResource privateKeyResource = new ClassPathResource(PRIVATE_KEY_FILE);
        ClassPathResource publicKeyResource = new ClassPathResource(PUBLIC_KEY_FILE);

        KeyPair keyPair;
        if (privateKeyResource.exists() && publicKeyResource.exists()) {
            // 从 classpath 加载（支持 jar 包内读取）
            keyPair = loadKeyPairFromClasspath(privateKeyResource, publicKeyResource);
        } else {
            // 生成新密钥对并保存到外部目录
            keyPair = generateAndSaveKeyPair();
        }

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        return rsaKey;
    }

    /**
     * 从 classpath 加载密钥对（支持 jar 包内读取）
     */
    private KeyPair loadKeyPairFromClasspath(ClassPathResource privateKeyResource,
                                              ClassPathResource publicKeyResource) throws Exception {
        // 使用 InputStream 读取，支持 jar 包内资源
        String privateKeyPEM;
        String publicKeyPEM;

        try (InputStream is = privateKeyResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            privateKeyPEM = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .reduce(String::concat)
                    .orElse("");
        }

        try (InputStream is = publicKeyResource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            publicKeyPEM = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .reduce(String::concat)
                    .orElse("");
        }

        byte[] privateKeyDecoded = Base64.getDecoder().decode(privateKeyPEM);
        byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyDecoded);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyDecoded);

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 生成并保存密钥对到外部目录
     */
    private KeyPair generateAndSaveKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 保存到外部目录（用户目录下的 .linsir/keys）
        File keysDir = new File(System.getProperty("user.home"), ".linsir/keys");
        if (!keysDir.exists()) {
            keysDir.mkdirs();
        }

        // 保存私钥
        PrivateKey privateKey = keyPair.getPrivate();
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(privateKey.getEncoded()) +
                "\n-----END PRIVATE KEY-----";
        try (FileWriter writer = new FileWriter(new File(keysDir, "private.key"))) {
            writer.write(privateKeyPEM);
        }

        // 保存公钥
        PublicKey publicKey = keyPair.getPublic();
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----";
        try (FileWriter writer = new FileWriter(new File(keysDir, "public.key"))) {
            writer.write(publicKeyPEM);
        }

        System.out.println("=================================================");
        System.out.println("RSA 密钥对已生成并保存到: " + keysDir.getAbsolutePath());
        System.out.println("=================================================");

        return keyPair;
    }
}
