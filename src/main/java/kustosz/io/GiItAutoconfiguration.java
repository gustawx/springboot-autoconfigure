package kustosz.io;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import kustosz.io.services.ConverterService;
import kustosz.io.services.GifEncoderService;
import kustosz.io.services.VideoDecoderService;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;

@Configuration
@ConditionalOnClass({FFmpegFrameGrabber.class, AnimatedGifEncoder.class})
public class GiItAutoconfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "com.justgifit", name = "create-result-dir")
    public Boolean createResultDir() {
        File gifFolder = new File(gifLocation);
        if (!gifFolder.exists()) {
            gifFolder.mkdir();
        }
        return true;
    }

    @Value("${multipart.location}/gif/")
    private String gifLocation;

    @Bean
    @ConditionalOnMissingBean(VideoDecoderService.class)
    public VideoDecoderService videoDecoderService(){
        return new VideoDecoderService();
    }

    @Bean
    @ConditionalOnMissingBean(GifEncoderService.class)
    public GifEncoderService gifEncoderService(){
        return new GifEncoderService();
    }

    @Bean
    @ConditionalOnMissingBean(ConverterService.class)
    public ConverterService converterService(){
        return new ConverterService();
    }

    @Configuration
    @ConditionalOnWebApplication
    public static class WebConfiguration{

        @Value("${multipart.location}/gif/")
        private String gifLocation;

        @Bean
        @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
        public FilterRegistrationBean deRegisterHiddenHttpMethodFilter (HiddenHttpMethodFilter filter ){
            FilterRegistrationBean bean = new FilterRegistrationBean(filter);
            bean.setEnabled(false);
            return bean;
        }

        @Bean
        @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
        public FilterRegistrationBean deRegisterHttpPutFormContentFilter ( HttpPutFormContentFilter filter ){
            FilterRegistrationBean bean = new FilterRegistrationBean(filter);
            bean.setEnabled(false);
            return bean;
        }

        @Bean
        @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
        public FilterRegistrationBean deRegisterRequestContextFilter ( RequestContextFilter filter ){
            FilterRegistrationBean bean = new FilterRegistrationBean(filter);
            bean.setEnabled(false);
            return bean;
        }

        @Bean
        @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry registry) {
                    registry.addResourceHandler("/gif/**")
                            .addResourceLocations("file:" + gifLocation);
                    super.addResourceHandlers(registry);
                }
            };
        }
    }
}
