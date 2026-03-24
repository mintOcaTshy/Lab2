package org.example;

import java.io.*;
import java.util.*;

class Human implements Serializable {
    private static final long serialVersionUID = 1L;
    String firstName;
    String lastName;

    public Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

class Author extends Human {
    public Author(String firstName, String lastName) {
        super(firstName, lastName);
    }
}

class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    String title;
    int year;
    List<Author> authors;

    public Book(String title, int year, List<Author> authors) {
        this.title = title;
        this.year = year;
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "'" + title + "' (" + year + "), автори: " + authors;
    }
}

class BookReader extends Human {
    int readerId;
    List<Book> borrowedBooks = new ArrayList<>();

    public BookReader(String firstName, String lastName, int readerId) {
        super(firstName, lastName);
        this.readerId = readerId;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    @Override
    public String toString() {
        return "Читач #" + readerId + ": " + super.toString() + ", книги: " + borrowedBooks;
    }
}

// головний контейнер
class Library implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    List<Book> allBooks = new ArrayList<>();
    List<BookReader> allReaders = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Бібліотека: " + name + "\nКниг: " + allBooks.size() + "\nЧитачів: " + allReaders.size();
    }
}


public class LibraryApp {
    public static void main(String[] args) {
        Library myLibrary = new Library("Центральна міська бібліотека");

        Author author = new Author("Рюноске", "Акутаґава");
        Book harryPotter = new Book("Ворота Расьомон", 1997, Arrays.asList(author));

        BookReader reader = new BookReader("Наталя", "Петренко", 101);
        reader.borrowBook(harryPotter);

        myLibrary.allBooks.add(harryPotter);
        myLibrary.allReaders.add(reader);

        System.out.println("/// До збереження ///");
        System.out.println(myLibrary);
        System.out.println(reader);

        String filename = "library_data.ser";

// cеріалізація
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(myLibrary);
            System.out.println("\nБібліотеку збережено у файл!");
        } catch (IOException e) {
            System.out.println("Ой! Помилка при збереженні: " + e.getMessage());
        }

// десеріалізація
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Library loadedLibrary = (Library) in.readObject();
            System.out.println("\n/// Після відновлення з файлу ///");
            System.out.println(loadedLibrary);
            System.out.println(loadedLibrary.allReaders.get(0));
        } catch (Exception e) {
            System.out.println("Ой! Помилка при читанні: " + e.getMessage());
        }
    }
}