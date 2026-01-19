package com.linsir.subject.sms.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author linsir
 * @since 2023-12-12
 */
@Getter
@Setter
@TableName("sys_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;
}
