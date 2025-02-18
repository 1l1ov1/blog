package com.blog.auth.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    @Value("${kaptcha.border:yes}")
    private String border;
    @Value("${kaptcha.border.color:105,179,90}")
    private String borderColor;
    @Value("${kaptcha.image.width:120}")
    private String imageWidth;
    @Value("${kaptcha.image.height:40}")
    private String imageHeight;
    @Value("${kaptcha.textproducer.font.color:blue}")
    private String fontColor;
    @Value("${kaptcha.textproducer.char.length:4}")
    private String charLength;
    @Value("${kaptcha.textproducer.char.string:0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz}")
    private String charString;
    @Value("${kaptcha.session.key:kaptcha_code}")
    private String sessionKey;
    @Value("${kaptcha.obscurificator.impl:com.google.code.kaptcha.impl.WaterRipple}")
    private String obscurificatorImpl;
    @Value("${kaptcha.background.impl:com.google.code.kaptcha.impl.NoNoise}")
    private String backgroundImpl;
    @Value("${kaptcha.background.clear.from:230,230,250}")
    private String backgroundClearFrom;
    @Value("${kaptcha.background.clear.to:164,164,188}")
    private String backgroundClearTo;
    @Value("${kaptcha.textproducer.font.size:30}")
    private String fontSize;
    @Value("${kaptcha.textproducer.font.names:Arial,Courier}")
    private String fontNames;
    @Value("${kaptcha.noise.impl}")
    private String noiseImpl;
    /**
     * 创建并配置一个DefaultKaptcha bean
     * 该方法主要用于Spring框架中，通过注解@Bean来定义一个bean
     * 这里我们配置了DefaultKaptcha的多个属性，以满足项目中验证码功能的需求
     *
     * @return DefaultKaptcha实例，带有配置好的属性
     */
    @Bean
    public Producer kaptchaProducer() {
        // 实例化DefaultKaptcha对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        // 创建Properties对象，用于存储Kaptcha的配置属性
        Properties properties = new Properties();

        // 设置验证码边框，是否显示
        properties.setProperty(Constants.KAPTCHA_BORDER, border);

        // 设置边框颜色
        properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, borderColor);

        // 设置验证码图片宽度
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, imageWidth);

        // 设置验证码图片高度
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, imageHeight);

        // 设置验证码字体颜色
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, fontColor);

        // 设置验证码字符长度
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, charLength);

        // 设置验证码字符集合
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, charString);

        // 设置Session Key，用于保存验证码信息到Session中
        properties.setProperty(Constants.KAPTCHA_SESSION_KEY, sessionKey);

        // 设置验证码干扰实现类
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, obscurificatorImpl);

        // 设置背景实现类
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_IMPL, backgroundImpl);

        // 设置背景颜色范围
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, backgroundClearFrom);
        properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, backgroundClearTo);

        // 设置字体大小
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, fontSize);

        // 设置字体名称
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, fontNames);

        // 设置噪点实现类
        properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, noiseImpl);
        // 实例化Config对象，并传入配置属性
        Config config = new Config(properties);

        // 将Config对象设置到DefaultKaptcha实例中
        defaultKaptcha.setConfig(config);

        // 返回配置好的DefaultKaptcha实例
        return defaultKaptcha;
    }
}
