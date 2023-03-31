package com.winmanboo.oh_my_oa;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

public class CodeGen {
  public static void main(String[] args) {
    FastAutoGenerator.create("jdbc:mysql://localhost:3306/oh-my-oa?serverTimezone=GMT%2B8&useSSL=false", "root", "mingge123")
        .globalConfig(builder -> {
          builder.author("winmanboo") // 设置作者
              .enableSwagger() // 开启 swagger 模式
              .fileOverride() // 覆盖已生成文件
              .outputDir("/Users/wangzhiming/StudyProjects/oh-my-oa/service-oa/src/main/java"); // 指定输出目录
        })
        .packageConfig(builder -> {
          builder.parent("com.winmanboo") // 设置父包名
              .moduleName("oh_my_oa") // 设置父包模块名
              .controller("controller")
              .service("service")
              .mapper("mapper")
              .pathInfo(Collections.singletonMap(OutputFile.xml, "/Users/wangzhiming/StudyProjects/oh-my-oa/service-oa/src/main/resources/mapper")); // 设置mapperXml生成路径
        })
        .strategyConfig(builder -> {
          builder.addInclude("sys_user_role") // 设置需要生成的表名
              .entityBuilder() // 实体类配置
              .naming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
              .columnNaming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
              .enableLombok() // lombok 模型 @Accessor(chain = true) setter 链式操作
              .controllerBuilder() // controller 配置
              .enableRestStyle() // restful api 风格
              .enableHyphenStyle() // url 中驼峰赚连字符
              .serviceBuilder()
              .formatServiceFileName("%sService");
        })
        .templateEngine(new VelocityTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
        .execute();
  }
}
