# MapStruct 的原理

MapStruct 是一个 Java 注解处理器，用于自动生成类型安全的 bean 映射代码。它通过编译时生成代码的方式，解决了 Java 应用中对象映射的各种问题。本文将详细分析 MapStruct 的工作原理。

## 1. 核心原理

MapStruct 的核心原理是通过注解处理器在编译时生成类型安全的映射代码。具体来说，MapStruct 通过以下步骤工作：

1. **注解定义映射接口**：用户定义一个映射接口，使用 @Mapper 注解标记，并在接口中定义映射方法。

2. **编译时代码生成**：MapStruct 注解处理器在编译时扫描带有 @Mapper 注解的接口，并根据接口定义和注解配置生成实现类。

3. **类型安全检查**：MapStruct 在编译时进行类型检查，确保源类型和目标类型匹配，避免运行时错误。

4. **生成的代码结构**：生成的实现类包含与接口中定义的方法对应的实现，使用普通的 Java 代码进行字段复制。

5. **运行时使用**：运行时使用生成的实现类进行对象映射，不需要 MapStruct 库，因为所有映射代码都已经在编译时生成。

## 2. 注解处理器

MapStruct 利用 Java 的注解处理 API（Annotation Processing API）来实现其功能。注解处理 API 允许开发者编写自定义注解处理器，在编译时处理注解。

### 2.1 注解处理器的注册

MapStruct 通过在 META-INF/services 目录下创建 javax.annotation.processing.Processor 文件，注册其注解处理器。这样，Java 编译器在编译时会自动加载并调用 MapStruct 的注解处理器。

### 2.2 注解处理器的执行

当 Java 编译器编译带有 MapStruct 注解的代码时，会执行以下步骤：

1. **解析源代码**：编译器解析源代码，生成抽象语法树（AST）。

2. **调用注解处理器**：编译器检查源代码中的注解，并调用相应的注解处理器。

3. **处理注解**：MapStruct 注解处理器处理 @Mapper 等注解，生成映射实现类的源代码。

4. **编译生成的代码**：编译器编译 MapStruct 生成的源代码，生成字节码。

5. **生成最终字节码**：编译器将所有代码（包括原始代码和生成的代码）编译成最终的字节码。

## 3. 映射接口解析

MapStruct 的注解处理器首先解析带有 @Mapper 注解的接口，提取映射方法的信息，包括源类型、目标类型、映射规则等。

### 3.1 映射方法解析

对于每个映射方法，MapStruct 注解处理器会解析以下信息：

- **源参数类型**：方法的参数类型，作为映射的源类型。

- **目标返回类型**：方法的返回类型，作为映射的目标类型。

- **映射注解**：方法上的 @Mapping、@Mappings 等注解，定义字段映射规则。

- **方法名**：方法名可能包含映射方向的信息（如 sourceToTarget）。

### 3.2 类型分析

MapStruct 会分析源类型和目标类型的结构，包括：

- **字段**：类型的字段，包括名称、类型、访问修饰符等。

- **getter/setter 方法**：类型的 getter 和 setter 方法，用于字段访问。

- **构造函数**：类型的构造函数，用于创建目标对象。

- **继承关系**：类型的继承关系，确保正确处理父类的字段。

## 4. 代码生成

MapStruct 的核心功能是生成映射实现类的源代码。生成的代码是普通的 Java 代码，使用 getter 和 setter 方法进行字段复制，没有反射或运行时开销。

### 4.1 实现类生成

对于每个带有 @Mapper 注解的接口，MapStruct 会生成一个实现类，类名通常为接口名加上 "Impl" 后缀。例如，对于 UserMapper 接口，生成的实现类为 UserMapperImpl。

### 4.2 映射方法实现

对于每个映射方法，MapStruct 会生成相应的实现，包括：

- **空值检查**：检查源对象是否为 null，如果是则返回 null。

- **目标对象创建**：创建目标类型的实例，通常使用无参构造函数。

- **字段映射**：使用 getter 方法获取源对象的字段值，使用 setter 方法设置目标对象的字段值。

