;Create a function called maxlist with formal parameter L.
;Assuming L contains numbers:
;Return the largest value in a non-empty list L

(define (maxlist list)
    (cond
        ((null? (cdr list)) (car list))
        (#t
            (if (> (car list) (maxlist (cdr list)))
                (car list)
                (maxlist (cdr list))
            )
        )
    )
)