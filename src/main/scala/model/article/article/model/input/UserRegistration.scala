package model.article.article.model.input

import model.users.model.save.SaveUser

final case class UserRegistration(username: String, password: String, email: String,token:String) {
  def create(): SaveUser = {
    SaveUser(
      id=0,
      username=username,
      email=email,
      password=password,
      token=token,
      bio = None,
      image = None,
      following =false)
  }}
