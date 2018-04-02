(ns bitchcoin-in-website.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [reagent.core :as r]

   [bitchcoin-in-website.subs]))

(defn playlist-view []
  (let [songs (subscribe [:db.songs/all])
        paused? (subscribe [:db/paused?])]
    (fn []
      [:ul.playlist
       (doall (map #(with-meta
                [:li
                 {:class [(when (:active? %1) "active")
                          (when (and (:active? %1) @paused?) "paused")]
                  }
                 [:button
                  {:on-click (fn []
                               (dispatch [:ui.playlist/click (:id %1)]))}]]
                {:key %2})
             @songs
             (range)))])))
