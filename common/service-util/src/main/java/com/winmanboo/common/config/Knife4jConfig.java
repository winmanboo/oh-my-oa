package com.winmanboo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Knife4jConfig {
  @Bean
  public Docket adminApiConfig() {
    // 添加请求头的参数
    List<Parameter> pars = new ArrayList<>();
    Parameter tokenPar = new ParameterBuilder()
        .name("token")
        .description("用户token")
        .defaultValue("")
        .modelRef(new ModelRef("string"))
        .parameterType("header")
        .required(false)
        .build();
    pars.add(tokenPar);

    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("adminAPi")
        .apiInfo(adminApiInfo())
        .select()
        // 只显示 admin 路径下的页面
        .apis(RequestHandlerSelectors.basePackage("com.winmanboo"))
        .paths(PathSelectors.regex("/admin/.*"))
        .build()
        .globalOperationParameters(pars);
  }

  private ApiInfo adminApiInfo() {
    return new ApiInfoBuilder()
        .title("后台管理系统-API文档")
        .description("本文档描述了后台管理系统微服务接口定义")
        .version("1.0")
        .contact(new Contact("winmanboo", "https://github.com/winmanboo", "winmanboo2332@gmail.com"))
        .build();
  }
}
