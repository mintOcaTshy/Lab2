package org.example;

import java.io.*;
import java.util.*;

class Human {
    String firstName;
    String lastName;
    public Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

class Author extends Human {
    public Author(String firstName, String lastName) { super(firstName, lastName); }
}

class Book {
    String title;
    int year;
    List<Author> authors;
    public Book(String title, int year, List<Author> authors) {
        this.title = title;
        this.year = year;
        this.authors = authors;
    }
}

class BookReader extends Human {
    int readerId;
    List<Book> borrowedBooks = new ArrayList<>();
    public BookReader(String firstName, String lastName, int readerId) {
        super(firstName, lastName);
        this.readerId = readerId;
    }
}

class Library implements Serializable {
    private static final long serialVersionUID = 2L;
    String name;

    transient List<Book> allBooks = new ArrayList<>();
    transient List<BookReader> allReaders = new ArrayList<>();

    public Library(String name) { this.name = name; }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(allBooks.size());
        for (Book b : allBooks) {
            saveBookData(out, b);
        }

        out.writeInt(allReaders.size());
        for (BookReader r : allReaders) {
            out.writeObject(r.firstName);
            out.writeObject(r.lastName);
            out.writeInt(r.readerId);

            out.writeInt(r.borrowedBooks.size());
            for (Book b : r.borrowedBooks) {
                saveBookData(out, b);
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int bookCount = in.readInt();
        allBooks = new ArrayList<>();
        for (int i = 0; i < bookCount; i++) {
            allBooks.add(loadBookData(in));
        }

        int readerCount = in.readInt();
        allReaders = new ArrayList<>();
        for (int i = 0; i < readerCount; i++) {
            BookReader r = new BookReader((String)in.readObject(), (String)in.readObject(), in.readInt());
            int borrowedCount = in.readInt();
            for (int j = 0; j < borrowedCount; j++) {
                r.borrowedBooks.add(loadBookData(in));
            }
            allReaders.add(r);
        }
    }

    private void saveBookData(ObjectOutputStream out, Book b) throws IOException {
        out.writeObject(b.title);
        out.writeInt(b.year);
        out.writeInt(b.authors.size());
        for (Author a : b.authors) {
            out.writeObject(a.firstName);
            out.writeObject(a.lastName);
        }
    }

    private Book loadBookData(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String title = (String) in.readObject();
        int year = in.readInt();
        int authCount = in.readInt();
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < authCount; i++) {
            authors.add(new Author((String)in.readObject(), (String)in.readObject()));
        }
        return new Book(title, year, authors);
    }

    @Override
    public String toString() {
        return "Бібліотека (v2): " + name + ", Книг: " + allBooks.size() + ", Читачів: " + allReaders.size();
    }
}

public class LibraryApp {
    public static void main(String[] args) {
        Library myLibrary = new Library("Одеська японська бібліотека");

        Author murakami = new Author("Харукі", "Муракамі");
        Book book1 = new Book("Норвезький ліс", 1987, Collections.singletonList(murakami));
        myLibrary.allBooks.add(book1);

        BookReader reader = new BookReader("Наталя", "Петренко", 445);
        reader.borrowedBooks.add(book1);
        myLibrary.allReaders.add(reader);

        String filename = "library_custom.ser";

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(myLibrary);
            System.out.println("/// Успішно збережено у " + filename + " ///");
        } catch (IOException e) { e.printStackTrace(); }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Library loaded = (Library) in.readObject();
            System.out.println("\n/// Дані відновлено! ///");
            System.out.println(loaded);

            BookReader restoredReader = loaded.allReaders.get(0);
            System.out.println("Читач: " + restoredReader.firstName + " тримає книгу: " + restoredReader.borrowedBooks.get(0).title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}