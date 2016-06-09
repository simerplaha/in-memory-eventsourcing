//package integration
//
//import akka.typed._
//import com.typesafe.scalalogging.LazyLogging
//import com.commerce.aggregate.user.UserCommands._
//import com.commerce.aggregate.user.manager._
//import com.commerce.aggregate.user.{UserCommand, UserState}
//import org.scalatest.BeforeAndAfterEach
//
//import scala.concurrent.duration._
//
//class UserManagerSearchSpec extends TypedSpec with BeforeAndAfterEach with LazyLogging {
//
//  val expectedDuration = 600000.seconds
//
//
//  override def beforeEach() {
//    logger.info("Deleting all events")
//    //    EventDAO.deleteAllEvents
//    logger.info("Events deleted")
//  }
//
//  override def afterEach() {
//    logger.info("Deleting all events after")
//    //        EventDAO.deleteAllEvents
//    logger.info("Events deleted after")
//  }
//
//  val userId = "userId"
//
//
//  object `Test user manager` {
//
//    def getAllUserManagerChildren()(state: UserManagerState): Boolean =
//      true
//
//    def getUserWithEmail(email: String)(state: UserState): Boolean =
//      state.email == email
//
//    def `Create user`: Unit =
//      sync(runTest("test") {
//        StepWise[Any] {
//          (ctx, startWith) ⇒
//            val self = ctx.self
//            startWith.withKeepTraces(true) {
//              val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, "UM")
//              domain ! GetCreatedUsers(domain.path.name, "system", "")(self)
//              (domain, 1)
//            }.expectMultipleMessages(2 seconds) {
//              case (users: List[UserAggregatesData], (domain, previousCount)) ⇒
//                println("searchResponse:" + users)
//                users.head.aggregates foreach {
//                  user =>
//                    user ! IsUserWithEmail("", "simer.j@gmail.com1", "")(self)
//                }
//                (domain, users.head.aggregates.size)
//            }.expectMultipleMessages(20001 seconds) {
//              case (users: List[UserCommand], (domain, previousCount)) ⇒
//                println(users.exists(_.isInstanceOf[UserIsWithEmail]))
//            }
//        }
//      })
//
//  }
//
//
//}