package service

import database.storage.TagStorage
import database.util.DataBaseConnector
import model.tag.model.output.CollectionTags

import scala.concurrent.{ExecutionContext, Future}

class TagService(runner: DataBaseConnector, tagStorage: TagStorage)
                (implicit executionContext: ExecutionContext) {
  def getTags(): Future[CollectionTags] =
    runner.run(
      for{
        tags <- tagStorage.getTags()
      } yield CollectionTags(tags.toSet))
}
