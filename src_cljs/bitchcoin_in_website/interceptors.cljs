(ns bitchcoin-in-website.interceptors
  (:require
   [clojure.spec.alpha :as s]
   [bitchcoin-in-website.db :as db]
   [goog.string :as gstring]
   [goog.string.format]
   [re-frame.core :refer [reg-event-db reg-event-fx path trim-v after debug reg-fx console dispatch]]))

;;;
;;;
;;; INTERCEPTORS
;;;
;;;

;;;
;;; spec
;;;
(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-data a-spec db)) {}))))

;;;
;;; interceptor for usual event handlers
;;;
(def check-spec-interceptor (after (partial check-and-throw :bitchcoin-in-website.db/db)))

;;;
;;; interceptor for FX event handlers
;;;
(def check-spec-interceptor-fx (after
                                (fn [db]
                                  ;; (js/console.log :debug :interceptor-fx db)
                                  (check-and-throw :bitchcoin-in-website.db/db db))))


;;;
;;; INTERCEPTORS DEFINITION
;;;
(defn interceptors [& rest]
  [check-spec-interceptor
   #_(when ^boolean js/goog.DEBUG debug)
   trim-v])

;;; interceptors for fx events
;; (def interceptors-fx [
;;                       check-spec-interceptor-fx
;;                       trim-v])

(defn interceptors-fx [& rest]
  (let [{:keys [spec]} (apply hash-map rest)
        default-interceptors [trim-v]]
    (if spec
      (conj default-interceptors check-spec-interceptor-fx)
      default-interceptors)))
