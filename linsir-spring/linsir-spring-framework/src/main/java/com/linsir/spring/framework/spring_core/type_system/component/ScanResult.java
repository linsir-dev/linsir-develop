package com.linsir.spring.framework.spring_core.type_system.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描结果
 * 封装组件扫描的完整结果
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ScanResult {

    /**
     * 扫描到的组件列表
     */
    private List<ComponentInfo> components;

    /**
     * 扫描到的类名列表
     */
    private final List<String> scannedClasses;

    /**
     * 被排除的类名列表
     */
    private final List<String> excludedClasses;

    /**
     * 错误信息列表
     */
    private final List<String> errors;

    public ScanResult() {
        this.components = new ArrayList<>();
        this.scannedClasses = new ArrayList<>();
        this.excludedClasses = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public List<ComponentInfo> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentInfo> components) {
        this.components = components;
    }

    public List<String> getScannedClasses() {
        return scannedClasses;
    }

    public void addScannedClass(String className) {
        this.scannedClasses.add(className);
    }

    public List<String> getExcludedClasses() {
        return excludedClasses;
    }

    public void addExcludedClass(String className) {
        this.excludedClasses.add(className);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    /**
     * 获取扫描统计信息
     *
     * @return 统计信息字符串
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("扫描统计:\n");
        sb.append("  扫描到的组件: ").append(components.size()).append("\n");
        sb.append("  扫描到的类: ").append(scannedClasses.size()).append("\n");
        sb.append("  排除的类: ").append(excludedClasses.size()).append("\n");
        sb.append("  错误数: ").append(errors.size()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "components=" + components.size() +
                ", scannedClasses=" + scannedClasses.size() +
                ", excludedClasses=" + excludedClasses.size() +
                ", errors=" + errors.size() +
                '}';
    }
}
