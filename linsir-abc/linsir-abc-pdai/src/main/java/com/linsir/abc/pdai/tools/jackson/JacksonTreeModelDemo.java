package com.linsir.abc.pdai.tools.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

/**
 * Jackson 树模型示例
 * 演示 JSON 树的创建、修改和遍历操作
 */
public class JacksonTreeModelDemo {

    /**
     * 演示 Jackson 树模型操作
     */
    public static void demonstrateTreeModel() {
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. 创建 JSON 树
            System.out.println("=== 1. 创建 JSON 树 ===");
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("id", 1);
            rootNode.put("name", "John Doe");
            rootNode.put("age", 30);
            rootNode.put("email", "john@example.com");

            // 添加嵌套对象
            ObjectNode addressNode = objectMapper.createObjectNode();
            addressNode.put("street", "123 Main St");
            addressNode.put("city", "New York");
            addressNode.put("zip", "10001");
            rootNode.set("address", addressNode);

            // 添加数组
            ArrayNode phoneNumbersNode = objectMapper.createArrayNode();
            phoneNumbersNode.add("123-456-7890");
            phoneNumbersNode.add("987-654-3210");
            rootNode.set("phoneNumbers", phoneNumbersNode);

            // 转换为 JSON 字符串
            String jsonTree = objectMapper.writeValueAsString(rootNode);
            System.out.println("创建的 JSON 树:");
            System.out.println(jsonTree);

            // 2. 解析 JSON 树
            System.out.println("\n=== 2. 解析 JSON 树 ===");
            JsonNode parsedNode = objectMapper.readTree(jsonTree);

            // 读取字段值
            int id = parsedNode.get("id").asInt();
            String name = parsedNode.get("name").asText();
            int age = parsedNode.get("age").asInt();
            String email = parsedNode.get("email").asText();

            System.out.println("解析的字段值:");
            System.out.println("id: " + id);
            System.out.println("name: " + name);
            System.out.println("age: " + age);
            System.out.println("email: " + email);

            // 读取嵌套对象
            JsonNode address = parsedNode.get("address");
            if (address != null && address.isObject()) {
                String street = address.get("street").asText();
                String city = address.get("city").asText();
                String zip = address.get("zip").asText();
                System.out.println("\n地址信息:");
                System.out.println("street: " + street);
                System.out.println("city: " + city);
                System.out.println("zip: " + zip);
            }

            // 读取数组
            JsonNode phoneNumbers = parsedNode.get("phoneNumbers");
            if (phoneNumbers != null && phoneNumbers.isArray()) {
                System.out.println("\n电话号码:");
                for (JsonNode phoneNode : phoneNumbers) {
                    System.out.println(phoneNode.asText());
                }
            }

            // 3. 修改 JSON 树
            System.out.println("\n=== 3. 修改 JSON 树 ===");
            ObjectNode modifiableNode = (ObjectNode) parsedNode;

            // 修改现有字段
            modifiableNode.put("age", 31);
            modifiableNode.put("email", "john.doe@example.com");

            // 添加新字段
            modifiableNode.put("active", true);

            // 修改嵌套对象
            ObjectNode modifiableAddress = (ObjectNode) modifiableNode.get("address");
            modifiableAddress.put("street", "456 Oak Ave");

            // 修改数组
            ArrayNode modifiablePhones = (ArrayNode) modifiableNode.get("phoneNumbers");
            modifiablePhones.add("555-555-5555");

            // 转换为 JSON 字符串
            String modifiedJson = objectMapper.writeValueAsString(modifiableNode);
            System.out.println("修改后的 JSON 树:");
            System.out.println(modifiedJson);

            // 4. 遍历 JSON 树
            System.out.println("\n=== 4. 遍历 JSON 树 ===");
            System.out.println("遍历根节点字段:");
            traverseJsonNode(parsedNode, "");

        } catch (IOException e) {
            System.err.println("处理 JSON 树时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 递归遍历 JSON 节点
     * @param node JSON 节点
     * @param indent 缩进字符串
     */
    private static void traverseJsonNode(JsonNode node, String indent) {
        if (node.isObject()) {
            // 遍历对象字段
            node.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                System.out.println(indent + fieldName + ":");
                traverseJsonNode(fieldValue, indent + "  ");
            });
        } else if (node.isArray()) {
            // 遍历数组元素
            for (int i = 0; i < node.size(); i++) {
                JsonNode element = node.get(i);
                System.out.println(indent + "[" + i + "]:");
                traverseJsonNode(element, indent + "  ");
            }
        } else if (node.isTextual()) {
            // 文本值
            System.out.println(indent + node.asText());
        } else if (node.isNumber()) {
            // 数字值
            System.out.println(indent + node.numberValue());
        } else if (node.isBoolean()) {
            // 布尔值
            System.out.println(indent + node.asBoolean());
        } else if (node.isNull()) {
            // 空值
            System.out.println(indent + "null");
        }
    }
}
