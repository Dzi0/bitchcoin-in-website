(ns bitchcoin-in-website.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]

            [bitchcoin-in-website.db :as db]))


(reg-sub
 :db/playlist
 (fn [db]
   (:playlist db)))


(reg-sub
 :db.songs/all
 :<- [:db/playlist]

 (fn [playlist _]
   (sort-by :id #(compare %1 %2) (vals playlist))))

(reg-sub
 :db/paused?

 (fn [db]
   (:paused? db)))
