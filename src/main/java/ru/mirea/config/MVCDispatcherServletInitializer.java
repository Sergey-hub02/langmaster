package ru.mirea.config;

import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Настройка DispatcherServlet
 */
public class MVCDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
  @Override
  protected Class<?>[] getRootConfigClasses() {
    return null;
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[] { MVCConfig.class };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] { "/" };
  }

  /**
   * Регистрация Spring-фильтра для скрытых полей формы
   * @param servletContext        контекст Servlet
   */
  private void registerHiddenFieldFilter(ServletContext servletContext) {
    servletContext.addFilter("hiddenHttpMethodFilter",
      new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
  }

  /**
   * Выполняется при инициализации DispatcherServlet
   * @param servletContext          контекст
   * @throws ServletException       исключение
   */
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);
    this.registerHiddenFieldFilter(servletContext);
  }
}
