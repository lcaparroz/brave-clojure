(ns chp05.repl
  (:gen-class))

;; Pure Functions

;; Referential Transparency
;; (return the same results given the same arguments)

(+ 1 2)

(defn wisdom
  [words]
  (str words ", Daniel-san"))

(wisdom "Always bathe on Fridays")

;; Not referentially transparent
;; (return different results given the same arguments)

(defn year-end-evaluation
  []
  (if (> (rand) 0.5)
    "You get a raise!"
    "Better luck next year!"))

(year-end-evaluation)

;; reading from a file makes a function not referentially transparent

;; (this one is referentially transparent)
(defn analysis
  [text]
  (str "Character count: " (count text)))

;; (this on is not)
(defn analyze-file
  [filename]
  (analysis (slurp filename)))

(analyze-file "../chp04/fwpd/suspects.csv")

;; Immutable Data Structures

(def great-baby-name "Rosanthony")
great-baby-name

(let [great-baby-name "Bloodthunder"]
  great-baby-name)

great-baby-name

;; summing without loop variables
;; (recursion approach)

(defn sum
  ([vals] (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (sum (rest vals) (+ (first vals) accumulating-total)))))

(sum [39 5 1])
(sum [39 5 1] 0)
(sum [5 1] 39)
(sum [1] 44)
(sum [] 45)

;; using recur, you can make a recursive function tail-recursive
;; (stack frame optimization)

(defn sum-recur
  ([vals] (sum-recur vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (recur (rest vals) (+ (first vals) accumulating-total)))))

(sum [39 5 1])

;; "Mutating" strings with function composition
(require '[clojure.string :as s])

(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))

(clean "My boa constrictor is so sassy lol!  ")

;; comp

((comp inc *) 2 3)
(inc (* 2 3))

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))

(c-int character)
(c-str character)
(c-dex character)

((fn [c] (:strength (:attributes c))) character)

(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))
(spell-slots character)

(def spell-slots-comp (comp int inc #(/ % 2) c-int))
(spell-slots-comp character)

;; Implementation of comp with arity 2
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

((two-comp inc *) 2 3)

;; Implementation of comp with arity n
;; (extra exercise 
(defn n-comp
  [f1 & fns]
  (if (empty? fns)
    f1
    (recur (fn [& args]
             (f1 (apply (first fns) args)))
           (rest fns))))

((n-comp inc *) 2 3)
((n-comp int inc #(/ % 2) c-int) character)

;; comp ideas

(defn dig-hashmap
  [hashmap & kwords]
  ((apply comp (reverse kwords)) hashmap))

(dig-hashmap {:a {:b {:c 3}} :b {:a 2}} :a :b :c)
