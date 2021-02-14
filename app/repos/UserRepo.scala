package repos

import models.User

import javax.inject.Singleton

@Singleton
class UserRepo {

  private def users = List(
    User(11, "marko", "mm@gmail.com", "12345"),
    User(22, "darko", "dd@gmail.com", "12345"),
    User(33, "petar", "pp@gmail.com", "12345"),
    User(44, "milan", "mm@gmail.com", "12345")
  )

  def allUsers(): List[User] = {
    users
  }

  def getUser(id: Int): Option[User] = {
    users.find(_.id == id)
  }

  def getUsers(ids: Seq[Int]): List[User] = {
    users.filter(u => ids.contains(u.id))
  }

}
