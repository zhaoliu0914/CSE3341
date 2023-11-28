;Make a function called union that takes as input formal parameters list1 and list2
;that returns a list containing the union of list1 and list2 (no duplicates)
;list1 and list2 themselves do not have duplicates.

(define (union list1 list2)
    (cond
        ((null? list1) list2)
        ((null? list2) list1)
        (#t
            (if (member (car list1) list2)
                (union (cdr list1) list2)
                (cons (car list1) (union (cdr list1) list2))
            )
        )
    )
)