(ns mower.core
  (:require [clojure.walk :as walk]))

(defn apply-mixins [definitions mixins]
  (let [map-ident (fn [k v] {k v})
        apply-one (fn [[k v]]
                    (let [mixin (get mixins k map-ident)]
                      (mixin k v)))]
    (into {} (mapcat apply-one definitions))))

(defn process [css processors]
  (walk/postwalk
    (fn [v] (if (map? v) (apply-mixins v processors)
                         v))
    css))
