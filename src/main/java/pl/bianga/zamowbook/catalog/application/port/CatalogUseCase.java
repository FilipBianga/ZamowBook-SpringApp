package pl.bianga.zamowbook.catalog.application.port;

import lombok.Builder;
import lombok.Value;
import pl.bianga.zamowbook.catalog.domain.Book;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {
    List<Book> findAll();

    List<Book> findByTitle(String title);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    void addBook(CreateBookCommand command);

    void removeById(Long id);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    @Value
    class CreateBookCommand {
        String title;
        String author;
        Integer year;
    }

    @Value
    @Builder
    class UpdateBookCommand {
        Long id;
        String title;
        String author;
        Integer year;

        public Book updateFields(Book book){
            if (title != null){
                book.setTitle(title);
            }
            if (author != null){
                book.setAuthor(author);
            }
            if (year != null){
                book.setYear(year);
            }
            return book;
        }
    }

    @Value
    class UpdateBookResponse {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }
}