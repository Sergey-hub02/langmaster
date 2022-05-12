package ru.mirea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.mirea.dao.CourseDAO;
import ru.mirea.dao.LessonDAO;
import ru.mirea.dao.UserDAO;
import ru.mirea.models.Course;
import ru.mirea.models.Lesson;
import ru.mirea.models.User;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * Обработчик HTTP-запросов приложения
 */
@Controller
@RequestMapping("/langmaster")
public class LangMasterController {
  private User user = null;

  private UserDAO userDAO;
  private CourseDAO courseDAO;
  private LessonDAO lessonDAO;
  private ServletContext servletContext;

  /* Объект, позволяющий хэшировать пароли */
  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Внедрение зависимостей
   * @param userDAO                   объект, позволяющий работать с пользователями
   * @param courseDAO                 объект, позволяющий работать с курсами
   * @param lessonDAO                 объект, позволяющий работать с уроками
   * @param servletContext            контекст приложения
   */
  @Autowired
  public LangMasterController(UserDAO userDAO,
                              CourseDAO courseDAO,
                              LessonDAO lessonDAO,
                              ServletContext servletContext) {
    this.userDAO = userDAO;
    this.courseDAO = courseDAO;
    this.lessonDAO = lessonDAO;
    this.servletContext = servletContext;
  }

  /**
   * Отображает главную страницу при GET запросе на /langmaster
   * @param model       объект для передачи данных шаблонизатору
   */
  @GetMapping()
  public String displayIndexPage(Model model) {
    model.addAttribute("user", this.user);

    // Если пользователь авторизован, то проверяем, является ли он админом
    if (this.user != null) {
      final int userId = this.user.getId();
      model.addAttribute("createdCourses", this.courseDAO.getCreatedCourses(userId));

      model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
      model.addAttribute("createdCourses", this.courseDAO.getCreatedCourses(userId));
      model.addAttribute("currentCourses", this.courseDAO.getCurrentCourses(userId));
    }

    model.addAttribute("allCourses", this.courseDAO.getAllCourses());
    return "index";
  }

  /**
   * Отображает страницу профиля при GET запросе /langmaster/profile/{name}
   * @param name        имя пользователя
   * @param model       объект для передачи данных шаблонизатору
   */
  @GetMapping("/profile/{name}")
  public String displayProfilePage(@PathVariable("name") String name, Model model) {
    // Если пользователь не авторизован, то происходит переадресация на страницу авторизации
    if (this.user == null)
      return "redirect:/langmaster/login";

    final int userId = this.user.getId();

    model.addAttribute("user", this.userDAO.getUser(name));
    model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
    model.addAttribute("createdCourses", this.courseDAO.getCreatedCourses(userId));
    model.addAttribute("currentCourses", this.courseDAO.getCurrentCourses(userId));

    return "pages/profile";
  }

