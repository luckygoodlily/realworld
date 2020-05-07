package model.users.model.input

import model.users.model.save.SaveUser

//ok
final case class UpdateUser(username: Option[String] = None,
                            email: Option[String] = None,
                            password: Option[String] = None,
                            bio: Option[String] = None,
                            image: Option[String] = None
                            //token: Option[String] = None
                           ) {
  def merge(user: SaveUser): SaveUser = {
    var token :String=""
    if (username.isDefined) token=username.get+".token.here"
    else token=user.token
    SaveUser(
      id = user.id,
      username = username.getOrElse(user.username),
      email = email.getOrElse(user.email),
      password = password.getOrElse(user.password),
      bio = bio.orElse(user.bio),
      image = image.orElse(user.image),
      token = token,
      following = user.following
    )
  }
}
