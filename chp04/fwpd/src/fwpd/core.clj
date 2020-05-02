(ns fwpd.core)
(def filename "suspects.csv")

(def vamp-keys [:name :glitter-index])

(defn str->int
  [str]
  (Integer. str))

(def conversions {:name identity
                  :glitter-index str->int})

(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))

(defn parse
  "Converts a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\n")))

(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

;; Exercises

(def csv-records (mapify (parse (slurp filename))))

;; Exercise 1

(defn glitter-filter-name
  [minimum-glitter records]
  (map :name (glitter-filter minimum-glitter records)))

;; Exercise 2

(defn append
  [entry records]
  (conj records entry))

;; Exercise 3

(defn validate
  [entry validations]
  (not (some #(not ((% validations) (% entry)))
             (keys validations))))

(def validations {:name #(not (clojure.string/blank? %))
                  :glitter-index #(and % (>= % 0))})
;; valid entry
(validate {:name "Vampire" :glitter-index 0} validations)

;; invalid entries
(validate {:name "Vampire" :glitter-index -1} validations)
(validate {:name "Vampire"} validations)
(validate {:glitter-index 3} validations)
(validate {:name "    " :glitter-index 4} validations)
(validate {:name nil :glitter-index 5} validations)

;; append and validate together

(defn safe-append
  [entry records]
  (or (and (validate entry validations)
           (append entry records))
      records))

(= (append {:name "Vampire" :glitter-index 2} csv-records)
   (safe-append {:name "Vampire" :glitter-index 2} csv-records))

(= (append {:glitter-index 3} csv-records)
   (safe-append {:glitter-index 3} csv-records))

;; Exercise 4

(defn convert-to-csv
  [records]
  (clojure.string/join
   "\n"
   (map #(clojure.string/join
          ","
          [(:name %) (:glitter-index %)])
        records)))

(= (convert-to-csv csv-records)
   (clojure.string/trim (nslurp filename)))
