package pl.bianga.zamowbook.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.bianga.zamowbook.catalog.application.port.CatalogUseCase;
import pl.bianga.zamowbook.catalog.db.AuthorJpaRepository;
import pl.bianga.zamowbook.catalog.domain.Author;
import pl.bianga.zamowbook.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.*;
import static pl.bianga.zamowbook.catalog.application.port.CatalogUseCase.*;

//Testy integracyjne
@SpringBootTest
@AutoConfigureTestDatabase
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) - użyłem adnotacji @Transactional bo jest trochę szybsza
@Transactional
class CatalogControllerIT {

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void getAllBooks() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());

        // then
        assertEquals(2, all.size());
    }

    @Test
    public void getBooksByAuthor() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        List<Book> all = controller.getAll(Optional.empty(), Optional.of("Bloch"));

        // then
        assertEquals(1, all.size());
        assertEquals("Effective Java", all.get(0).getTitle());
    }

    @Test
    public void getBooksByTitle() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        List<Book> all = controller.getAll(Optional.of("Java Concurrency in Practice"), Optional.empty());

        // then
        assertEquals(1, all.size());
        assertEquals("Java Concurrency in Practice", all.get(0).getTitle());
    }

    private void givenEffectiveJava() {
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
    }

    private void givenJavaConcurrencyInPractice() {
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
    }
}