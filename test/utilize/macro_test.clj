(ns utilize.macro-test
  (:use clojure.test utilize.macro midje.sweet))

;; necessary because deftest does weird shit with namespaces, resolution, and
;; macroexpansion, so this can't be inside there
(let [strip-extraneous-do (fn [form]
                            (->> form
                                 (iterate second)
                                 (drop-while (comp #{`do} first))
                                 first))
      expansion (macroexpand '(anon-macro [name num]
                                `(inc ~(symbol (str name num)))
                                test 1))]

  (deftest test-macro-toys
    (is (= `(inc ~'test1)
           (strip-extraneous-do expansion)))
    (is (= "123abc"
           (with-out-str
             (macro-do [x] `(print '~x)
               123
               abc)))))) 

(def ^{:dynamic true} *value* 1)

(deftest test-alter-var
  (let [get-value (fn [] *value*)]
    (is (= 1 *value*))
    (is (= 4 (with-altered-var [*value* + 3]
               (get-value))))))

(fact 
  (macro-for [n [1 2]] 
    `[~(* 2 n)]) 
  => 
  `(do [2] [4]))

;; TODO: Alex - Dec 19, 2011 - get this working right

;(defmacro a [s] `(defn ~(symbol s) [] ~s))
;
;(fact "gives a seq of each expansion until fully expanded"
;  (macroexpand-scan `(a "f")) => `[ (clojure.core/defn f [] "f") 
;                                     (def f (clojure.core/fn ([] "f"))) ] )