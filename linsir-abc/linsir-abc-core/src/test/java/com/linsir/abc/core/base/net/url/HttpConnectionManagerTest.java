package com.linsir.abc.core.base.net.url;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * HttpConnectionManager测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpConnectionManagerTest {

    @Test
    public void testConstructor() {
        HttpConnectionManager manager = new HttpConnectionManager();
        assertNotNull(manager);
    }

    @Test
    public void testSendGet() {
        HttpConnectionManager manager = new HttpConnectionManager();

        assertDoesNotThrow(() -> {
            try {
                HttpConnectionManager.HttpResponse response = manager.sendGet("http://example.com");
                assertNotNull(response);
                assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 400);
                assertNotNull(response.getBody());
            } catch (IOException e) {
                // 网络问题可能导致失败
                System.out.println("GET请求失败（预期）: " + e.getMessage());
            }
        });
    }

    @Test
    public void testSendPost() {
        HttpConnectionManager manager = new HttpConnectionManager();

        assertDoesNotThrow(() -> {
            try {
                HttpConnectionManager.HttpResponse response = manager.sendPost(
                    "http://httpbin.org/post", "name=test&value=123");
                assertNotNull(response);
                assertNotNull(response.getBody());
            } catch (IOException e) {
                // 网络问题可能导致失败
                System.out.println("POST请求失败（预期）: " + e.getMessage());
            }
        });
    }

    @Test
    public void testSendGetWithInvalidUrl() {
        HttpConnectionManager manager = new HttpConnectionManager();

        assertThrows(MalformedURLException.class, () -> {
            manager.sendGet("not-a-valid-url");
        });
    }

    @Test
    public void testHttpResponse() {
        HttpConnectionManager.HttpResponse response = new HttpConnectionManager.HttpResponse();

        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.setBody("Test body content");

        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertEquals("Test body content", response.getBody());

        // 测试toString
        String str = response.toString();
        assertTrue(str.contains("200"));
        assertTrue(str.contains("OK"));
    }

    @Test
    public void testHttpResponseWithHeaders() {
        HttpConnectionManager.HttpResponse response = new HttpConnectionManager.HttpResponse();

        Map<String, List<String>> headers = Map.of(
            "Content-Type", List.of("text/html"),
            "Content-Length", List.of("1234")
        );
        response.setHeaders(headers);

        assertEquals(headers, response.getHeaders());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> HttpConnectionManager.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> HttpConnectionManager.main(new String[]{}));
    }
}
