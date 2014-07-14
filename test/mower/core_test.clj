(ns mower.core-test
  (:require [clojure.test :refer :all]
            [mower.core :refer :all]))

(defmethod mixin :sample-inc [[k v]] {k (inc v)})

(defmethod mixin :sample-expand [[k v]] {k v
                                         (str "-moz-" (name k)) v})

(deftest vendorize-test
  (testing "vendorizing third part"
    (is (= (vendorize "sample" "moz") "-moz-sample"))
    (is (= (vendorize "sample" :webkit) "-webkit-sample")))
  (testing "vendorizing official"
    (is (= (vendorize "sample" :official) "sample"))))

(deftest vendor-value-test
  (testing "vendorizing value"
    (is (= (vendor-value "flex") #{"-webkit-flex" "-ms-flex" "-moz-flex" "-o-flex" "flex"}))
    (is (= (vendor-value "flex" :only #{:webkit :official}) #{"-webkit-flex" "flex"}))))

(deftest mixin-display-flex-test
  (testing "doesn't change for regular displays"
    (is (= (mixin [:display "block"]) {:display "block"}))
    (is (= (mixin [:display "none"]) {:display "none"})))
  (testing "converting flex"
    (is (= (mixin [:display "flex"]) {:display (sorted-set "-webkit-box" "-moz-box" "-webkit-flex" "-ms-flexbox" "box" "flex")})))
  (testing "converting inline-flex"
    (is (= (mixin [:display "inline-flex"]) {:display (sorted-set "-webkit-inline-box" "-moz-inline-box" "-webkit-inline-flex" "-ms-inline-flexbox" "inline-box" "inline-flex")}))))

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
