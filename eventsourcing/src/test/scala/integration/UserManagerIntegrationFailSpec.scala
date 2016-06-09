package integration

import akka.typed.{ActorRef, TypedSpec}
import com.base.{ErrorInternal, ErrorMessages}
import com.commerce.aggregate.user.EmailIsInvalidError
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.aggregate.user.manager._
import com.commerce.database.EventDAO
import com.commerce.enums.{AggregateIdPrefix, Language}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.duration._
import scalaz.NonEmptyList
import scalaz.Scalaz._

class UserManagerIntegrationFailSpec extends TypedSpec with BeforeAndAfterEach with LazyLogging {

  val expectedDuration = 600000.seconds


  override def beforeEach() {
    logger.info("Deleting all events")
    EventDAO.deleteAllEvents
    logger.info("Events deleted")
  }

  override def afterEach() {
    logger.info("Deleting all events after")
    //    EventDAO.deleteAllEvents
    logger.info("Events deleted after")
  }

  val userId = "userId"


  object `Test user manager fail scenarios` {

    def `Test when sending double CreateUser command. The second command does not get processed.`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            def finder = (state: UserManagerState) => state.some
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager
            val createUserCommand =
              CreateUser(
                destinationId = "",
                email = "simer.j@gmail.com".some,
                firstName = none,
                lastName = none,
                password = "SimerPlaha".some,
                phoneNumber = none,
                address = none,
                challengeQuestion = none,
                challengeAnswer = none,
                creator = "",
                language = Language.English
              )(self)

            startWith.withKeepTraces(true) {

              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)

              domain ! UserManagerChildCommand(createUserCommand)(self)
              domain ! UserManagerChildCommand(createUserCommand)(self)

              val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
              domain ! userCommandWrapper
              (domain, 3)
            }.expectMessages(2 seconds) {
              case (messages, (domain, expectedCount)) ⇒
//                val expectedErrorMessages = ErrorMessages(NonEmptyList(ErrorDBO(ErrorUserCreationIsInProgress(createUserCommand.email.get), Language.English)))
//                assert(messages.contains(expectedErrorMessages))
                val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
                domain ! userCommandWrapper
                (domain, 1)
            }.expectMessage(2 seconds) {
              case (message, domain) ⇒
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                val expectedNextId = 1
                assertResult(expectedNextId)(state.nextId)
                assertResult(AggregateIdPrefix.user + expectedNextId)(state.nextIdWithPrefix)
                assertResult(0)(state.inProgressCreateUserCommands.size)
                assertResult(List("U0"))(state.userIds)
                assertResult(UserSetting())(state.setting)
            }
        }
      })

    //
    //    def `Test Creating a user with invalid email.`: Unit =
    //      sync(runTest("test") {
    //        StepWise[Any] {
    //          (ctx, startWith) ⇒
    //            val self = ctx.self
    //            val UM = AggregateIdPrefix.userManager
    //            val createUserCommand =
    //              CreateUser(
    //                destinationId = "",
    //                email = "invalidEmail".some,
    //                firstName = none,
    //                lastName = none,
    //                password = "SimerPlaha".some,
    //                phoneNumber = none,
    //                address = none,
    //                challengeQuestion = none,
    //                challengeAnswer = none,
    //                creator = "",
    //                language = Language.English
    //              )(self)
    //
    //            startWith.withKeepTraces(true) {
    //
    //              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)
    //
    //              domain ! UserManagerChildCommand(createUserCommand)(self)
    //              (domain, 1)
    //            }.expectMultipleMessages(111111 seconds) {
    //              case (messages, (domain, expectedCount)) ⇒
    //                println(messages)
    //                val expectedErrorMessages = ErrorMessages(NonEmptyList(ErrorDBO(EmailIsInvalidError(createUserCommand.email.get), Language.English)))
    //                assert(messages.contains(expectedErrorMessages))
    //            }
    //        }
    //      })


    def `Test Creating a user with a large name.`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager
            val createUserCommand =
              CreateUser(
                destinationId = "",
                email = "simer.j@gmail.com".some,
                firstName = "".some,
                lastName = none,
                password = "SimerPlaha".some,
                phoneNumber = none,
                address = none,
                challengeQuestion = none,
                challengeAnswer = none,
                creator = "",
                language = Language.English
              )(self)

            startWith.withKeepTraces(true) {

              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)

              domain ! UserManagerChildCommand(createUserCommand)(self)
              (domain, 1)
            }.expectMessages(2 seconds) {
              case (messages, (domain, expectedCount)) ⇒
                println(messages)
              //                val expectedErrorMessages = ErrorMessages(NonEmptyList(ErrorDBO(EmailIsInvalidError(createUserCommand.email.get), Language.English)))
              //                assert(messages.contains(expectedErrorMessages))
            }
        }
      })


  }

}
