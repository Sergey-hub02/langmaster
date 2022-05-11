package ru.mirea.models;

import javax.validation.constraints.NotEmpty;

/**
 * Представляет урок курса
 */
public class Lesson {
  private int id;

  @NotEmpty(message = "Обязательное поле!")
  private String title;

  @NotEmpty(message = "Обязательное поле!")
  private String content;

  /**
   * Пустой конструктор
   */
  public Lesson() {}

  /**
   * Возвращает id урока
   */
  public int getId() {
    return this.id;
  }

  /**
   * Устанавливает значение id для урока
   * @param id        id урока
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Возвращает название урока
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Устанавливает значение названия урока
   * @param title       название урока
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Возвращает содержание урока
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Устанавливает значение для содержания урока
   * @param content         содержание урока
   */
  public void setContent(String content) {
    this.content = content;
  }
}
