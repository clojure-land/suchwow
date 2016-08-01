(ns such.shorthand
  "Explicit functions for what could be done easily - but less clearly -
   by composing clojure.core functions. Anti-shibboleths such as using
   `not-empty?` instead of `seq`."
  (:refer-clojure :exclude [any?]))


(def ^:no-doc this-var-has-no-value-and-is-used-in-testing)

(defn any?
  "`any?` provides shorthand for \"containment\" queries that otherwise
   require different functions. Behavior depends on the type of `predlike`.
   
   * A function: `true` iff `predlike` returns a *truthy* value for any value in `coll`.
     
            (any? even? [1 2 3]) => true           ; works best with boolean-valued functions
            (any? inc [1 2 3]) => true             ; a silly example to demo truthiness.
            (any? identity [nil false]) => false   ; also silly
     
   * A collection: `true` iff `predlike` contains any element of `coll`.

            (any? #{1 3} [5 4 1]) => true
            (any? [1 3] [5 4 1]) => true
        
        When `predlike` is a map, it checks key/value pairs:
        
            (any? {:a 1} {:a 1}) => true
            (any? {:a 1} {:a 2}) => false
            (any? {:a 2, :b 1} {:b 1, :c 3}) => true
     
   * A keyword: `true` iff `predlike` is a key in `coll`, which *must* be a map.
     
            (any? :a {:a 1, :b 2}) => true         ; equivalent to:
            (contains? {:a 1, :b 2} :a) => true
"
  [predlike coll]
  (boolean (cond (coll? predlike)
                 (some (set predlike) coll)

                 (keyword? predlike)
                 (contains? coll predlike)

                 :else
                 (some predlike coll))))

(defn not-empty? 
  "Returns `true` if `value` has any values, `false` otherwise. `value` *must* be a collection,
     a String, a native Java array, or something that implements the `Iterable` interface."
  [value]
  (boolean (seq value)))

(defn third 
  "Returns the third element of `coll`. Returns `nil` if there are fewer than three elements."
  [coll]
  (second (rest coll)))

(defn fourth
  "Returns the fourth element of `coll`. Returns `nil` if there are fewer than four elements."
  [coll]
  (third (rest coll)))

(defn find-first
  "Returns the first item of `coll` where `(pred item)` returns a truthy value, `nil` otherwise.
   `coll` is evaluated lazily.
   
        (find-first even? [1 2 3]) => 2
   
   You can apply `find-first` to a map, even though which
   element matches \"first\" is undefined. Note that the item passed to `pred` will
   be a key-value pair:

        (find-first #(even? (second %)) {:a 2, :b 22, :c 222}) => [:c 222]
"
  [pred coll]
  (first (filter pred coll)))

(defn without-nils 
  "A lazy sequence of non-nil values of `coll`."
  [coll]
  (keep identity coll))


(defmacro prog1
  "The `retform` is evaluated, followed by the `body`. The value of the
   form is returned, so the point of `body` should be to have side-effects.
   
       (defn pop! [k]
          (prog1 (top k)
            (alter! k clojure.core/pop)))
   
   The name is a homage to older Lisps.
"
  [retform & body]
  `(let [retval# ~retform]
     ~@body
     retval#))

