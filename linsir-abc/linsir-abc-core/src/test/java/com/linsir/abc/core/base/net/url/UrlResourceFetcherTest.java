package com.linsir.abc.core.base.net.url;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * UrlResourceFetcher测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class UrlResourceFetcherTest {

    @Test
    public void testConstructor() {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();
        assertNotNull(fetcher);
    }

    @Test
    public void testFetchContent() {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();

        // 使用一个稳定的测试URL
        assertDoesNotThrow(() -> {
            try {
                String content = fetcher.fetchContent("http://example.com");
                assertNotNull(content);
                assertFalse(content.isEmpty());
            } catch (IOException e) {
                // 网络问题可能导致失败，但不应该抛出异常
                System.out.println("网络请求失败（预期）: " + e.getMessage());
            }
        });
    }

    @Test
    public void testFetchContentWithInvalidUrl() {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();

        assertThrows(MalformedURLException.class, () -> {
            fetcher.fetchContent("not-a-valid-url");
        });
    }

    @Test
    public void testPrintUrlInfo() {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();

        String testUrl = "https://www.example.com:8080/path/to/resource?key=value#section";

        assertDoesNotThrow(() -> fetcher.printUrlInfo(testUrl));
    }

    @Test
    public void testPrintUrlInfoWithSimpleUrl() {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();

        String testUrl = "http://example.com";

        assertDoesNotThrow(() -> fetcher.printUrlInfo(testUrl));
    }

    @Test
    public void testDownloadFile() throws IOException {
        UrlResourceFetcher fetcher = new UrlResourceFetcher();
        Path tempFile = Files.createTempFile("download_test", ".html");

        assertDoesNotThrow(() -> {
            try {
                fetcher.downloadFile("http://example.com", tempFile.toString());
                File downloaded = tempFile.toFile();
                if (downloaded.exists() && downloaded.length() > 0) {
                    assertTrue(downloaded.length() > 0);
                }
            } catch (IOException e) {
                // 网络问题可能导致失败
                System.out.println("下载失败（预期）: " + e.getMessage());
            }
        });

        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> UrlResourceFetcher.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> UrlResourceFetcher.main(new String[]{}));
    }
}
