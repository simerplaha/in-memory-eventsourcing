//package unit
//
//import akka.typed.Inbox.SyncInbox
//import akka.typed._
//import common.events.database.dao.EventDAO
//import com.base.Response
//import com.commerce.aggregate.user.UserCommands.{CreateUser, UpdateUserUsername}
//import com.commerce.aggregate.user.{UserState, UserAggregate, UserCommand, UserCommands}
//
//class UserSpec extends TypedSpec {
//
//  override def beforeAll() {
//    println("Deleting all events")
//    EventDAO.deleteAllEvents
//    println("Events deleted")
//  }
//
//  override def afterAll() {
//    println("Deleting all events after")
//    EventDAO.deleteAllEvents
//    println("Events deleted after")
//  }
//
//  object `Command CreateUser` {
//
//    val ctx: EffectfulActorContext[UserCommand] = new EffectfulActorContext[UserCommand]("userContext", UserAggregate.props("me@gmail.com"), system)
//
//    def `should create a new user` = {
//      val responseReply: SyncInbox[Response] = Inbox.sync[Response]("ResponseBehavior")
//      val command = CreateUser("me@gmail.com", "me@gmail.com", "password", "userId")(responseReply.ref)
//      ctx.run(command)
//      assert(responseReply.hasMessages)
//      val responseList = responseReply.receiveAll()
//      responseList.size should be(1)
//      val reply = responseList.head
//      println("*************" + responseList)
//      val userState = reply.asInstanceOf[UserState]
//
//      assert(userState.email == command.email)
//      assert(userState.password == command.password)
//      assert(userState.phoneNumber == 0)
//      assert(userState.username == command.email)
//
//      assert(userState.address.isEmpty)
//      assert(userState.challengeAnswer.isEmpty)
//      assert(userState.challengeQuestion.isEmpty)
//      assert(userState.firstName.isEmpty)
//      assert(userState.lastName.isEmpty)
//
//      assert(!userState.active)
//      assert(!userState.deleted)
//      assert(!userState.loggedIn)
//    }
//
////    def `should reply with an ErrorMessage with text 'already exists'` = {
////
////      val responseReply2: SyncInbox[Response] = Inbox.sync[Response]("ResponseBehavior")
////      val command2 = CreateUser("me@gmail.com", "me@gmail.com", "password")(responseReply2.ref)
////      ctx.run(command2)
////      assert(responseReply2.hasMessages)
////      val messages2 = responseReply2.receiveAll()
////      assert(messages2.size == 1)
////      val response2 = messages2.head.asInstanceOf[ErrorMessages]
////      assert(response2.messages.head.message.contains("already exists"))
////    }
//
//    def `should update the username` = {
//      val responseReply3: SyncInbox[Response] = Inbox.sync[Response]("ResponseBehavior")
//      val command3 = UpdateUserUsername("me@gmail.com", "me", "userId")(responseReply3.ref)
//      ctx.run(command3)
//      assert(responseReply3.hasMessages)
//      val responseList3 = responseReply3.receiveAll()
//      responseList3.size should be(1)
//      val reply3 = responseList3.head
//      val userState3 = reply3.asInstanceOf[UserState]
//
//      assert(userState3.username == command3.username)
//    }
//
//    //    def `should forward the shopkeeper command to shopkeeper domain` = {
//    //      val responseReply3: SyncInbox[Response] = Inbox.sync[Response]("ResponseBehavior")
//    //      val command3 = CreateShopkeeper("shopkeeper1", "shopkeeper1")(responseReply3.ref)
//    //      ctx.run(command3)
//    //      val shopkeeperInbox = ctx.getInbox("shopkeeper1")
//    //      val shopkeeperInboxMessages = shopkeeperInbox.receiveAll()
//    //      shopkeeperInboxMessages.size should be(1)
//    //      val createShopkeeperCommandInShopkeeperAggregate = shopkeeperInboxMessages.head.asInstanceOf[CreateShopkeeper]
//    //      assertResult(createShopkeeperCommandInShopkeeperAggregate.shopkeeperName)(command3.shopkeeperName)
//    //    }
//
//
//    //    def `creating the same user again should return an Error message with 'Already exists in the message'` = {
//    //
//    //    }
//    //
//    //    def `should update the username` = {
//    //
//    //    }
//
//  }
//
//  //  val left = 2
//  //  val right = 1
//  //
//  //  assert(left != right)
//  //
//  //  val ctx: EffectfulActorContext[UserCommand] = new EffectfulActorContext("userContext", User.props("User1"), system)
//  //
//  //  val responseReply: SyncInbox[Response] = Inbox.sync[Response]("ResponseBehavior")
//  //
//  //  //  ctx.run(DeleteShopkeeper("Shopkeeper1")(responseReply.ref))
//  //  //  val shopkeeperInbox = ctx.getInbox("Shopkeeper1")
//  //  //  println("shopkeeperInbox:" + shopkeeperInbox.receiveAll())
//  //  //
//  //  //  val all = responseReply.receiveAll()
//  //  //  println("Messages in response box: " + all)
//  //
//  //
//  //  ctx.run(CreateUser("me@gmail.com", "me@gmail.com", "password")(responseReply.ref))
//  //  println(responseReply.hasMessages)
//  //  println(responseReply.receiveAll())
//
//
//  //  "Something" should "produce NoSuchElementException when head is invoked" in {
//  //    intercept[NoSuchElementException] {
//  //      Set.empty.head
//  //    }
//  //  }
//  //
//  //  val a = 4
//  //  val b = 2
//  //  assertResult(2) {
//  //    a - b
//  //  }
//
//}
