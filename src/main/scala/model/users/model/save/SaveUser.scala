package model.users.model.save

// domain model
//ok
final case class SaveUser(id:Long,
                          username: String,
                          email: String,
                          password: String,
                          bio:Option[String]=None,
                          image:Option[String]=None,
                          token: String,
                          following:Boolean
                          //favoritedAuthor:Map[String,String]=Map()
                          //slug author
                          //followUser:Set[String]=Set()
                         )
