(ns mower.core
  (:require [clojure.walk :as walk]))

(defmulti mixin
  (fn [[k _]] (keyword k))
  :default ::default)

(defmethod mixin ::default [[k v]] {k v})

(defn apply-mixins [definitions]
  (into {} (mapcat mixin definitions)))

(defn process [css]
  (walk/postwalk
    #(if (map? %) (apply-mixins %) %)
    css))
