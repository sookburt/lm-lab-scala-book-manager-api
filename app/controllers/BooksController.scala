package controllers

import models.Book
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json._
import repositories.BookRepository

import javax.inject.{Inject, Singleton}

@Singleton
class BooksController @Inject()(val controllerComponents: ControllerComponents, dataRepository: BookRepository) extends BaseController {

  def getAll: Action[AnyContent] = Action {
    Ok(Json.toJson(dataRepository.getAllBooks))
  }

  def getBook(bookId: Long): Action[AnyContent] = Action {
    var bookToReturn: Book = null
    dataRepository.getBook(bookId) foreach { book =>
      bookToReturn = book
    }
    if(bookToReturn != null) {
      Ok(Json.toJson(bookToReturn))
    }
    else {
      NotFound(s"No book was found for the book id $bookId")
    }
  }

  def addBook() : Action[AnyContent] = Action {
    implicit request => {
      val requestBody = request.body
      val bookJsonObject = requestBody.asJson

      // This type of JSON un-marshalling will only work
      // if ALL fields are POSTed in the request body
      val bookItem: Option[Book] =
        bookJsonObject.flatMap(
          Json.fromJson[Book](_).asOpt
        )

      val savedBook: Option[Book] = dataRepository.addBook(bookItem.get)
      if(!savedBook.isEmpty)
        Created(Json.toJson(savedBook))
      else
        Conflict("The book already exists.")
    }
  }

  def deleteBook(bookId: Long): Action[AnyContent] = Action {
    val result:Boolean = dataRepository.deleteBook(bookId)
    if (!result) {
      NotFound("That book does not exist.")
    }
    else {
      NoContent
    }
  }
}
