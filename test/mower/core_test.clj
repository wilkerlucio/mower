(ns mower.core-test
  (:require [clojure.test :refer :all]
            [mower.core :refer :all]))

(deftest process-test
  (let [css [:body {:border-radius 3}]
        processors {:border-radius (fn [_ v] {:border-radius-changed (+ v 2)})}]
    (testing "without any registered processors"
      (is (= (process css {}) css)))
    (testing "applying the processor"
      (is (= (process css processors)
             [:body {:border-radius-changed 5}])))))

(deftest apply-mixins-test
  (let [sample {:sample "value"}]
    (testing "applying no filters"
      (is (= (apply-mixins sample {}) sample)))
    (testing "applying a simple filter"
      (let [mixin (fn [k v] {k (str v "-extended")})
            mixins {:sample mixin}]
        (is (= (apply-mixins sample mixins) {:sample "value-extended"}))))
    (testing "expanding selectors"
      (let [mixin (fn [k v] {k v (str "-moz-" (name k)) v})
            mixins {:sample mixin}]
        (is (= (apply-mixins sample mixins) {:sample "value" "-moz-sample" "value"}))))))
