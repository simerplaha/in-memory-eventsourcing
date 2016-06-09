package integration

import akka.typed.{ActorRef, TypedSpec}
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.aggregate.user.UserState
import com.commerce.aggregate.user.manager._
import com.commerce.database.EventDAO
import com.commerce.enums.{Language, AggregateIdPrefix}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.duration._
import scalaz.Scalaz._

class UserManagerIntegrationSpec extends TypedSpec with BeforeAndAfterEach with LazyLogging {

  val expectedDuration = 600000.seconds


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

    def `Test user manager's initial state`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager
            startWith.withKeepTraces(true) {
              def finder = (state: UserManagerState) => state.some
              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)
              val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
              domain ! userCommandWrapper
              (domain, 1)
            }.expectMessage(1 seconds) {
              case (message, domain) ⇒
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                assertResult(0)(state.nextId)
                assertResult(AggregateIdPrefix.user + 0)(state.nextIdWithPrefix)
                assertResult(0)(state.inProgressCreateUserCommands.size)
                assertResult(0)(state.userIds.size)
                assertResult(UserSetting())(state.setting)
            }
        }
      })


    def `Test AlertUserManagerOfUserCreated updates the state`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager

            val createUserCommand =
              CreateUser(
                destinationId = "U10",
                email = "email@gmail.com".some,
                firstName = None,
                lastName = None,
                password = "password".some,
                phoneNumber = None,
                address = None,
                challengeQuestion = None,
                challengeAnswer = None,
                creator = "",
                language = Language.English
              )(self)

            startWith.withKeepTraces(true) {


              def finder = (state: UserManagerState) => state.some
              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)
              val alertUserManagerOfUserCreatedCommand = AlertUserManagerOfUserCreated(UM, createUserCommand, "test")
              domain ! alertUserManagerOfUserCreatedCommand

              val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
              domain ! userCommandWrapper
              (domain, 1)
            }.expectMessage(1 seconds) {
              case (message, domain) ⇒
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                val expectedNextId = createUserCommand.userIdWithoutPrefix + 1
                assertResult(expectedNextId)(state.nextId)
                assertResult(AggregateIdPrefix.user + expectedNextId)(state.nextIdWithPrefix)
                assertResult(0)(state.inProgressCreateUserCommands.size)
                assertResult(List(createUserCommand.userId))(state.userIds)
                assertResult(UserSetting())(state.setting)
            }
        }
      })


    def `Test AlertUserManagerOfUserDeleted updates the state`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager

            val createUserCommand =
              CreateUser(
                destinationId = "U1",
                email = "email@gmail.com2".some,
                firstName = None,
                lastName = None,
                password = "password".some,
                phoneNumber = None,
                address = None,
                challengeQuestion = none,
                challengeAnswer = None,
                creator = "",
                language = Language.English
              )(self)

            startWith.withKeepTraces(true) {

              def finder = (state: UserManagerState) => state.some
              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)

              val alertUserManagerOfUserCreatedCommand = AlertUserManagerOfUserCreated(UM, createUserCommand, "test")
              domain ! alertUserManagerOfUserCreatedCommand

              val alertUserManagerOfUserDeletedCommand = AlertUserManagerOfUserDeleted(UM, createUserCommand.userId, "test")
              domain ! alertUserManagerOfUserDeletedCommand

              val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
              domain ! userCommandWrapper
              (domain, 1)
            }.expectMessage(1 seconds) {
              case (message, domain) ⇒
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                val expectedNextId = createUserCommand.userIdWithoutPrefix + 1
                assertResult(expectedNextId)(state.nextId)
                assertResult(AggregateIdPrefix.user + expectedNextId)(state.nextIdWithPrefix)
                assertResult(0)(state.inProgressCreateUserCommands.size)
                assert(state.userIds.isEmpty)
                assertResult(UserSetting())(state.setting)
            }
        }
      })


    def `Test when sending double CreateUser command the inProgressCreateUserCommand field get updated.`: Unit =
      sync(runTest("test") {
        StepWise[Any] {
          (ctx, startWith) ⇒
            def finder = (state: UserManagerState) => state.some
            val self = ctx.self
            val UM = AggregateIdPrefix.userManager
            startWith.withKeepTraces(true) {

              val createUserCommand =
                CreateUser(
                  destinationId = "",
                  email = "email@gmail.com".some,
                  firstName = None,
                  lastName = None,
                  password = "password".some,
                  phoneNumber = None,
                  address = None,
                  challengeQuestion = None,
                  challengeAnswer = None,
                  creator = "",
                  language = Language.English
                )(self)


              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, UM)

              domain ! UserManagerChildCommand(createUserCommand)(self)

              val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
              domain ! userCommandWrapper
              domain
            }.expectMessage(2 seconds) {
              //check that inProgressCreateUserCommand is added
              case (message, domain) ⇒
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                assertResult(1)(state.nextId)
                assertResult(AggregateIdPrefix.user + 1)(state.nextIdWithPrefix)
                assertResult(1)(state.inProgressCreateUserCommands.size)
                assertResult(0)(state.userIds.size)
                assertResult(UserSetting())(state.setting)
                domain
            }.expectMessage(2 seconds) {
              case (message, domain) ⇒
                // ignore the UserState because we not testing User here
                assert(message.isInstanceOf[UserState])
                val userCommandWrapper = UserManagerFinder(UM, finder, "test")(self)
                domain ! userCommandWrapper
            }.expectMessage(2 seconds) {
              case (message, domain) ⇒
                //check that the inProgressCreateUserCommand is removed after the user is created. i.e. the above set
                //also check that the UserManager state is updated properly
                assert(message.isInstanceOf[Option[UserManagerState]])
                val state = message.asInstanceOf[Option[UserManagerState]].get
                assertResult(1)(state.nextId)
                assertResult(AggregateIdPrefix.user + 1)(state.nextIdWithPrefix)
                assertResult(0)(state.inProgressCreateUserCommands.size)
                assertResult(1)(state.userIds.size)
                assert(state.userIds.contains("U0"))
                assertResult(UserSetting())(state.setting)
                domain
            }
        }
      })
  }

}
