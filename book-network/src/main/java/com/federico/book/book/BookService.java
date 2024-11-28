package com.federico.book.book;

import com.federico.book.common.PageResponse;
import com.federico.book.exception.OperationNotPermittedException;
import com.federico.book.history.BookTransactionHistory;
import com.federico.book.history.BookTransactionHistoryRepository;
import com.federico.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Long save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Long bookId) {
        return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(()-> new EntityNotFoundException("Nessun libro trovato con ID:: "+bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);

        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());

        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());

        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public Long updateShareableStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+bookId));
        User user = ((User) connectedUser.getPrincipal());

        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi aggiornare lo stato del libro");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Long updateArchivedStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+bookId));
        User user = ((User) connectedUser.getPrincipal());

        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi aggiornare lo stato del libro");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Long borrowBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+bookId));
        User user = ((User) connectedUser.getPrincipal());

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Il libro richiesto non può essere prestato");
        }

        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi richiedere il tuo stesso libro");
        }

        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed) {
            throw new OperationNotPermittedException("Il libro richiesto è già borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+bookId));
        User user = ((User) connectedUser.getPrincipal());

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Il libro richiesto non può essere prestato");
        }
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi richiedere o ritornare il tuo stesso libro");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("Non hai richiesto questo libro"));
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long approveReturnBorrowBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+bookId));
        User user = ((User) connectedUser.getPrincipal());

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Il libro richiesto non può essere prestato");
        }
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi richiedere o ritornare il tuo stesso libro");
        }

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId,user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("Il libro non è stato ancora restituito"));

        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

    }
}
