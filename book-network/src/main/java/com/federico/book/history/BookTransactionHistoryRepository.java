package com.federico.book.history;

import com.federico.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}
