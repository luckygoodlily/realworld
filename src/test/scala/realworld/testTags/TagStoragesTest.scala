package realworld.testTags

import database.storage.JdbcTagStorage
import model.tag.model.save.TagV
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import realworld.DBServiceTest
import realworld.allTestValue.Tags

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
class TagStoragesTest extends DBServiceTest{
  "insert model.article tag" should "should return seq model.article Tags" in new Context {
    newdbRun(_.run(tagStorage.insertAndGet(Seq(TagV.create("test")))))
      .futureValue(Timeout(60 seconds)) should be(Vector(TagV(1, "test")))

//      awaitFuture(
//      newdbRun { runner =>
//        //Await.result(
//        runner.run(for {
//          tags <- tagStorage.insertAndGet(Seq(TagV.create("test")))
//        } yield {
//          println("1Tags~" + tags)
//          tags should be(Vector(TagV(tags.head.id, "test")))
//        })
//      })
  }

  "find model.article tag!!!" should "should return correct tag" in new Context {
//    newdbRun(_.run{
//      tagStorage.insertAndGet(Tags.tag).flatMap(_=>
//        tagStorage.findTagByNames(Seq("test1")))
//    }).futureValue(Timeout(60 seconds)) should be(Seq(TagV(1,"test1")))

    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          insert <- tagStorage.insertAndGet(Tags.tag)
          tags <- tagStorage.findTagByNames(Seq("test1"))
        } yield {
          println("2inserttags~" + insert)
          println("2Tags~" + tags)
          tags should be(Seq(TagV(1,"test1")))
        })
      })
  }

  "getTags~~~" should "return all Tags" in new Context {
//    newdbRun(_.run{
//      tagStorage.insertAndGet(Seq(TagV.create("test1"), TagV.create("test2"))).flatMap(_=>
//        tagStorage.getTags())
//    }).futureValue(Timeout(60 seconds)) should be(Seq("test1", "test2"))

    awaitFuture(
      newdbRun { runner =>
        runner.run(for {
          _ <- tagStorage.insertAndGet(Seq(TagV.create("test1"), TagV.create("test2")))
          all <- tagStorage.getTags()
        } yield {
          println("3all.length~~" + all.length)
          println("3all name" + all)
          all.length should be(Tags.tag.length)
          all should be(Seq("test1", "test2"))
        })
      })
  }

  trait Context{
    val tagStorage = new JdbcTagStorage()
    //val insertTags = Seq(TagV(1,"test1"),TagV(2,"test2"))
    //val tag =Seq(TagV.create("test1"),TagV.create("test2"))
  }

}
