package integration

import akka.typed.{ActorRef, Props, TypedSpec}
import com.base.Response
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.aggregate.user.{UserSearch, UserState}
import com.commerce.aggregate.user.manager._
import com.commerce.database.EventDAO
import com.commerce.enums.Language
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.BeforeAndAfterEach

import scalaz._
import Scalaz._
import scala.concurrent.duration._

class UserIntegrationSpec extends TypedSpec with BeforeAndAfterEach with LazyLogging {


  override def beforeEach() {
    logger.info("Deleting all events")
    EventDAO.deleteAllEvents
    logger.info("Events deleted")
  }

  override def afterEach() {
    logger.info("Deleting all events after")
    EventDAO.deleteAllEvents
    logger.info("Events deleted after")
  }

  val userId = "userId"


  object `Test user manager` {

    def `Create user`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            val self = ctx.self
            val createUserCommand =
              CreateUser(
                destinationId = "",
                email = "email@gmail.com".some,
                firstName = None,
                lastName = None,
                password = "$@#@#DFAS1111$#$#".some,
                phoneNumber = none,
                address = none,
                challengeQuestion = none,
                challengeAnswer = none,
                creator = "",
                language = Language.English
              )(self)
            startWith.withKeepTraces(true) {
              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, "UM")
              for (i <- 0 until 100) {
                val userCommandWrapper = UserManagerChildCommand(createUserCommand.copy(email = Some("email@gmail.com" + i))(self))(self)
                domain ! userCommandWrapper
              }

              (domain, 100)
            }.expectMessages(2 seconds) {
              case (messages, domain) ⇒
                println("SIZE:" + messages.size)
                ctx.spawnAnonymous(Props(UserSearch.findUserWithEmail("email@gmail.com90", domain._1, self)))
                (domain, 1)
            }.expectMessages(2 seconds) {
              case (messages, domain) ⇒
                println("Found user with email ********************************************")
                messages foreach println
                println("SIZE:" + messages.size)
            }
        }
      })
  }

}
