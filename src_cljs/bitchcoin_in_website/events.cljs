(ns bitchcoin-in-website.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx path trim-v after debug reg-fx console dispatch]]

   [jayq.core :as jq :refer [$ css html outer-height attr]]

    [bitchcoin-in-website.db :as db]

   [bitchcoin-in-website.interceptors :refer [interceptors
                                              interceptors-fx]]))


(reg-event-fx
 :initialize

 (interceptors-fx :spec true)

 (fn [_]
   (console :log :initialize)

   ;; grab all songs from article
   (.each (js/$ "article a")
          (fn []
            (this-as self
              (db/add-music-url (.. (js/$ self)
                                    (attr "href"))))))

   {:db (db/default-db)
    :dispatch [:player/change-song 0]}))

(reg-event-fx
 :ui.playlist/click

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [song-id]]

   (console :log :clicked song-id)

   {:db db
    :dispatch [:player/change-song song-id]}))


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
      :dispatch [:player/change-song song-id]})))


;;;
;;;
;;; player events
;;;
;;;

;;;
;;; start playing song
;;;
(reg-event-fx
 :player/start

 (interceptors-fx :spec true)

 (fn [{:keys [db]} [song-id]]
   (console :log :started song-id)

   {:db (-> db
            (update-in [:playlist song-id :active?] not))
    :dispatch-n [[:player/do-start song-id]
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

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [next-song-id]]
   (console :log :changing-to next-song-id)

   (let [cur-song-id (:id (first (filter #(:active? %) (vals (:playlist db)))))]
     {:db db
      :dispatch-n (cond-> []
                    cur-song-id
                    (conj [:player/stop cur-song-id])

                    true
                    (conj [:player/start next-song-id]))})))

;;;
;;; play next
;;;
(reg-event-fx
 :player/play-next

 (interceptors-fx :spec :false)

 (fn [{:keys [db]}]
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
      :dispatch [:player/change-song next-song-id]})))

;;;
;;;
;;; html5 audio player events
;;;
;;;
(reg-event-fx
 :player/do-start

 (interceptors-fx :spec false)

 (fn [{:keys [db]} [song-id]]
   (let [url (get-in db [:playlist song-id :url])
         audio (.getElementById js/document "audio-player")]
     (aset audio "src" url)
     (.load audio)
     (.play audio))
   {}))


;;;
;;; toggle pause
;;;
(reg-event-fx
 :player/do-toggle-pause

 (interceptors-fx :spec true)

 (fn [{:keys [db]}]
   (let [paused? (get db :paused?)
         audio (.getElementById js/document "audio-player")
         $pause-button ($ :#pause-button)]
     (if paused?
       (do
         (.play audio)
         (jq/remove-class $pause-button "paused"))
       (do
         (.pause audio)
         (jq/add-class $pause-button "paused")))
     {:db (update db :paused? not)})))
