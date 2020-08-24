(ns koans.16-refs
  (:require [koan-engine.core :refer :all]))

(def the-world (ref "hello"))
(def bizarro-world (ref {}))

(meditations
  "In the beginning, there was a word"
  (= "hello" (deref the-world))

  "You can get the word more succinctly, but it's the same"
  (= "hello" @the-world)

  "You can be the change you wish to see in the world."
  (= "better" (do
          (dosync (ref-set the-world "better"))
          @the-world))

  "Alter where you need not replace"
  (= "better!!!" (let [exclamator (fn [x] (str x "!"))]
          (dosync                                         ;always alter in a transaction (dosync)
           (alter the-world exclamator)
           (alter the-world exclamator)
           (alter the-world exclamator))
          @the-world))

  "Don't forget to do your work in a transaction!"
  (= 0 (do 
         (dosync (alter the-world (fn[x] 0))) ;set it to 0, for any input
           @the-world))

  "Functions passed to alter may depend on the data in the ref"
  (= 20 (do
          ;(dosync (alter the-world (fn [x] 20))))) ; also works
          ;(dosync (alter the-world (fn [x] (+ x 20))))))
          (dosync (alter the-world (fn[x] 20))); no need to pass the-world?? in prev
          @the-world))
          

  "Two worlds are better than one"
  (= ["Real Jerry" "Bizarro Jerry"]
       (do
         (dosync
          (ref-set the-world {})
          (alter the-world assoc :jerry "Real Jerry")
          (alter bizarro-world assoc :jerry "Bizarro Jerry")
          [(@the-world :jerry) (@bizarro-world :jerry)]))))

;(println bizarro-world)              ;hash .... {:jerry Bizarro Jerry}
;(println @bizarro-world)             ;{:jerry Bizarro Jerry}
;(println (deref bizarro-world))      ;{:jerry Bizarro Jerry}
