import route.HttpRoute
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import database.storage.{JdbcArticleStorage, JdbcCommentStorage, JdbcTagStorage, JdbcUserStorage}
import service.{ArticleService, CommentService, ProfileService, TagService, UserService}
import database.util.DataBaseConnector

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import slick.jdbc.H2Profile.api._

object Realworld extends App {
  def startApplication() {
    implicit val system: ActorSystem = ActorSystem("real-world-akka-http")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    //val token: String = "jwt.token.here"
    val db = Database.forConfig("h2mem1")
    val databaseConnector = new DataBaseConnector(db)

    //val storageRunner = new StorageRunner(databaseConnector)


    val userStorage = new JdbcUserStorage()
    val articleStorage = new JdbcArticleStorage()
    val tagStorage = new JdbcTagStorage()
    val commentStorage = new JdbcCommentStorage()

    databaseConnector.createTable.flatMap {_=>
      //val olduserStorage = new OldUserStorage()
      val userService = new UserService(databaseConnector, userStorage)
      val profileService = new ProfileService(databaseConnector, userStorage)
      val articleService = new ArticleService(databaseConnector, articleStorage, userStorage, tagStorage)
      val commentService = new CommentService(databaseConnector, commentStorage, articleStorage, userStorage)
      val tagService = new TagService(databaseConnector, tagStorage)


      val httpRoute = new HttpRoute(userService, profileService, articleService, commentService, tagService)


      val bindingFuture = Http().bindAndHandle(httpRoute.route, "localhost", 3000) //"localhost", 3000
      println(s"Server online at http://localhost:3000/\nPress RETURN to stop...")
      StdIn.readLine() // let it runUsers until user presses return
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
    }.onComplete(_ => system.terminate()) // and shutdown when done

    Await.result(system.whenTerminated, Duration.Inf)
  }

  startApplication()
}
