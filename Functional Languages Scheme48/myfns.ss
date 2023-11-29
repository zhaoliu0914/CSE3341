; Get expression to evaluate, call mainEval
(define (myinterpreter prog)
    (mainEval (cadr prog) '())
)

; switchboard, finds what kind of expression x is
(define (mainEval x param)
    ;(display "in mainEval x=")
    ;(display x)
    ;(newline)

    (cond
        ((integer? x) x)
        ((symbol? x) x)
        ((equal? 'planIf (car x)) (evalPlanIf (cdr x) param))
        ((equal? 'planAdd (car x)) (evalPlanAdd (cdr x) param))
        ((equal? 'planMul (car x)) (evalPlanMul (cdr x) param))
        ((equal? 'planSub (car x)) (evalPlanSub (cdr x) param))
        ((equal? 'planLet (car x)) (evalPlanLet (cdr x) param))
        (#t x)
    )
)

(define (evalPlanIf x param)
    ;(display "in evalPlanIf x=")
    ;(display x)
    ;(newline)

    (if (> (mainEval (car x) param) 0)
        (mainEval (cadr x) param)
        (mainEval (caddr x) param)
    )
)

(define (evalPlanAdd x param)
    ;(display "in evalPlanAdd x=")
    ;(display x)
    ;(newline)
    ;(display "in evalPlanAdd param=")
    ;(display param)
    ;(newline)

    (if (integer? (car x))
        (+ (mainEval (car x) param) (mainEval (cadr x) param))

        (+
            (if (equal? (car x) (car param))
                ;(cadar param)
                (cadr param)
                (evalPlanAdd x (cdr param))
            )
            (if (equal? (cadr x) (car param))
                ;(cadar param)
                (cadr param)
                (evalPlanAdd x (cdr param))
            )
        )
    )
)

(define (evalPlanMul x param)
    (* (mainEval (car x) param) (mainEval (cadr x) param))
)

(define (evalPlanSub x param)
    (- (mainEval (car x) param) (mainEval (cadr x) param))
)

(define (evalPlanLet x param)
    ;(display "in evalPlanLet x=")
    ;(display x)
    ;(newline)
    ;(display "in evalPlanLet param=")
    ;(display param)
    ;(newline)

    (mainEval
        (caddr x)
        (if (null? param)
            (list (car x) (mainEval (cadr x) param))
            (list param (list (car x) (mainEval (cdr x) param)))
        )
    )
)