(ns bitchcoin-in-website.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx path trim-v after debug reg-fx console dispatch]]

   [jayq.core :as jq :refer [$ css html outer-height attr]]

   [bitchcoin-in-website.db :as db]

   [bitchcoin-in-website.audio :as audio]

   [bitchcoin-in-website.interceptors :refer [interceptors
                                              interceptors-fx]]))


(reg-event-fx
 :initialize

 (interceptors-fx :spec true)

 (fn [_ [click-task-atom]]
   (console :log :initialize)

   ;; grab all songs from article
   (.each (js/$ "article a")
          (fn []
            (this-as self
              (db/add-music-url (.. (js/$ self)
                                    (attr "href"))))))

   (cond-> {:db (db/default-db click-task-atom)}
     (not js/isMobile)
     (merge {:dispatch-n [[:ui.splash/hide]
                          [:player/change-song 0 false]]}))))
     ;; TODO: if not mobile start here!
                                        ;:dispatch [:player/change-song 0]


;;;
;;; when playlist button is clicked
;;;
(reg-event-fx
 :ui.playlist/click

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [song-id]]

   (console :log :clicked song-id)

   {:db db
    :dispatch [:player/change-song song-id false]}))


;;;
;;; activate link in text
;;;
(reg-event-fx
 :ui.text/activate-link

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [song-id]]
   (let [song-url (get-in db [:playlist song-id :url])]
     (console :log :ui.text/activate-link song-url)
     (.. (js/$ (str "article a[href='" song-url "']"))
         (addClass "active")))
   {}))

;;;
;;; deactivate link in text
;;;
(reg-event-fx
 :ui.text/deactivate-link

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [song-id]]
   (let [song-url (get-in db [:playlist song-id :url])]
     (.. (js/$ (str "article a[href='" song-url "']"))
         (removeClass "active")))
   {}))

;;;
;;; click on link in text
;;;
(reg-event-fx
 :ui.text/click

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [song-url]]
   (console :log :ui.text/click song-url db)

   (let [song-id (:id (db/song-by-url db song-url))]
     {:db db
      :dispatch [:player/change-song song-id false]})))

;;;
;;;
;;; UI / PAUSE BUTTON  EVENTS
;;;
;;;

;;;
;;; click on pause button
;;;
(reg-event-fx
 :ui.pause/click

 (interceptors-fx :spec true)

 (fn [{:keys [db]}]
   (let [was-paused? (get db :paused?)]
     {:db (update db :paused? not)
      :dispatch-n (if was-paused?
                    [[:ui.pause/set-playing]
                     [:player.pause/play]]

                    [[:ui.pause/set-paused]
                     [:player.pause/pause]])})))

;;;
;;; change status to playing
;;;
(reg-event-fx
 :ui.pause/set-playing

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   (let [$pause-button ($ :#pause-button)]
     (jq/remove-class $pause-button "paused")

     {})))


;;;
;;; change status to paused
;;;
(reg-event-fx
 :ui.pause/set-paused

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   (let [$pause-button ($ :#pause-button)]
     (jq/add-class $pause-button "paused")

     {})))

;;;
;;;
;;; SPLASH SCREEN EVENTS
;;;
;;;

(reg-event-fx
 :ui.splash/click

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   {:dispatch-n [[:ui.splash/hide]
                 [:player/change-song 0 false]]}))

(reg-event-fx
 :ui.splash/hide

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   (-> ($ :#splash)
       (jq/add-class "hidden"))
   {}))


;;;
;;;
;;; HTML5  PLAYER EVENTS
;;;
;;;

;;;
;;; start playing song
;;;
(reg-event-fx
 :player/start

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [song-id force?]]
   (console :log :started song-id)

   {:db (-> db
            (update-in [:playlist song-id :active?] not))
    :dispatch-n [[:player/do-start song-id force?]
                 [:ui.text/activate-link song-id]]}))

;;;
;;; stop current
;;;
(reg-event-fx
 :player/stop

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [song-id]]

   (console :log :stopped song-id)

   {:db (-> db
            (update-in [:playlist song-id :active?] not))
    :dispatch [:ui.text/deactivate-link song-id]}))

;;;
;;; change song
;;;
(reg-event-fx
 :player/change-song

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [next-song-id force?]]
   (console :log :changing-to next-song-id)

   (let [cur-song-id (:id (first (filter #(:active? %) (vals (:playlist db)))))]
     {:db (assoc db :paused? false)
      :dispatch-n (cond-> [[:ui.pause/set-playing]]
                    cur-song-id
                    (conj [:player/stop cur-song-id])

                    true
                    (conj [:player/start next-song-id force?]))})))

;;;
;;; play next
;;;
(reg-event-fx
 :player/play-next

 (interceptors-fx :spec :false)

 (fn [{:keys [db]} [force?]]
   (let [songs (vals (:playlist db))
         cur-song-id (:id (first (filter #(:active? %) songs)))
         last-song-id (:id (last (sort-by :id #(compare %1 %2) songs)))
         next-song-id (cond
                        (nil? cur-song-id)
                        0 ; play first if no one is playing

                        (= last-song-id cur-song-id)
                        0 ; play first if last one was playing

                        :else
                        (inc cur-song-id))]
     {:db db
      :dispatch [:player/change-song next-song-id force?]})))

;;;
;;;
;;; html5 audio player events
;;;
;;;
(reg-event-fx
 :player/do-start

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [song-id force?]]
   (let [url (get-in db [:playlist song-id :url])]
     (console :log :player/do-start url)
     
     (if (or force? (not js/isMobile))
       (audio/load-and-play url)
       (reset! (:click-task db) (fn [] (audio/load-and-play url))))
     {}
     #_(db/set-click-task db (partial audio/load-and-play url)))))


;;;
;;; pause player
;;;
(reg-event-fx
 :player.pause/pause

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   (if js/isMobile
     (reset! (:click-task db) audio/pause)
     (audio/pause))
   {}))

 ;;;
;;; pause player
;;;
(reg-event-fx
 :player.pause/play

 (interceptors-fx :spec false)

 (fn [{:keys [db]}]
   (if js/isMobile
     (reset! (:click-task db) audio/play)
     (audio/play))
   {}))
