(ns bitchcoin-in-website.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [reagent.core :as r]

   [bitchcoin-in-website.subs]))

(defn playlist-view []
  (let [songs (subscribe [:db.songs/all])]
    (fn []
      [:ul.playlist
       (map #(with-meta
               [:li
                {:class (when (:active? %1) "active")}
                [:button
                 {:on-click (fn []
                              (dispatch [:ui.playlist/click (:id %1)]))}]]
               {:key %2})
            @songs
            (range))])))
