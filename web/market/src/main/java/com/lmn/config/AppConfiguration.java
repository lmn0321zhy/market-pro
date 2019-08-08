package com.lmn.config;

import cn.wenwuyun.common.servlet.UserfilesDownloadServlet;
import cn.wenwuyun.common.servlet.ValidateCodeServlet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.wickedcharts.highcharts.jackson.*;
import com.googlecode.wickedcharts.highcharts.json.LowercaseEnum;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.*;
import com.googlecode.wickedcharts.highcharts.options.series.Bubble;
import com.googlecode.wickedcharts.highcharts.options.series.Coordinate;
import com.googlecode.wickedcharts.highcharts.options.series.RangeCoordinate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.validation.Validator;

/**
 * 自动配置
 */
@Configuration
@EnableCaching
@MapperScan(basePackages = "cn.wenwuyun.modules.**.dao")
@ImportResource({"classpath*:spring-*.xml"})
public class AppConfiguration {

    /**
     * 配置cors,以便支持跨域
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        /*config.addAllowedOrigin("http://localhost:8088");
        config.addAllowedOrigin("http://127.0.0.1:8088");*/
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/front/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    /**
     * 图像文件自动缩放
     */
    @Bean
    public ServletRegistrationBean dispatcherUserFiles() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new UserfilesDownloadServlet());
        registration.addUrlMappings("/userfiles/*");
        return registration;
    }

    /**
     * 验证码服务
     */
    @Bean
    public ServletRegistrationBean dispatcherRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ValidateCodeServlet());
        registration.addUrlMappings("/servlet/validateCodeServlet");
        return registration;
    }


    /**
     * 自定义json序列化类
     */
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        //下列为highcharts的曲线序列化类型
        builder.serializerByType(BoxSeriesSerializer.class, new BoxSeriesSerializer());
        builder.serializerByType(Bubble.class, new BubbleSerializer());
        builder.serializerByType(Center.class, new CenterSerializer());
        builder.serializerByType(Crosshair.class, new CrosshairSerializer());
        builder.serializerByType(CssStyle.class, new CssStyleSerializer());
        builder.serializerByType(DateTimeLabelFormat.class, new DateTimeLabelFormatSerializer());
        builder.serializerByType(Function.class, new FunctionSerializer());
        builder.serializerByType(HexColor.class, new HexColorReferenceSerializer());
        builder.serializerByType(HighchartsColor.class, new HighchartsColorReferenceSerializer());

        builder.serializerByType(PixelOrPercent.class, new PixelOrPercentSerializer());
        builder.serializerByType(LowercaseEnum.class, new LowercaseEnumSerializer());
        builder.serializerByType(SimpleColor.class, new SimpleColorReferenceSerializer());
        builder.serializerByType(Symbol.class, new SymbolSerializer());
        builder.serializerByType(RgbaColor.class, new RgbaColorReferenceSerializer());
        builder.serializerByType(NullColor.class, new NullColorReferenceSerializer());
        builder.serializerByType(MinorTickInterval.class, new MinorTickIntervalSerializer());
        builder.serializerByType(Coordinate.class, new CoordinateSerializer());
        builder.serializerByType(RangeCoordinate.class, new RangeCoordinateSerializer());

        return builder;
    }
    /*@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new WickedChartsJacksonModule());
        return objectMapper;
    }*/


    /**
     * 将Jackson2HttpMessageConverter的默认格式化输出为false
     */
    /*@Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8,
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.TEXT_HTML,
                new MediaType("application", "*+json", Charset.forName("UTF-8"))));
        return converter;
    }*/

    /**
     * 配置 JSR303 Bean Validator 定义
     */
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
