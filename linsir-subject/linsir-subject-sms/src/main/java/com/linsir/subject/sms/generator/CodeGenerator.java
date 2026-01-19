package com.linsir.subject.sms.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.Entity;

/**
 * @ClassName : CodeGenerator
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-11 18:06
 */

public class CodeGenerator {

    /**
     * 策略配置
     */
    protected static StrategyConfig.Builder strategyConfig() {
        return new StrategyConfig.Builder();
    }

    protected  static Entity.Builder strategyConfigEntity()
    {
        return new StrategyConfig.Builder().entityBuilder()
                .enableLombok()
                .idType(IdType.AUTO);
    }



    /**
     * 全局配置
     */
    protected static GlobalConfig.Builder globalConfig() {
        return new GlobalConfig.Builder()
                .author("linsir");
    }

    /**
     * 包配置
     */
    protected static PackageConfig.Builder packageConfig() {
        return new PackageConfig.Builder()
                .parent("com.linsir.subject.sms"); //最高级的报名
    }




    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
            .Builder("jdbc:mysql://106.55.181.141:3306/linsir-subject-sms?useUnicode=true&characterEncoding=utf8", "root", "yu@2023")
            .schema("linsir-subject-sms")
            .build();

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.strategy(strategyConfigEntity().build());
        generator.global(globalConfig().build());
        generator.packageInfo(packageConfig().build());
        generator.execute();
    }
}
