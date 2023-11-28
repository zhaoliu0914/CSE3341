;Checking whether x is in the list
;returns #t if x is in list
;returns #f if x is not in list

(define (is_member x list)
    (cond
        ((null? list) #f)
        ((equal? x (car list)) #t)
        (#t (is_member x (cdr list)))
    )
)