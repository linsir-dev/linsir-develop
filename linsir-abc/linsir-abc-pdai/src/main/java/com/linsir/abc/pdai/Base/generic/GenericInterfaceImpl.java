package com.linsir.abc.pdai.base.generic;

/**
 * 泛型接口实现类示例
 * 实现时指定具体类型为String
 */
public class GenericInterfaceImpl implements GenericInterface<String> {
    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
