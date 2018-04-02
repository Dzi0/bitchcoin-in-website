(ns bitchcoin-in-website.audio
  (:require
   [re-frame.core :refer [console]]
   [jayq.core :as jq :refer [$ css html outer-height attr]]))


(defn- audio-player []
  (.getElementById js/document "audio-player"))

(defn- mp3-src []
  (.getElementById js/document "mp3-src"))

(defn play []
  (.. (audio-player)
      play))


(defn pause []
  (.. (audio-player)
      pause))

(defn- on-can-play []
  (console :log :load-and-play :can-play)
  (.play (audio-player)))

(defn load-and-play [url]
  (console :log :load-and-play url)
  
  (-> (mp3-src)
      (aset "src" url))

  (aset (audio-player) "src" url)
  (.play (audio-player))
  
  #_(.removeEventListener (audio-player)
                        "canplaythrough"
                        on-can-play)
  #_(.addEventListener (audio-player)
                     "canplaythrough"
                     on-can-play))
