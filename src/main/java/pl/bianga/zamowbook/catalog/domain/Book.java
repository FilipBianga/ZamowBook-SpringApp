package pl.bianga.zamowbook.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import pl.bianga.zamowbook.jpa.BaseEntity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import java.math.BigDecimal;

@ToString(exclude = "authors")
@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Book extends BaseEntity {
    @Column(unique = true)
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    private Long available;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();

    public Book(String title, Integer year, BigDecimal price, Long available) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors() {
        Book self = this;
        authors.forEach(author -> author.getBooks().remove(self));
        authors.clear();
    }
}