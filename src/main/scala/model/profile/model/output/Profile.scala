package model.profile.model.output

final case class Profile(username: String,
                         bio:Option[String],
                         image:Option[String],
                         following:Boolean=false)
