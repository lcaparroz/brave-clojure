(ns chp02.exercises
  (:gen-class))

;; Exercise 1

;; str
(str 1 " + " 1 " = " 2)
(str "I " "can " "concatenate " "like" " this")

;; vector
(vector 1 2 3 4)
(vector + - * /)

;; list
(list "a" "b" "c" "d")
(list + - * /)

;; hash-map
(hash-map :a 1 :b 2 :c 3 :d 4)
(hash-map :add + :subtract - :multiply * :divide /)

;; hash-set
(hash-set 1 2 2 3 3 3 4 4 4 4)
(hash-set + + - - * * / /)

;; Exercise 2

(defn add100
  [x]
  (+ x 100))

(add100 1)
(add100 100)
(add100 1000)

;; Exercise 3

(defn dec-maker
  [x]
  #(- % x))

(def dec9 (dec-maker 9))
(dec9 10)

(def dec42 (dec-maker 42))
(dec42 21)

;; Exercise 4

(defn mapset
  [f coll]
  (set (map f coll)))

(mapset inc [1 1 2 2])

;; Exercise 5

(def radial-symmetry-suffix #"-1$")

(defn matching-part-x
  [part x]
  {:name (clojure.string/replace (:name part)
                                 radial-symmetry-suffix
                                 (str "-" x))
   :size (:size part)})

(defn matching-n-parts
  [part n]
  (if (re-find radial-symmetry-suffix (:name part))
    (map #(matching-part-x part %) (range 1 (+ 1 n)))
    (list part)))

(matching-n-parts {:name "test-1" :size 3} 5)
(matching-n-parts {:name "test" :size 3} 5)

(defn matching-5-parts
  [part]
  (matching-n-parts part 5))

(matching-5-parts {:name "test-1" :size 3})

(defn symmetrize-body-parts-5
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts
                  (matching-5-parts part)))
          []
          asym-body-parts))

(def asym-body-parts [{:name "head" :size 7}
                      {:name "eye-1" :size 1}
                      {:name "nose" :size 2}
                      {:name "mouth" :size 3}])

(symmetrize-body-parts-5 asym-body-parts)

;; Exercise 6

(defn symmetrize-body-parts
  [coll n]
  (reduce (fn [final-body-parts part]
            (into final-body-parts
                  (matching-n-parts part n)))
          []
          asym-body-parts))

(symmetrize-body-parts asym-body-parts 10)
