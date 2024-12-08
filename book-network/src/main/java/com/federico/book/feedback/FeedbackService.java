package com.federico.book.feedback;

import com.federico.book.book.Book;
import com.federico.book.book.BookRepository;
import com.federico.book.exception.OperationNotPermittedException;
import com.federico.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId()).orElseThrow(()-> new EntityNotFoundException("Libro non trovato con id:: "+request.bookId()));
        User user = ((User) connectedUser.getPrincipal());

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Non puoi dare un feedback per un libro archiviato o non condivisibile");
        }
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Non puoi dare un feedback per il tuo libro");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);

        return feedbackRepository.save(feedback).getId();
    }
}
