//package unit
//
//import akka.typed.{EffectfulActorContext, Inbox, TypedSpec}
//import common.events.database.dao.EventDAO
//import com.base.Response
//import com.commerce.aggregate.shop.ShopCommands.CreateShop
//import com.commerce.aggregate.shop.{ShopAggregate, ShopCommand, ShopState}
//
//class ShopSpec extends TypedSpec {
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
//  object `Should create a shop` {
//
//    val ctx = new EffectfulActorContext[ShopCommand]("shop1", ShopAggregate.props("shop1"), system)
//
//    def `should create a shop on create command` = {
//      val responseReply = Inbox.sync[Response]("ResponseBehavior")
//      val command = CreateShop("shop1", "shop1", "userId")(responseReply.ref)
//      ctx.run(command)
//
//      val responseMessages = responseReply.receiveAll()
//      responseMessages.size should be(1)
//      assert(responseMessages.head.isInstanceOf[ShopState])
//      val state = responseMessages.head.asInstanceOf[ShopState]
//      assertResult(command.name)(state.name)
//    }
//
//    //    def `should send Product command to product inbox` = {
//    //      val responseReply = Inbox.sync[Response]("ResponseBehavior")
//    //      val command = CreateProduct("product1", "product1", "shop1")(responseReply.ref)
//    //      ctx.run(command)
//    //      val productCommandInbox = ctx.getInbox[ProductCommand]("product1")
//    //
//    //      val productInboxMessages = productCommandInbox.receiveAll()
//    //      productInboxMessages.size should be(1)
//    //      assertResult(productInboxMessages.head)(command)
//    //    }
//  }
//
//}
