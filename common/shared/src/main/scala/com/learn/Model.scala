package com.learn

import scala.util.{Success, Try};


case class User(name: String)
case class Author(name: String)
case class Book(title: String, year: Int, author: Author)

object User {
  def parse(name: String): Try[User] = {
    Success(new User(name))
  }
}