- **嵌套对象映射**：如果字段是复杂类型，递归调用相应的映射方法。

- **集合类型映射**：如果字段是集合类型，遍历集合元素并调用相应的映射方法。

### 4.3 生成的代码示例

以下是 MapStruct 为 UserMapper 接口生成的实现类示例：

```java
// 生成的实现类
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDTO userToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getFirstName());
        userDTO.setAge(calculateAge(user.getBirthDate()));
        userDTO.setEmail(user.getEmailAddress());
        userDTO.setAddress(addressToAddressDTO(user.getAddress()));

        return userDTO;
    }

    @Override
    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();

        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getName());
        user.setEmailAddress(userDTO.getEmail());
        user.setAddress(addressDTOToAddress(userDTO.getAddress()));

        return user;
    }

    protected AddressDTO addressToAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO addressDTO = new AddressDTO();

        addressDTO.setStreet(address.getStreet());
        addressDTO.setCity(address.getCity());
        addressDTO.setZipCode(address.getZipCode());

        return addressDTO;
    }

    protected Address addressDTOToAddress(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }

        Address address = new Address();

        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setZipCode(addressDTO.getZipCode());

        return address;
    }

    protected int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        // 计算年龄的逻辑
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTime(birthDate);
        int birthYear = cal.get(Calendar.YEAR);
        return currentYear - birthYear;
    }
}
```

### 4.4 代码优化

MapStruct 生成的代码经过了优化，例如：

- **避免不必要的对象创建**：只在需要时创建目标对象。

- **空值检查**：只在必要时进行空值检查，减少不必要的检查。

- **直接字段访问**：在可能的情况下，直接访问字段而不是通过 getter/setter 方法，减少方法调用开销。

- **类型转换**：自动处理基本类型之间的转换（如 int 到 long）。

## 5. 类型安全检查

MapStruct 在编译时进行类型安全检查，确保源类型和目标类型匹配，避免运行时错误。

### 5.1 字段类型匹配检查

MapStruct 会检查源字段和目标字段的类型是否匹配。如果类型不匹配，MapStruct 会尝试以下方式解决：

1. **基本类型转换**：自动处理基本类型之间的转换，如 int 到 long、float 到 double 等。

2. **装箱/拆箱**：自动处理装箱和拆箱操作，如 int 到 Integer、long 到 Long 等。

3. **字符串转换**：自动处理字符串和其他类型之间的转换，如 String 到 int、int 到 String 等。

4. **自定义转换方法**：如果存在自定义的转换方法，使用该方法进行转换。

5. **编译错误**：如果无法解决类型不匹配问题，MapStruct 会在编译时报错。

### 5.2 映射完整性检查

MapStruct 会检查映射是否完整，确保源对象的所有字段都被映射到目标对象，或者明确指定为忽略。如果存在未映射的字段，MapStruct 会在编译时发出警告或错误。

**映射完整性检查示例**：

```
Warning:(10, 5) java: Unmapped target property: "status". Consider to add a mapping for this property.
```

## 6. 依赖注入集成

MapStruct 支持与依赖注入框架集成，如 Spring、CDI 等，生成的实现类可以作为框架的组件被注入和使用。

### 6.1 Spring 集成

通过在 @Mapper 注解中指定 componentModel = "spring"，MapStruct 可以生成 Spring 兼容的实现类，该类会被标记为 @Component，可在 Spring 应用中通过 @Autowired 或构造函数注入使用。

**Spring 集成示例**：

```java
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // 生成 Spring Bean
public interface UserMapper {
    UserDTO userToUserDTO(User user);
}

// 生成的实现类
@Component
public class UserMapperImpl implements UserMapper {
    // 实现代码...
}

// 在 Spring 组件中注入使用
@Service
public class UserService {
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    // 使用 userMapper...
}
```

### 6.2 CDI 集成

通过在 @Mapper 注解中指定 componentModel = "cdi"，MapStruct 可以生成 CDI 兼容的实现类，该类会被标记为 @javax.inject.Named，可在 CDI 应用中通过 @Inject 注入使用。

