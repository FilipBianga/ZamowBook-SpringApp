package pl.bianga.zamowbook.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bianga.zamowbook.catalog.application.port.AuthorsUseCase;
import pl.bianga.zamowbook.catalog.domain.Author;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/authors")
class AuthorsController {
    private final AuthorsUseCase authors;

    @GetMapping
    public List<Author> findAll() {
        return authors.findAll();
    }


}
