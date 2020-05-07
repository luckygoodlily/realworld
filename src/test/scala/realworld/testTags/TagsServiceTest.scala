package realworld.testTags


import database.storage.TagStorage
import model.tag.model.output.CollectionTags
import realworld.DBServiceTest
import realworld.allTestValue.Tags
import service.TagService

import scala.concurrent.ExecutionContext.Implicits.global
import slick.dbio.DBIO

class TagsServiceTest extends DBServiceTest{
  "Tag Service" should "return tags" in new Context {
    tagStorage.getTags answers DBIO.successful(Tags.insertTags)

    awaitFuture(
      for {
        tags <- tagService.getTags()
      } yield {
        println("tags"+ tags)
        tags should be (CollectionTags(Tags.insertTags.toSet))}
    )
  }


  trait Context {
    val tagStorage=mock[TagStorage]
    val tagService = new TagService(databaseConnector,tagStorage)
  }

}
