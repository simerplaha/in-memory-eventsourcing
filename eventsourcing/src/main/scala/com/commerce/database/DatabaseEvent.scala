package com.commerce.database

import java.sql.Timestamp

case class DatabaseEvent(persistentId: String,
                         json: String,
                         eventName: String,
                         eventType: String,
                         tags: List[String],
                         createUser: String,
                         createTime: Timestamp)
