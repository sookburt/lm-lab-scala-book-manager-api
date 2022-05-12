package controllers

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import repositories.BookRepository
import models.Book
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json._
import scala.collection.mutable

class BooksControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

  val mockDataService: BookRepository = mock[BookRepository]
  var sampleBook: Option[Book] = Option(Book(2,
    "Fantastic Mr. Fox",
    "Roald Dahl",
    "Brilliant",
    "Childs fiction"
  ))

  "BooksController GET allBooks" should {

    "return 200 OK for all books request" in {

      // Here we utilise Mockito for stubbing the request to getAllBooks
      when(mockDataService.getAllBooks).thenReturn(mutable.Set[Book]())
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val allBooks = controller.getAll().apply(FakeRequest(GET, "/books"))

      status(allBooks) mustBe OK
      contentType(allBooks) mustBe Some("application/json")
    }

    "return empty JSON array of books for all books request" in {

      // Here we utilise Mockito for stubbing the request to getAllBooks
      when(mockDataService.getAllBooks) thenReturn mutable.Set[Book]()
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val allBooks = controller.getAll().apply(FakeRequest(GET, "/books"))

      status(allBooks) mustBe OK
      contentType(allBooks) mustBe Some("application/json")
      contentAsString(allBooks) mustEqual "[]"
    }
  }

  "BooksController GET bookById" should {

    "return 200 OK for single book request" in {

      // Here we utilise Mockito for stubbing the request to getBook
      when(mockDataService.getBook(1)) thenReturn sampleBook
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val book = controller.getBook(1).apply(FakeRequest(GET, "/books/1"))

      status(book) mustBe OK
      contentType(book) mustBe Some("application/json")
    }

    "return 404 NOT_FOUND if book id doesn't exist" in {

      // Here we utilise Mockito for stubbing the request to getBook
      when(mockDataService.getBook(45)) thenReturn(None)
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val book = controller.getBook(45).apply(FakeRequest(GET, "/books/45"))

      status(book) mustBe NOT_FOUND
    }
  }

  "BooksController POST addBook" should {

    "return 200 OK for adding a single book" in {

      // Here we utilise Mockito for stubbing the request to addBook
      when(mockDataService.addBook(any())) thenReturn sampleBook
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val book = controller.addBook().apply(
        FakeRequest(POST, "/books").withJsonBody(Json.toJson(sampleBook)))

      status(book) mustBe CREATED
      contentType(book) mustBe Some("application/json")
    }

    "return 409 CONFLICT if trying to add a book with the same id" in {

      // Here we utilise Mockito for stubbing the request to addBook
      when(mockDataService.addBook(any())) thenReturn(None)
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val book = controller.addBook().apply(
        FakeRequest(POST, "/books").withJsonBody(Json.toJson(sampleBook)))

      status(book) mustBe CONFLICT
    }

  }

  "BooksController DELETE deleteBook" should {

    "return 204 if successfully deleted" in {

      when(mockDataService.deleteBook(1)) thenReturn true
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val result = controller.deleteBook(1).apply(FakeRequest(DELETE, "/books/1"))

      status(result) mustBe NO_CONTENT
    }

    "return 404 if book doesn't exist" in {

      when(mockDataService.deleteBook(1)) thenReturn false
      val controller = new BooksController(stubControllerComponents(), mockDataService)
      val result = controller.deleteBook(1).apply(FakeRequest(DELETE, "/books/1"))

      status(result) mustBe NOT_FOUND
    }
  }

}