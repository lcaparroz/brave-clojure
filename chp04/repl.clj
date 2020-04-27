(ns chp04.repl
  (:gen-class))

;; abstraction

(seq '(1 2 3))
(seq [1 2 3])
(seq #{1 2 3})
(seq {:name "Bill Compton" :occupation "Dead mopey guy"})

(into {} (seq {:a 1 :b 2 :c 3}))

;; Map
;; The map function works on several data structures, since it works on
;; abstractions of sequences rather than on specific data structures

(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(map titleize ["Hamsters" "Ragnarok"])
(map titleize '("Empathy" "Decorating"))
(map titleize #{"Elbows" "Soap Carving"})
(map #(titleize (second %)) {:uncomfortable-thing "Winking"})

(map inc [1 2 3])

;; map operates over multiple collections if the mapping function
;; has an arity equal to the number of collections

(map str ["a" "b" "c"] ["A" "B" "C"])
(list (str "a" "A") (str "b" "B") (str "c" "C"))

(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumption)

;; map also works with collections of functions

(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))

(stats [3 4 10])
(stats [80 1 44 13 6])

;; map using keywords as a function to extract values

(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)

;; reduce

(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})

(assoc (assoc {} :max (inc 30))
       :min (inc 10))

(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1 :critter 3.9})

;; take, drop, take-while, drop-while

(take 3 [1 2 3 4 5 6 7 8 9 10])
(drop 3 [1 2 3 4 5 6 7 8 9 10])

(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :critter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(take-while #(< (:month %) 3) food-journal)
(drop-while #(< (:month %) 3) food-journal)
(take-while #(< (:month %) 4)
            (drop-while #(< (:month %) 2) food-journal))

;; filter, some

(filter #(< (:human %) 5) food-journal)
(filter #(< (:month %) 3) food-journal)

(some #(> (:critter %) 5) food-journal)
(some #(> (:critter %) 3) food-journal)

(some #(and (> (:critter %) 3) %) food-journal)

;; sort, sort-by

(sort [3 1 2])

(sort-by count ["aaa" "c" "bb"])

;; sort-by accepts a comparator argument, which can be a function
;; that changes how elements can be ordered

;; ascending order (sort-by without comparator function)
(sort-by #(:human %) food-journal)
;; desceindig order (sort-by with comparator function, greatest comes first)
(sort-by #(:human %) > food-journal)

;; concat

(concat [1 2] [3 4])

;; Lazy Seqs

(def vampire-database
  {0 {:makes-blood-puns? false :has-pulse? true  :name "McFishwich"}
   1 {:makes-blood-puns? false :has-pulse? true  :name "McMackson"}
   2 {:makes-blood-puns? true  :has-pulse? false :name "Damon Salvatore"}
   3 {:makes-blood-puns? true  :has-pulse? true  :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

(time (vampire-related-details 0))
(time (def mapped-details (map vampire-related-details (range 0 1000000))))
(time (first mapped-details))
(time (identify-vampire (range 0 1000000)))

;; Infinite Sequences

(concat (take 8 (repeat "na")) ["Batman!"])
(take 3 (repeatedly (fn [] (rand-int 10))))

(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))
(take 10 (even-numbers 10))
(take 10 (even-numbers -18))

(cons 0 '(2 4 6))

;; The Collection Abstraction

(empty? [])
(empty? ["no!"])

;; into
;; adds one collection to another

;; converting a mapped seq back to the original collection type

(map identity {:sunlight-reaction "Glitter!"})
(into {} (map identity {:sunlight-reaction "Glitter!"}))

(map identity [:garlic :sesame-oil :fried-eggs])
(into [] identity [:garlic :sesame-oil :fried-eggs])

(map identity [:garlic-clove :garlic-clove])
(into #{} (map identity [:garlic-clove :garlic-clove]))

;; adding elements to non-empty collections

(into {:favorite-emotion "gloomy"} [[:sunlight-reaction "Glitter!"]])
(into ["cherry"] '("pine" "spruce"))

(into {:favorite-animal "kitty"} {:least-favorite-smell "dog"
                                  :relationship-with-teenager "creepy"})

;; conj
;; adds a scalar to a collection

(conj [0] [1])
(into [0] [1])

(conj [0] 1)

(conj [0] 1 2 3 4)
(conj {:time "midnight"} [:place "ye olde cemetarium"])

;; defining conj in terms of into

(defn my-conj
  [target & additions]
  (into target additions))

(my-conj [0] 1 2 3 4)

;; Function functions

;; apply
;; it explodes seqable data, so it can be passed as multiple
;; arguments to a function

(max 0 1 2)
(max [0 1 2])
(apply max [0 1 2])

;; defining into in terms of conj, by using apply
;; notice that the function doesn't expect a rest parameter
;; (param & rest)

(defn my-into
  [target additions]
  (apply conj target additions))

(my-into [0] [1 2 3 4])

;; partial
;; returns a function with "partially" applied arguments, it is, if you have a
;; function which expects two arguments and pass it to partial along with an
;; argument, partial will return a new function which expects just a second
;; argument

(def add10 (partial + 10))
(add10 3)
(add10 5)

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))
(add-missing-elements "unobtainium" "adamantium")

;; defining partial

(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))
(add20 3)

(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))

(warn "Reg light ahead")
(lousy-logger :warn "Red light ahead")

;; complement

(defn identify-humans
  [social-security-numbers]
  (filter #(not (vampire? %))
          (map vampire-related-details social-security-numbers)))
(map :name (identify-humans (range 0 4)))

(def not-vampire? (complement vampire?))
(defn identify-humans-compl
  [social-security-numbers]
  (filter not-vampire?
          (map vampire-related-details social-security-numbers)))
(map :name (identify-humans-compl (range 0 4)))

;; implementing complement

(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

(def my-pos? (complement neg?))
(my-pos? 1)
(my-pos? -1)