  /**
   * Отображает страницу регистрации при GET запросе /langmaster/register
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/register")
  public String displayRegisterPage(Model model) {
    model.addAttribute("user", new User());
    return "pages/register";
  }

  /**
   * Регистрирует пользователя в базе данных, используя данные из формы
   * @param user                объект пользователя
   * @param bindingResult       объект, содержащий ошибки при заполнении полей формы
   */
  @PostMapping("/register")
  public String processRegister(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult) {
    // Отображение ошибок под полями ввода
    if (bindingResult.hasErrors())
      return "pages/register";

    // Проверка на существование пользователя с указанным именем
    this.user = this.userDAO.getUser(user.getName());

    if (this.user != null) {
      // Отображение ошибки уникальности имени пользователя
      bindingResult.rejectValue("name",
        "error.user",
        "Имя " + user.getName() + " уже занято!");
      return "pages/register";
    }

    // Шифрование пароля
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Добавление пользователя в БД и авторизация
    this.userDAO.registerUser(user);
    this.user = this.userDAO.getUser(user.getName());

    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу обновления информации о пользователе
   * @param userId      id пользователя
   */
  @GetMapping("/update/{userId}")
  public String displayUpdateUserPage(@PathVariable("userId") int userId,
                                      Model model) {
    // Нельзя изменить информацию о пользователе без авторизации
    // Также нельзя, будучи авторизованным, изменить информацию другого пользователя
    if (this.user == null || this.user.getId() != userId)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    model.addAttribute("userId", userId);
    model.addAttribute("user", this.user);

    return "pages/updateUser";
  }

  /**
   * Обновляет данные о пользователе
   * @param userId          id пользователя
   * @param user            объект пользователя
   * @param bindingResult   объект, содержащий ошибки при заполнении полей формы
   */
  @PatchMapping("/update/{userId}")
  public String processUpdateUser(@PathVariable("userId") int userId,
                                  @ModelAttribute("user") User user,
                                  BindingResult bindingResult) {
    // Отображение ошибок при заполнении полей формы
    if (bindingResult.hasErrors())
      return "pages/updateUser";

    String userPassword = this.userDAO.getUser(userId).getPassword();

    user.setId(userId);
    user.setPassword(userPassword);

    this.userDAO.updateUser(user);
    return "redirect:/langmaster";
  }

  /**
   * Удаляет пользователя из БД
   * @param userId    id пользователя
   */
  @DeleteMapping("/delete/{userId}")
  public String processDeleteUser(@PathVariable("userId") int userId) {
    this.userDAO.deleteUser(userId);
    this.user = null;

    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу авторизации при GET запросе /langmaster/login
   * @param model       объект для передачи данных шаблонизатору
   */
  @GetMapping("/login")
  public String displayLoginPage(Model model) {
    // Здесь используется ненастоящий адрес электронной почты, чтобы не возникла ошибка
    User user = new User();
    user.setEmail("placeholder@mail.com");

    model.addAttribute("user", user);
    return "pages/login";
  }

  /**
   * Выполняет авторизацию пользователя с помощью данных из формы
   * @param user              объект пользователя
   * @param bindingResult     объект, содержащий ошибки в заполнении полей формы
   */
  @PostMapping("/login")
  public String processLogin(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult) {
    // Отображение ошибок под полями ввода
    if (bindingResult.hasErrors())
      return "pages/login";

    User dbUser = this.userDAO.getUser(user.getName());

    // Незакодированный пароль, т.е. пароль из формы
    String userPassword = user.getPassword();

    // Сравнение введённого пароля с закодированным
    if (dbUser != null
      && passwordEncoder.matches(userPassword, dbUser.getPassword())) {
      this.user = dbUser;
      return "redirect:/langmaster";
    }

    // Отображение ошибки с логином или паролем
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
   * Отображает страницу курса при GET запросе /langmaster/course/{courseId}
   * @param courseId      id отображаемого курса
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/{courseId}")
  public String displayCoursePage(@PathVariable("courseId") int courseId, Model model) {
    model.addAttribute("user", this.user);

    if (this.user != null) {
      final int userId = this.user.getId();

      model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
      model.addAttribute("courseIsInUserList", this.courseDAO.courseIsInUserList(userId, courseId));
      model.addAttribute("userIsCourseCreator", this.courseDAO.userIsCourseCreator(userId, courseId));
    }

    // Если пользователь не авторизован, то отображается только общая информация о курсе
    model.addAttribute("course", this.courseDAO.getCourse(courseId));
    model.addAttribute("courseLessons", this.lessonDAO.getCourseLessons(courseId));

    return "pages/course";
  }

  /**
   * Отображает страницу создания курса
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/new")
  public String displayCourseCreationPage(Model model) {
    // Пользователь не может создать курс, если он не авторизован или не является админом
    if (this.user == null || !this.userDAO.isAdmin(this.user.getId()))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    model.addAttribute("user", this.user);
    model.addAttribute("course", new Course());

    return "pages/courseCreation";
  }

  /**
   * Отображает страницу изменения данных о курсе
   * @param courseId      id курса
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/{courseId}/edit")
  public String displayUpdateCoursePage(@PathVariable("courseId") int courseId,
                                        Model model) {
    // Нельзя изменить курс, если пользователь не авторизован
    // или пользователь не является создателем курса
    if (this.user == null || !this.courseDAO.userIsCourseCreator(this.user.getId(), courseId))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    model.addAttribute("courseId", courseId);
    model.addAttribute("course", this.courseDAO.getCourse(courseId));
    return "pages/updateCourse";
  }

  /**
   * Обновляет данные курса
   * @param courseId          id курса
   * @param course            объект курса
   * @param bindingResult     объект, содержащий ошибки при заполнении полей формы
   */
  @PatchMapping("/course/{courseId}/edit")
  public String processUpdateCourse(@PathVariable("courseId") int courseId,
                                    @ModelAttribute("course") @Valid Course course,
                                    BindingResult bindingResult) {
    // Отображение ошибок при заполнении полей формы
    if (bindingResult.hasErrors())
      return "pages/updateCourse";

    course.setId(courseId);
    this.courseDAO.updateCourse(course);

    return "redirect:/langmaster/course/{courseId}";
  }

  /**
   * Обрабатывает данные из формы и создаёт курс
   * @param course              объект курса
   * @param bindingResult       объект, содержащий ошибки при заполнении полей формы
   */
  @PostMapping("/course/new")
  public String processCourseCreation(@ModelAttribute("course") @Valid Course course,
                                      BindingResult bindingResult) {
    if (bindingResult.hasErrors())
      return "pages/courseCreation";

    // Создание изображения
    MultipartFile courseImage = course.getImage();

    if (courseImage != null || !courseImage.isEmpty()) {
      // Генерация пути и названия картинки
      String imageName = this.servletContext.getRealPath("/")
        + "resources/images/"
        + courseImage.getOriginalFilename();

      try {
        // Загрузка картинки на сервер
        courseImage.transferTo(new File(imageName));
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    this.courseDAO.createCourse(this.user.getId(), course);
    return "redirect:/langmaster";
  }

  /**
   * Удаляет курс из БД вместе с уроками этого курса
   * @param courseId        id курса
   */
  @DeleteMapping("/course/{courseId}/delete")
  public String processCourseDelete(@PathVariable("courseId") int courseId) {
    this.courseDAO.deleteCourse(courseId);
    return "redirect:/langmaster";
  }

  /**
   * Добавляет курс в список проходимых на данные момент
   * @param courseId      id пользователя
   */
  @PostMapping("/course/{courseId}/assign")
  public String processAssignCourse(@PathVariable("courseId") int courseId) {
    // Запрос не должен пройти, если пользователь не авторизован
    if (this.user == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    this.courseDAO.assignCourse(this.user.getId(), courseId);
    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу создания урока для курса при GET запросе /langmaster/course/{courseId}/create
   * @param courseId        id курса, для которого создаётся урок
   * @param lesson          объект создаваемого урока
   * @param model           объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/{courseId}/create")
  public String displayLessonCreationPage(@PathVariable("courseId") int courseId,
                                          @ModelAttribute("lesson") Lesson lesson,
                                          Model model) {
    // Страницу нельзя отобразить, если пользователь не авторизован
    if (this.user == null)
      return "redirect:/langmaster/login";

    model.addAttribute("courseId", courseId);
    return "pages/lessonCreation";
  }

  /**
   * Создаёт урок для курса
   * @param courseId          id курса, для которого создаётся урок
   * @param lesson            создаваемый урок
   * @param bindingResult     объект, содержащий ошибки при заполнении полей формы
   */
  @PostMapping("/course/{courseId}/create")
  public String processLessonCreation(@PathVariable("courseId") int courseId,
                                      @ModelAttribute("lesson") @Valid Lesson lesson,
                                      BindingResult bindingResult) {
    // Есть ошибки при заполнении полей формы
    if (bindingResult.hasErrors())
      return "pages/lessonCreation";

    this.lessonDAO.createLesson(courseId, lesson);
    return "redirect:/langmaster/course/{courseId}";
  }

  /**
   * Отображает страницу урока
   * @param courseId      id курса, которому принадлежит урок
   * @param lessonId      id отображаемого урока
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/{courseId}/lesson/{lessonId}")
  public String displayLessonPage(@PathVariable("courseId") int courseId,
                                  @PathVariable("lessonId") int lessonId,
                                  Model model) {
    // Неавторизованный пользователь не может увидеть эту страницу
    if (this.user == null)
      return "redirect:/langmaster/login";

    model.addAttribute("courseId", courseId);
    model.addAttribute("userIsCourseCreator",
      this.courseDAO.userIsCourseCreator(this.user.getId(), courseId));
    model.addAttribute("lessonsList", this.lessonDAO.getCourseLessons(courseId));
    model.addAttribute("lesson", this.lessonDAO.getLesson(lessonId));

    return "pages/lessons";
  }

  /**
   * Отображает страницу изменения урока
   * @param courseId      id курса
   * @param lessonId      id урока
   * @param model         объект для передачи данных шаблонизатору
   */
  @GetMapping("/course/{courseId}/lesson/{lessonId}/edit")
  public String displayUpdateLessonPage(@PathVariable("courseId") int courseId,
                                        @PathVariable("lessonId") int lessonId,
                                        Model model) {
    // Запрос не должен пройти, если пользователь не авторизован
    if (this.user == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    model.addAttribute("courseId", courseId);
    model.addAttribute("lesson", this.lessonDAO.getLesson(lessonId));

    return "pages/updateLesson";
  }

  /**
   * Обновляет информацию об уроке курса
   * @param courseId          id курса
   * @param lessonId          id урока
   * @param lesson            обновляемый урок
   * @param bindingResult     объект, содержащий ошибки при заполнении полей формы
   */
  @PatchMapping("/course/{courseId}/lesson/{lessonId}/edit")
  public String processUpdateLesson(@PathVariable("courseId") int courseId,
                                    @PathVariable("lessonId") int lessonId,
                                    @ModelAttribute("lesson") @Valid Lesson lesson,
                                    BindingResult bindingResult) {
    // Отображение ошибок при заполнении полей формы
    if (bindingResult.hasErrors())
      return "pages/updateLesson";

    lesson.setId(lessonId);
    this.lessonDAO.updateLesson(lesson);

    return "redirect:/langmaster/course/{courseId}/lesson/{lessonId}";
  }

  /**
   * Удаляет урок из указанного курса
   * @param courseId      id курса
   * @param lessonId      id урока
   */
  @DeleteMapping("/course/{courseId}/lesson/{lessonId}/delete")
  public String processDeleteLesson(@PathVariable("courseId") int courseId,
                                    @PathVariable("lessonId") int lessonId) {
    this.lessonDAO.deleteLesson(lessonId);
    return "redirect:/langmaster/course/{courseId}";
  }
}
