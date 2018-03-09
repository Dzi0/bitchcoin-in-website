(ns bitchcoin-in-website.core
  (:require
   [re-frame.core :as re-frame :refer [dispatch subscribe console]]
   [reagent.core :as reagent]

   ;; [enfocus.core :as ef]
   ;; [enfocus.events :as events]
   ;; [enfocus.effects :as effects]

   [bitchcoin-in-website.events]
   [bitchcoin-in-website.views :as views])

  ;; (:require-macros [enfocus.macros :as em])
  )


;; (em/defaction  []
;;   ["article" "a"]
;;   (events/listen :click #(console :log "a-clicked")))

(defn- set-article-padding []
  (let [height (.. (js/$ ".playlist")
                   (outerHeight true))]
    (.. (js/$ "article")
        (css "padding-bottom" (str height "px")))))

(defn mount-root []
  (reagent/render [views/playlist-view]
                  (.getElementById js/document "app-root"))
  (set-article-padding))


(defn ^:export init []
  (.log js/console "init")
  (.addEventListener js/window
                     "load"
                     (fn []
                       (re-frame/dispatch-sync [:initialize])
                       (mount-root)
                       (.. (js/$ "article a")                           
                           (click (fn [e]
                                    (let [target (.-target e)
                                          href (.. (js/$ target)
                                                   (attr "href"))]
                                      (console :log :a-clicked href)
                                      (re-frame/dispatch-sync [:ui.text/click href])
                                      (.stopPropagation e)
                                      false))))

                       (.. (js/$ "#audio-player")
                           (on "ended"
                               (fn [e]
                                 (console :log :audio-ended)
                                 (re-frame/dispatch-sync [:player/play-next])))))))

