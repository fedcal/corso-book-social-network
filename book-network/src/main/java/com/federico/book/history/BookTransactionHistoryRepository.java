package com.federico.book.history;

import com.federico.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query("""
            SELECT history 
            FROM BookTransactionHistory history
            WHERE history.user.id = :idUser
                """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Long idUser);

    @Query("""
            SELECT history 
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :idUser
                """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Long idUser);

    @Query("""
            SELECT (COUNT(*)>0) as isBorrowed 
            FROM BookTransactionHistory history 
            WHERE history.user.id = :idUser 
            AND history.book.id = :bookId 
            AND history.returnApproved = false
                """)
    boolean isAlreadyBorrowedByUser(Long bookId, Long idUser);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history 
            WHERE history.user.id = :idUser 
            AND history.book.id = :bookId 
            AND history.returned = false
            AND history.returnApproved = false
                """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Long bookId, Long idUser);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history 
            WHERE history.book.owner.id = :idUser 
            AND history.book.id = :bookId 
            AND history.returned = false
            AND history.returnApproved = false
                """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Long bookId, Long id);
}
