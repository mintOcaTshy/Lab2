package org.example;

import java.io.*;
import java.util.*;

class Human implements Externalizable {
    String firstName;
    String lastName;

    public Human() {}

    public Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(firstName);
        out.writeObject(lastName);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        firstName = (String) in.readObject();
        lastName = (String) in.readObject();
    }

    @Override
    public String toString() { return firstName + " " + lastName; }
}

class Author extends Human {
    public Author() { super(); }
    public Author(String firstName, String lastName) { super(firstName, lastName); }
}

class Book implements Externalizable {
    String title;
    int year;
    List<Author> authors = new ArrayList<>();

    public Book() {}

    public Book(String title, int year, List<Author> authors) {
        this.title = title;
        this.year = year;
        this.authors = authors;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(title);
        out.writeInt(year);
        out.writeObject(authors);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        title = (String) in.readObject();
        year = in.readInt();
        authors = (List<Author>) in.readObject();
    }
}

class BookReader extends Human {
    int readerId;
    List<Book> borrowedBooks = new ArrayList<>();

    public BookReader() { super(); }

    public BookReader(String firstName, String lastName, int readerId) {
        super(firstName, lastName);
        this.readerId = readerId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(readerId);
        out.writeObject(borrowedBooks);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        readerId = in.readInt();
        borrowedBooks = (List<Book>) in.readObject();
    }
}

class Library implements Externalizable {
    String name;
    List<Book> allBooks = new ArrayList<>();
    List<BookReader> allReaders = new ArrayList<>();

    public Library() {}

    public Library(String name) { this.name = name; }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(allBooks);
        out.writeObject(allReaders);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        allBooks = (List<Book>) in.readObject();
        allReaders = (List<BookReader>) in.readObject();
    }

    @Override
    public String toString() {
        return "Бібліотека: " + name + " (Книг: " + allBooks.size() + ", Читачів: " + allReaders.size() + ")";
    }
}

public class LibraryApp {
    public static void main(String[] args) {
        Library myLibrary = new Library("Токійський книжковий сад");

        Author murakami = new Author("Харукі", "Муракамі");
        Book book = new Book("Норвезький ліс", 1987, Collections.singletonList(murakami));
        myLibrary.allBooks.add(book);

        BookReader reader = new BookReader("Олексій", "Іванов", 101);
        reader.borrowedBooks.add(book);
        myLibrary.allReaders.add(reader);

        String filename = "library_v3.ext";

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(myLibrary);
            System.out.println("/// Дані успішно експортовано ///");
        } catch (IOException e) { e.printStackTrace(); }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Library loaded = (Library) in.readObject();
            System.out.println("\n/// Дані відновлено ///");
            System.out.println(loaded);
            System.out.println("Читач " + loaded.allReaders.get(0).firstName + " читає: " + loaded.allReaders.get(0).borrowedBooks.get(0).title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}