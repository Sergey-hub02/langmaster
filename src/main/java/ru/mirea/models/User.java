package ru.mirea.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Класс, представляющий пользователя приложения
 */
public class User {
  private int id;

  @NotEmpty(message = "Обязательное поле!")
  @Length(min = 4, message = "Имя пользователя должно содержать хотя бы 4 символа!")
  private String name;

  @NotEmpty(message = "Обязательное поле!")
  @Email(message = "Неправильный формат электронной почты!")
  private String email;

  @NotEmpty(message = "Обязательное поле!")
  @Length(min = 4, message = "Пароль должен содержать хотя бы 4 символа!")
  private String password;

  private String registrationDate;

  /**
   * Пустой конструктор
   */
  public User() {}

  /**
   * Возвращает id пользователя
   */
  public int getId() {
    return this.id;
  }

  /**
   * Устанавливает новое значение для id пользователя
   * @param id        новый id пользователя
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Возвращает имя пользователя
   */
  public String getName() {
    return this.name;
  }

  /**
   * Устанавливает новое значение для имени пользователя
   * @param name      новое имя пользователя
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Возвращает адрес электронной почты пользователя
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * Устанавливает новое значение для адреса электронной почты пользователя
   * @param email         новый адрес электронной почты пользователя
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Возвращает пароль пользователя
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Устанавливает новое значения для пароля пользователя
   * @param password        новый пароль пользователя
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Возвращает дату регистрации аккаунта
   */
  public String getRegistrationDate() {
    return this.registrationDate;
  }

  /**
   * Устанавливает новую дату регистрации аккаунта
   * @param registrationDate      новая дата регистрации аккаунта
   */
  public void setRegistrationDate(String registrationDate) {
    this.registrationDate = registrationDate;
  }

  /**
   * Выводит значение полей объекта
   */
  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", registrationDate='" + registrationDate + '\'' +
      '}';
  }
}
