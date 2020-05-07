package realworld

import org.mockito.scalatest.IdiomaticMockito

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import slick.dbio.DBIO
import database.util.{DataBaseConnector}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest.concurrent.ScalaFutures

abstract class DBServiceTest extends FlatSpec with Matchers
                              with BeforeAndAfter with IdiomaticMockito with ScalaFutures{
  val db = Database.forConfig("h2mem2")
  val databaseConnector = new DataBaseConnector(db)
  //val storageRunner = new StorageRunner(databaseConnector)
  //val userStorage = mock[UserStorage]

  //storage
  //todo
  def dbRun[T](dbio:DBIO[T]):T=
      Await.result(databaseConnector.run(dbio),60.seconds)

  def dropDB=
    databaseConnector.table.dropIfExists

  def createDB=
    databaseConnector.table.createIfNotExists


  def newdbRun[T](code:DataBaseConnector=>Future[T]):Future[T]={
//    for {
//      _ <-databaseConnector.run(createDB)
//      r <- code(databaseConnector)
//      _ <- databaseConnector.run(dropDB)
//    } yield r

//    databaseConnector.run(createDB).flatMap { _ =>
//      code(databaseConnector) //確保run完db
//    }.flatMap{ r=>
//      databaseConnector.run(dropDB).map(_=>r) //再跑drop
//    }

    databaseConnector.run(createDB).flatMap { _ =>
      code(databaseConnector).flatMap{ r=>
        databaseConnector.run(dropDB).map(_=>r)//再跑drop
      }
    }
  }

   //service
   def awaitFuture[T](futureResult:Future[T]):T=
     Await.result(futureResult,5.seconds)
}
