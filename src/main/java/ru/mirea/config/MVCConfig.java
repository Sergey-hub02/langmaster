package ru.mirea.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * Класс, содержащий конфигурацию Spring-приложения
 */
@Configuration
@ComponentScan("ru.mirea")
@EnableWebMvc
public class MVCConfig implements WebMvcConfigurer {
  private final ApplicationContext applicationContext;

  /**
   * Конструктор, выполняющий внедрение зависимости контекста приложения
   * @param applicationContext        контекст приложения, в котором хранятся все бины
   */
  @Autowired
  public MVCConfig(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Создаёт бин, управляющий html-шаблонами
   */
  @Bean
  public SpringResourceTemplateResolver templateResolver() {
    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

    templateResolver.setApplicationContext(this.applicationContext);
    templateResolver.setPrefix("/WEB-INF/views/");
    templateResolver.setSuffix(".html");

    return templateResolver;
  }

  /**
   * Создаёт бин, который является движком для шаблонов
   */
  @Bean
  public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();

    templateEngine.setTemplateResolver(this.templateResolver());
    templateEngine.setEnableSpringELCompiler(true);

    return templateEngine;
  }

  /**
   * Настройка шаблонизатора Thymeleaf
   * @param registry        реестр
   */
  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    ThymeleafViewResolver resolver = new ThymeleafViewResolver();
    resolver.setTemplateEngine(this.templateEngine());
    resolver.setContentType("text/html; charset=UTF-8");
    registry.viewResolver(resolver);
  }
}
