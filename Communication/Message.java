package Communication;

import java.io.Serializable;

class Message {
  static String register() {
    return "register";
  }

  static String login() {
    return "login";
  }

  static String query() {
    return "query";
  }

  static String list() {
    return "list";
  }

  static String logout() {
    return "logout";
  }

  static String notifyLogin() {
    return "notifyLogin";
  }

  static String notifyLogout() {
    return "notifyLogout";
  }

  static String like() {
    return "like";
  }
}