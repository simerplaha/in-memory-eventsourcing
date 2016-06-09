package com.commerce.database

import java.sql.Timestamp

import scala.concurrent.Await
import scala.concurrent.duration._

import MyPostgresDriver.api._

object SchemaWrite {

  import DBConfig.slickDriver.api._

  val database = DBConfig.database


  ///WRITE SIDE
  class Events(tag: Tag) extends Table[DatabaseEvent](tag, "EVENTS") {

    def persistentId = column[String]("PERSISTENT_ID")

    def json = column[String]("EVENT_JSON")

    def eventName = column[String]("EVENT_NAME")

    def eventType = column[String]("EVENT_TYPE")

    def tags = column[List[String]]("TAGS", O.Default(Nil))

    def createUser = column[String]("CREATE_USER")

    def createTime = column[Timestamp]("CREATE_TIME")

    def idx = index("persistence_id_index", persistentId)

    def tagsIndex = index("tags_index", tags)

    def createTimeIndex = index("create_time_index", createTime)


    def * =
      (persistentId, json, eventName, eventType, tags, createUser, createTime) <>(
        (resultSet: (String, String, String, String, List[String], String, Timestamp)) =>
          DatabaseEvent(
            resultSet._1,
            resultSet._2,
            resultSet._3,
            resultSet._4,
            resultSet._5,
            resultSet._6,
            resultSet._7),
        (event: DatabaseEvent) =>
          Some((
            event.persistentId,
            event.json,
            event.eventName,
            event.eventType,
            event.tags,
            event.createUser,
            event.createTime)))
  }

  val events = TableQuery[Events]

  def createSchema() = {

    val setup = DBIO.seq(
      events.schema.create
      //      events += PersistentEvent(id, some_json, tags, time)
    )
    Await.result(database.run(setup), 10.seconds)
  }

  def main(args: Array[String]) = createSchema()

}


