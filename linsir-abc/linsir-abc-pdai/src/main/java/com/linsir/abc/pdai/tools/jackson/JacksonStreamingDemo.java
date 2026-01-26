package com.linsir.abc.pdai.tools.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Jackson 流式 API 示例
 * 演示使用 JsonParser 读取 JSON 和使用 JsonGenerator 写入 JSON
 */
public class JacksonStreamingDemo {

    /**
     * 演示 Jackson 流式 API 操作
     */
    public static void demonstrateStreamingAPI() {
        try {
            // 创建 JsonFactory 实例
            JsonFactory jsonFactory = new JsonFactory();

            // 1. 使用 JsonGenerator 写入 JSON
            System.out.println("=== 1. 使用 JsonGenerator 写入 JSON ===");
            StringWriter stringWriter = new StringWriter();
            JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);

            // 开始写入对象
            jsonGenerator.writeStartObject();

            // 写入字段
            jsonGenerator.writeNumberField("id", 1);
            jsonGenerator.writeStringField("name", "John Doe");
            jsonGenerator.writeNumberField("age", 30);
            jsonGenerator.writeStringField("email", "john@example.com");

            // 写入嵌套对象
            jsonGenerator.writeFieldName("address");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("street", "123 Main St");
            jsonGenerator.writeStringField("city", "New York");
            jsonGenerator.writeStringField("zip", "10001");
            jsonGenerator.writeEndObject();

            // 写入数组
            jsonGenerator.writeFieldName("phoneNumbers");
            jsonGenerator.writeStartArray();
            jsonGenerator.writeString("123-456-7890");
            jsonGenerator.writeString("987-654-3210");
            jsonGenerator.writeEndArray();

            // 结束写入对象
            jsonGenerator.writeEndObject();
            jsonGenerator.close();

            // 获取生成的 JSON 字符串
            String generatedJson = stringWriter.toString();
            System.out.println("使用 JsonGenerator 生成的 JSON:");
            System.out.println(generatedJson);

            // 2. 使用 JsonParser 读取 JSON
            System.out.println("\n=== 2. 使用 JsonParser 读取 JSON ===");
            StringReader stringReader = new StringReader(generatedJson);
            JsonParser jsonParser = jsonFactory.createParser(stringReader);

            // 遍历 JSON 令牌
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.getCurrentName();
                if (fieldName != null) {
                    jsonParser.nextToken(); // 移动到字段值

                    switch (fieldName) {
                        case "id":
                            System.out.println("id: " + jsonParser.getIntValue());
                            break;
                        case "name":
                            System.out.println("name: " + jsonParser.getText());
                            break;
                        case "age":
                            System.out.println("age: " + jsonParser.getIntValue());
                            break;
                        case "email":
                            System.out.println("email: " + jsonParser.getText());
                            break;
                        case "address":
                            System.out.println("address:");
                            // 处理嵌套对象
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                                String addressField = jsonParser.getCurrentName();
                                if (addressField != null) {
                                    jsonParser.nextToken();
                                    System.out.println("  " + addressField + ": " + jsonParser.getText());
                                }
                            }
                            break;
                        case "phoneNumbers":
                            System.out.println("phoneNumbers:");
                            // 处理数组
                            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                                System.out.println("  " + jsonParser.getText());
                            }
                            break;
                    }
                }
            }

            jsonParser.close();

            // 3. 演示流式 API 处理大型 JSON
            System.out.println("\n=== 3. 演示流式 API 处理大型 JSON ===");
            String largeJson = generateLargeJson();
            System.out.println("生成的大型 JSON 长度: " + largeJson.length() + " 字符");
            
            // 使用流式 API 快速读取大型 JSON
            long startTime = System.currentTimeMillis();
            processLargeJson(largeJson);
            long endTime = System.currentTimeMillis();
            System.out.println("处理大型 JSON 耗时: " + (endTime - startTime) + " ms");

        } catch (IOException e) {
            System.err.println("使用流式 API 时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成大型 JSON 字符串
     * @return 大型 JSON 字符串
     */
    private static String generateLargeJson() throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);

        // 开始写入对象
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", "Large Dataset");
        jsonGenerator.writeNumberField("version", 1);

        // 写入大型数组
        jsonGenerator.writeFieldName("items");
        jsonGenerator.writeStartArray();

        // 生成 1000 个项目
        for (int i = 0; i < 1000; i++) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", i);
            jsonGenerator.writeStringField("name", "Item " + i);
            jsonGenerator.writeNumberField("value", i * 10.5);
            jsonGenerator.writeBooleanField("active", i % 2 == 0);
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        return stringWriter.toString();
    }

    /**
     * 处理大型 JSON 字符串
     * @param largeJson 大型 JSON 字符串
     */
    private static void processLargeJson(String largeJson) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        StringReader stringReader = new StringReader(largeJson);
        JsonParser jsonParser = jsonFactory.createParser(stringReader);

        int itemCount = 0;
        double totalValue = 0;

        // 遍历 JSON 令牌
        while (jsonParser.nextToken() != null) {
            if (JsonToken.FIELD_NAME.equals(jsonParser.getCurrentToken()) && "items".equals(jsonParser.getCurrentName())) {
                // 找到 items 数组
                jsonParser.nextToken(); // 移动到数组开始
                
                // 遍历数组元素
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    if (JsonToken.START_OBJECT.equals(jsonParser.getCurrentToken())) {
                        itemCount++;
                        
                        // 遍历对象字段
                        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                            String fieldName = jsonParser.getCurrentName();
                            if (fieldName != null && "value".equals(fieldName)) {
                                jsonParser.nextToken();
                                totalValue += jsonParser.getDoubleValue();
                            }
                        }
                    }
                }
            }
        }

        jsonParser.close();
        System.out.println("处理结果: 共 " + itemCount + " 个项目，总价值: " + totalValue);
    }
}
