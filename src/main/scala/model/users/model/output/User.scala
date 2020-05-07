package model.users.model.output

//ok
final case class User(username: String = "",
                      email: String = "",
                      token: String = "",
                      // var token: String= "",
                      bio: Option[String] = None,
                      image: Option[String] = None)
