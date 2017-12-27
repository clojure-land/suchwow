(ns such.vars
  "Common operations on vars.")

(defprotocol Rootable
  "A protocol to look at \"root\" values of Vars. The root value is
   the value before any `binding` - it's the value altered by `alter-var-root`.
   Defines `has-root-value?` and `root-value`."
  (has-root-value? [this]
    "Does this var have a root value?" )
  (root-value [this]
    "What is the value of the var, ignoring any bindings in effect?"))

(extend-type clojure.lang.Var
  Rootable
  (has-root-value? [var] (.hasRoot var))
  (root-value [var] (alter-var-root var identity)))

(defn name-as-symbol
  "Unlike symbols and keywords, the \"name\" of a var is a symbol. This function
   returns that symbol. See also [[name-as-string]].

        (var/name-as-symbol #'clojure.core/even?) => 'even?)

   Note that the symbol does not have a namespace."
  [^clojure.lang.Var var]
  (.sym var))

(defn name-as-string
  "Unlike symbols and keywords, the \"name\" of a var is a symbol. This function
   returns the string name of that symbol. See also [[name-as-symbol]].

        (var/name-as-string #'clojure.core/even?) => \"even?\")"
  [var]
  (name (name-as-symbol var)))

(defn has-macro?
  "Does the var point to a macro?"
  [v]
  (boolean (:macro (meta v))))

(defn has-function?
  "Does the var point to a function?"
  [v]
  (or (and (boolean (:arglists (meta v)))
           (not (has-macro? v)))
      (= (type (root-value v)) clojure.lang.MultiFn)))

(defn has-plain-value?
  "Does the var point to something not a macro nor a function?"
  [v]
  ((complement (some-fn has-macro? has-function?)) v))
