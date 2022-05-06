package ru.mirea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mirea.dao.UserDAO;
import ru.mirea.models.User;

import javax.validation.Valid;

/**
 * Обработчик HTTP-запросов приложения
 */
@Controller
@RequestMapping("/langmaster")
public class LangMasterController {
  private User user = null;
  private UserDAO userDAO;

  /**
   * Внедрение зависимости DAO
   * @param userDAO       объект, позволяющий общаться с БД
   */
  @Autowired
  public LangMasterController(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * Отображает главную страницу при GET-запросе
   * на адрес /langmaster
   */
  @GetMapping()
  public String displayIndexPage(Model model) {
    model.addAttribute("user", this.user);
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
  public String displayRegisterPage(Model model) {
    model.addAttribute("user", new User());
    return "pages/register";
  }

  /**
   * Забирает данные из формы регистрации пользователя
   * и добавляет запись о нём в БД
   */
  @PostMapping("/register")
  public String processRegister(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult) {
    if (bindingResult.hasErrors())
      return "pages/register";

    /* Проверка на существование пользователя с указанным именем */
    this.user = this.userDAO.getUser(user.getName());

    if (this.user != null) {
      bindingResult.rejectValue("name",
        "error.user",
        "Имя " + user.getName() + " уже занято!");
      return "pages/register";
    }

    /* Добавление пользователя в БД и авторизация */
    this.userDAO.registerUser(user);
    this.user = this.userDAO.getUser(user.getName());

    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу авторизации при
   * GET-запросе на адрес /langmaster/login
   */
  @GetMapping("/login")
  public String displayLoginPage(Model model) {
    User user = new User();
    user.setEmail("placeholder@mail.com");

    model.addAttribute("user", user);
    return "pages/login";
  }

  /**
   * Получает данные из формы и выполняет авторизацию пользователя
   */
  @PostMapping("/login")
  public String processLogin(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "pages/login";
    }

    User dbUser = this.userDAO.getUser(user.getName());

    if (dbUser != null
      && user.getPassword().equals(dbUser.getPassword())) {
      this.user = dbUser;
      return "redirect:/langmaster";
    }

    bindingResult.rejectValue("password",
      "error.user",
      "Неправильный логин или пароль!");
    return "pages/login";
  }

  /**
   * Выполняет выход из аккаунта
   */
  @PostMapping("/logout")
  public String processLogout() {
    this.user = null;
    return "redirect:/langmaster";
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
