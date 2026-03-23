package com.linsir.spring.framework.spring_core.conversion.support;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;
import com.linsir.spring.framework.spring_core.conversion.descriptor.TypeDescriptor;
import com.linsir.spring.framework.spring_core.conversion.exception.ConversionException;
import com.linsir.spring.framework.spring_core.conversion.factory.ConverterFactory;
import com.linsir.spring.framework.spring_core.conversion.generic.GenericConverter;
import com.linsir.spring.framework.spring_core.conversion.service.ConfigurableConversionService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 通用类型转换服务实现
 * 提供完整的类型转换功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class GenericConversionService implements ConfigurableConversionService {

    private final Set<GenericConverter> converters = new CopyOnWriteArraySet<>();
    private final Map<ConverterCacheKey, GenericConverter> converterCache = new ConcurrentHashMap<>();

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }
        return canConvert(TypeDescriptor.valueOf(sourceType), TypeDescriptor.valueOf(targetType));
    }

    @Override
    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }
        return getConverter(sourceType, targetType) != null;
    }

    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
        return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType == null || targetType == null) {
            throw new IllegalArgumentException("Source and target type must not be null");
        }

        // 如果源值为 null，直接返回 null
        if (source == null) {
            return null;
        }

        // 如果类型相同，直接返回源对象
        if (sourceType.getType().equals(targetType.getType())) {
            return source;
        }

        GenericConverter converter = getConverter(sourceType, targetType);
        if (converter == null) {
            throw new ConversionException(sourceType.getType(), targetType.getType(), source,
                    new IllegalStateException("No converter found"));
        }

        try {
            return converter.convert(source, sourceType, targetType);
        } catch (Exception e) {
            throw new ConversionException(sourceType.getType(), targetType.getType(), source, e);
        }
    }

    @Override
    public <S, T> void addConverter(Converter<S, T> converter) {
        // 简化实现：通过反射获取泛型参数类型
        // 实际实现需要更复杂的泛型解析逻辑
        throw new UnsupportedOperationException("Please use addConverter(Class<S>, Class<T>, Converter<S, T>) instead");
    }

    @Override
    public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter) {
        @SuppressWarnings("unchecked")
        ConverterAdapter<S, T> adapter = new ConverterAdapter<>(converter, sourceType, targetType);
        addConverterInternal(adapter);
    }

    @Override
    public <S, R> void addConverterFactory(ConverterFactory<S, R> factory) {
        // 简化实现
        throw new UnsupportedOperationException("Please use addConverter(GenericConverter) with custom adapter instead");
    }

    @Override
    public void addConverter(GenericConverter converter) {
        addConverterInternal(converter);
    }

    @Override
    public void removeConvertible(Class<?> sourceType, Class<?> targetType) {
        converters.removeIf(converter -> {
            Set<GenericConverter.ConvertiblePair> pairs = converter.getConvertibleTypes();
            for (GenericConverter.ConvertiblePair pair : pairs) {
                if (pair.getSourceType().equals(sourceType) && pair.getTargetType().equals(targetType)) {
                    return true;
                }
            }
            return false;
        });
        converterCache.clear();
    }

    /**
     * 添加内置转换器
     */
    public void addDefaultConverters() {
        // 字符串转数字
        addConverter(String.class, Integer.class, new StringToIntegerConverter());
        addConverter(String.class, Long.class, new StringToLongConverter());
        addConverter(String.class, Double.class, new StringToDoubleConverter());
        addConverter(String.class, Boolean.class, new StringToBooleanConverter());

        // 数字互转
        addConverter(Number.class, Number.class, new NumberToNumberConverter());

        // 字符串转枚举
        addConverter(String.class, Enum.class, new StringToEnumConverter());

        // 数组和集合转换
        addConverter(new ArrayToCollectionConverter(this));
        addConverter(new CollectionToArrayConverter(this));
    }

    private void addConverterInternal(GenericConverter converter) {
        converters.add(converter);
        converterCache.clear();
    }

    private GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConverterCacheKey key = new ConverterCacheKey(sourceType.getType(), targetType.getType());
        GenericConverter converter = converterCache.get(key);

        if (converter == null) {
            converter = findConverter(sourceType, targetType);
            if (converter != null) {
                converterCache.put(key, converter);
            }
        }

        return converter;
    }

    private GenericConverter findConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        for (GenericConverter converter : converters) {
            Set<GenericConverter.ConvertiblePair> pairs = converter.getConvertibleTypes();
            for (GenericConverter.ConvertiblePair pair : pairs) {
                if (pair.getSourceType().isAssignableFrom(sourceType.getType()) &&
                        pair.getTargetType().isAssignableFrom(targetType.getType())) {
                    return converter;
                }
            }
        }
        return null;
    }

    /**
     * 添加 ConverterFactory 适配器
     *
     * @param factory 转换器工厂
     * @param sourceType 源类型
     * @param targetType 目标类型
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addConverterFactory(ConverterFactory factory, Class sourceType, Class targetType) {
        ConverterFactoryAdapter adapter = new ConverterFactoryAdapter(factory, sourceType, targetType);
        addConverterInternal(adapter);
    }

    /**
     * 转换器缓存键
     */
    private static class ConverterCacheKey {
        private final Class<?> sourceType;
        private final Class<?> targetType;

        public ConverterCacheKey(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConverterCacheKey that = (ConverterCacheKey) o;
            return Objects.equals(sourceType, that.sourceType) &&
                    Objects.equals(targetType, that.targetType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceType, targetType);
        }
    }

    /**
     * Converter 适配器
     */
    private static class ConverterAdapter<S, T> implements GenericConverter {
        private final Converter<S, T> converter;
        private final Class<S> sourceType;
        private final Class<T> targetType;

        public ConverterAdapter(Converter<S, T> converter, Class<S> sourceType, Class<T> targetType) {
            this.converter = converter;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(sourceType, targetType));
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return converter.convert((S) source);
        }
    }

    /**
     * ConverterFactory 适配器 - 使用原始类型避免复杂泛型问题
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class ConverterFactoryAdapter implements GenericConverter {
        private final ConverterFactory factory;
        private final Class sourceType;
        private final Class targetType;

        public ConverterFactoryAdapter(ConverterFactory factory, Class sourceType, Class targetType) {
            this.factory = factory;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            Set<ConvertiblePair> pairs = new HashSet<>();
            pairs.add(new ConvertiblePair(sourceType, targetType));
            return pairs;
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            // 使用原始类型绕过泛型检查
            Converter converter = factory.getConverter(targetType.getType());
            return converter.convert(source);
        }
    }

    // 内置转换器实现

    /**
     * 字符串转整数转换器
     */
    public static class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return Integer.valueOf(source.trim());
        }
    }

    /**
     * 字符串转长整数转换器
     */
    public static class StringToLongConverter implements Converter<String, Long> {
        @Override
        public Long convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return Long.valueOf(source.trim());
        }
    }

    /**
     * 字符串转双精度浮点数转换器
     */
    public static class StringToDoubleConverter implements Converter<String, Double> {
        @Override
        public Double convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return Double.valueOf(source.trim());
        }
    }

    /**
     * 字符串转布尔值转换器
     */
    public static class StringToBooleanConverter implements Converter<String, Boolean> {
        @Override
        public Boolean convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return Boolean.valueOf(source.trim());
        }
    }

    /**
     * 数字互转转换器
     */
    public static class NumberToNumberConverter implements Converter<Number, Number> {
        @Override
        public Number convert(Number source) {
            return source;
        }
    }

    /**
     * 字符串转枚举转换器
     */
    @SuppressWarnings("unchecked")
    public static class StringToEnumConverter implements Converter<String, Enum> {
        @Override
        public Enum convert(String source) {
            // 实际使用时需要知道目标枚举类型
            // 这里简化处理
            return null;
        }
    }

    /**
     * 数组转集合转换器
     */
    public static class ArrayToCollectionConverter implements GenericConverter {
        private final GenericConversionService conversionService;

        public ArrayToCollectionConverter(GenericConversionService conversionService) {
            this.conversionService = conversionService;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            Set<ConvertiblePair> pairs = new HashSet<>();
            pairs.add(new ConvertiblePair(Object[].class, Collection.class));
            return pairs;
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return null;
            }

            Object[] sourceArray = (Object[]) source;
            Collection<Object> targetCollection = new ArrayList<>();

            TypeDescriptor elementType = targetType.getElementTypeDescriptor();
            if (elementType == null) {
                Collections.addAll(targetCollection, sourceArray);
            } else {
                for (Object element : sourceArray) {
                    Object converted = conversionService.convert(element,
                            TypeDescriptor.forObject(element), elementType);
                    targetCollection.add(converted);
                }
            }

            return targetCollection;
        }
    }

    /**
     * 集合转数组转换器
     */
    public static class CollectionToArrayConverter implements GenericConverter {
        private final GenericConversionService conversionService;

        public CollectionToArrayConverter(GenericConversionService conversionService) {
            this.conversionService = conversionService;
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            Set<ConvertiblePair> pairs = new HashSet<>();
            pairs.add(new ConvertiblePair(Collection.class, Object[].class));
            return pairs;
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return null;
            }

            Collection<?> sourceCollection = (Collection<?>) source;
            Class<?> componentType = targetType.getType().getComponentType();
            Object[] targetArray = (Object[]) java.lang.reflect.Array.newInstance(componentType, sourceCollection.size());

            int i = 0;
            for (Object element : sourceCollection) {
                Object converted = conversionService.convert(element,
                        TypeDescriptor.forObject(element), TypeDescriptor.valueOf(componentType));
                targetArray[i++] = converted;
            }

            return targetArray;
        }
    }
}