## 7. 自定义映射逻辑

MapStruct 允许在映射接口中定义自定义方法，实现复杂的映射逻辑。这些自定义方法会被生成的实现类调用。

### 7.1 默认方法

Java 8 引入了默认方法，MapStruct 支持在映射接口中定义默认方法，实现自定义映射逻辑。

**默认方法示例**：

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserMapper {
    @Mapping(source = "birthDate", target = "age", qualifiedByName = "calculateAge")
    UserDTO userToUserDTO(User user);
    
    @Named("calculateAge")
    default int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        // 计算年龄的逻辑
        return ...;
    }
}
```

### 7.2 静态方法

MapStruct 也支持在映射接口中定义静态方法，实现自定义映射逻辑。

**静态方法示例**：

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserMapper {
    @Mapping(source = "birthDate", target = "age", qualifiedByName = "calculateAge")
    UserDTO userToUserDTO(User user);
    
    @Named("calculateAge")
    static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        // 计算年龄的逻辑
        return ...;
    }
}
```

## 8. 与 Lombok 集成

MapStruct 可以与 Lombok 无缝集成，处理 Lombok 生成的 getter/setter 方法、构造函数等。

### 8.1 集成原理

MapStruct 会在编译时分析 Lombok 生成的代码，包括：

- **getter/setter 方法**：由 @Getter、@Setter 注解生成的方法。

- **构造函数**：由 @NoArgsConstructor、@AllArgsConstructor 等注解生成的构造函数。

- **字段**：由 @Value 注解生成的不可变字段。

### 8.2 使用示例

**Lombok 与 MapStruct 集成示例**：

```java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private int age;
    private String email;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private int age;
    private String email;
}

import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}

// MapStruct 会自动识别 Lombok 生成的 getter/setter 方法和构造函数
```

## 9. 运行时行为

MapStruct 是一个编译时工具，运行时不需要 MapStruct 库。生成的映射代码是普通的 Java 代码，使用 getter 和 setter 方法进行字段复制，没有反射或运行时开销。

### 9.1 无运行时依赖

MapStruct 的核心依赖（mapstruct）只在编译时需要，运行时不需要。这意味着生成的映射代码可以在任何 Java 环境中运行，不需要额外的依赖。

### 9.2 性能优势

由于生成的映射代码是普通的 Java 代码，没有反射或运行时开销，因此性能与手动编写的映射代码相同，甚至可能更高（因为 MapStruct 生成的代码经过了优化）。

**性能对比**：

| 映射方式 | 性能 | 类型安全 | 编译时检查 | 维护性 |
|---------|------|---------|-----------|--------|
| MapStruct | 高 | 是 | 是 | 高 |
| 手动映射 | 高 | 是 | 是 | 低 |
| Dozer | 中 | 否 | 否 | 中 |
| ModelMapper | 中 | 否 | 否 | 中 |

## 10. 总结

MapStruct 的核心原理是通过注解处理器在编译时生成类型安全的映射代码。它具有以下特点：

1. **编译时生成代码**：MapStruct 在编译时生成映射实现类的源代码，编译成字节码后，运行时不需要 MapStruct 库。

2. **类型安全**：MapStruct 在编译时进行类型安全检查，确保源类型和目标类型匹配，避免运行时错误。

3. **高性能**：生成的映射代码是普通的 Java 代码，使用 getter 和 setter 方法进行字段复制，没有反射或运行时开销。

4. **可维护性**：通过注解定义清晰的映射规则，自动更新映射代码，减少维护成本。

5. **灵活性**：支持自定义映射逻辑、嵌套对象映射、集合类型映射等复杂场景。

6. **框架集成**：与 Spring、CDI 等依赖注入框架无缝集成。

7. **Lombok 集成**：与 Lombok 无缝集成，处理 Lombok 生成的代码。

MapStruct 是 Java 应用中对象映射的理想解决方案，它结合了手动映射的高性能和类型安全，以及自动映射框架的便捷性和可维护性。
