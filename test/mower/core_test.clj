(ns mower.core-test
  (:require [clojure.test :refer :all]
            [mower.core :refer :all]))

(defmethod mixin :sample-inc [[k v]] {k (inc v)})

(defmethod mixin :sample-expand [[k v]] {k v
                                         (str "-moz-" (name k)) v})

(deftest apply-mixins-test
  (let [sample {:sample "value"}]
    (testing "applying no filters"
      (is (= (apply-mixins sample) sample)))
    (testing "applying a simple filter"
      (is (= (apply-mixins {:sample-inc 2}) {:sample-inc 3})))
    (testing "expanding selectors"
      (is (= (apply-mixins {:sample-expand "value"}) {:sample-expand "value"
                                                      "-moz-sample-expand" "value"})))))

(deftest process-test
  (let [css [:body {:border-radius 3}]]
    (testing "without any registered processors"
      (is (= (process css) css)))
    (testing "applying the processor"
      (is (= (process [:body {:sample-inc 4}])
             [:body {:sample-inc 5}])))))
