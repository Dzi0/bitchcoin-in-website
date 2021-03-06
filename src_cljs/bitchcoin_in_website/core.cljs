(ns bitchcoin-in-website.core
  (:require
   [re-frame.core :as re-frame :refer [dispatch subscribe console]]
   [reagent.core :as reagent]

   [jayq.core :refer [$ css html outer-height attr]]
   ;; [enfocus.core :as ef]
   ;; [enfocus.events :as events]
   ;; [enfocus.effects :as effects]

   [bitchcoin-in-website.events]
   [bitchcoin-in-website.views :as views])

  ;; (:require-macros [enfocus.macros :as em])
  )

(def $playlist ($ :.playlist))

(defn- $target [t]
  ($ t))

;; (em/defaction  []
;;   ["article" "a"]
;;   (events/listen :click #(console :log "a-clicked")))

(defn- set-article-padding []
  (let [height (outer-height ($ :.playlist) true)]
    (.. (js/$ "article")
        (css "padding-bottom" (str height "px")))))

(defn mount-root []
  (reagent/render [views/playlist-view]
                  (.getElementById js/document "app-root"))
  (set-article-padding))


(defn ^:export init []
  (.log js/console "init")
  (.addEventListener
   js/window

   "load"
   (fn []
     (let [click-task (atom
                       (fn []
                         (console :log :default-click-task)))]
         (re-frame/dispatch-sync [:initialize click-task])
      (mount-root)
      (.. (js/$ "article a")                           
          (click (fn [e]
                   (let [target (.-target e)
                         href (.. ($target target)
                                  (attr "href"))]
                     (console :log :a-clicked href)
                     (re-frame/dispatch-sync [:ui.text/click href])
                     (js/setTimeout #(@click-task) 200)
                     (.stopPropagation e)
                     false))))

      (.. (js/$ ".playlist button")
          (click (fn [e]
                   (js/setTimeout #(@click-task) 200)
                   #_(.stopPropagation e)
                   true)))

      (.. (js/$ "#splash h1")
          (click (fn [e]
                   (console :log :splash-clicked)
                   (re-frame/dispatch-sync [:ui.splash/click])
                   (js/setTimeout #(@click-task) 200)
                   (.stopPropagation e)
                   false)))

      (.. (js/$ "#pause-button")
          (click (fn [e]
                   (let [target (.-target e)]
                     (console :log :pause-button-clicked)

                     (re-frame/dispatch-sync [:ui.pause/click])

                     (js/setTimeout #(@click-task) 200)                     

                     (.stopPropagation e)
                     true))))

      (.. (js/$ "#audio-player")
          (on "ended"
              (fn [e]
                (console :log :audio-ended)
                (re-frame/dispatch-sync [:player/play-next true]))))))))

