package realworld.testUsers

case class TestUpdate(id:Long,
                      username: Option[String],
                      email: Option[String],
                      password: Option[String],
                      bio:Option[String] ,
                      image:Option[String])
