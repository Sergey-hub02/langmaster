package ru.mirea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/langmaster")
public class LangMasterController {
  /**
   * Отображает главную страницу при GET-запросе
   * на адрес /langmaster
   */
  @GetMapping()
  public String displayIndexPage() {
    return "index";
  }

  /**
   * Отображает страницу профиля пользователя при
   * GET-запросе на адрес /langmaster/profile
   */
  @GetMapping("/profile")
  public String displayProfilePage() {
    return "pages/profile";
  }

  /**
   * Отображает страницу регистрации при
   * GET-запросе на адрес /langmaster/register
   */
  @GetMapping("/register")
  public String displayRegisterPage() {
    return "pages/register";
  }

  /**
   * Отображает страницу авторизации при
   * GET-запросе на адрес /langmaster/login
   */
  @GetMapping("/login")
  public String displayLoginPage() {
    return "pages/login";
  }

  /**
   * Отображает страницу курса при
   * GET-запросе на адрес /langmaster/course
   */
  // TODO: добавить @PathVariable для id курса
  @GetMapping("/course")
  public String displayCoursePage() {
    return "pages/course";
  }

  /**
   * Отображает страницу уроков курса при
   * GET-запросе на адрес /langmaster/course/lesson
   */
  // TODO: добавить @PathVariable для id урока
  @GetMapping("/course/lesson")
  public String displayLessonsPage() {
    return "pages/lessons";
  }
}
