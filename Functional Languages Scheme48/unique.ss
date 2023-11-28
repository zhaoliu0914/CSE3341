;Create a function called unique with formal parameter x
;Assuming x is a sorted list of numbers
;return a sorted list with all elements of x but duplicates removed

(define (unique list)
    (cond 
        ((null? list) list)
        ((null? (cdr list)) list)
        (#t (if (equal? (car list) (cadr list))
                (unique (cdr list))
                (cons (car list) (unique (cdr list)))
            )
        )
    )
    
)