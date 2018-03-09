(ns bitchcoin-in-website.db
  (:require [clojure.spec.alpha :as s]
            [clojure.core.reducers :as reducers]))

;;;
;;;
;;; constants
;;;
;;;
(def music-urls (atom []))

;;;
;;;
;;; SPECS
;;;
;;;

(s/def ::url string?)
(s/def ::active? boolean?)
(s/def ::id int?)
(s/def ::active-song-id (s/or :nil nil?
                              :int int?))

(s/def ::song (s/keys :req-un [::id
                               ::url
                               ::active?]))

(s/def ::playlist (s/map-of ::id ::song))

(s/def ::db (s/keys :req-un [::playlist
                             ::active-song-id]))

;;;
;;;
;;; default values
;;;
;;;

(defn- default-song [url id]
  {:url url
   :active? false
   :id id})

(defn- default-playlist []
  (into {} (map #(hash-map %2 (default-song %1 %2)) @music-urls (range))))

(defn- default-db []
  {:playlist (default-playlist)
   :active-song-id nil})


;;;
;;;
;;; utils
;;;
;;;

(defn song-by-url [db song-url]
  (first (filter #(= song-url (:url %)) (-> db :playlist vals))))

(defn add-music-url [url]
  (swap! music-urls #(conj % url)))
