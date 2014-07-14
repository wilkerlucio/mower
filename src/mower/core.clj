(ns mower.core
  (:require [clojure.walk :as walk]
            [clojure.set :as set]))

(defn vendorize [v type]
  (if (= type :official)
    v
    (str "-" (name type) "-" v)))

(defn vendor-value [value & {:keys [only]}]
  (let [all-vendors #{:webkit :ms :moz :o :official}
        included (if only (set/intersection all-vendors only) all-vendors)]
    (into #{} (map (partial vendorize value) included))))

(defmulti mixin
  (fn [[k _]] (keyword k))
  :default ::default)

(defmethod mixin ::default [[k v]] {k v})

(defmethod mixin :display [[k v]]
  (if (#{"flex" "inline-flex"} v)
    (let [box (if (= v "flex") (into #{"-ms-flexbox"} (vendor-value "box" :only #{:moz :webkit :official}))
                               (into #{"-ms-inline-flexbox"} (vendor-value "inline-box" :only #{:moz :webkit :official})))
          flex (vendor-value v :only #{:webkit :official})]
      {k (into (sorted-set) (set/union box flex))})
    {k v}))

(defn apply-mixins [definitions]
  (into {} (mapcat mixin definitions)))

(defn process [css]
  (walk/postwalk
    #(if (map? %) (apply-mixins %) %)
    css))
