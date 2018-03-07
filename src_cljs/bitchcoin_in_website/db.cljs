(ns bitchcoin-in-website.db
  (:require [clojure.spec.alpha :as s]
            [clojure.core.reducers :as reducers]))

;;;
;;;
;;; constants
;;;
;;;
(def music-urls
  [
   "music/Not%20Waving%20-%20Me%20Me%20Me.mp3",
   "music/SHXCXCHCXSH%20-%20SsSsSsSsSsSsSsSsSs.mp3",
   "music/Prurient%20-%20I%20Have%20A%20Message%20For%20You.mp3",
   "music/MikeQ%20-%20Just%20for%20you.mp3",
   "music/Anika%20-%20Masters%20of%20War.mp3",
   "music/Fatima%20Al%20Qadiri%20-%20War%20Games.mp3",
   "music/Cigarettes%20After%20Sex%20-%20Nothing%27s%20Gonna%20Hurt%20You%20Baby.mp3",
   "music/Fatima%20Al%20Qadiri%20-%20How%20Can%20I%20Resist%20U.mp3",
   "music/Appetite%20-%20Anxiety.mp3",
   "music/Gesaffelstein%20-%20Nameless.mp3",
   "music/LSDXOXO%20-%20TECHNOLOGIC%20%28REMIX%29.mp3",
   "music/WWWINGS,%20111X%20&amp;%20BUNGALOV%20-%20Infinite%20Machine.mp3",
   "music/Kuedo%20-%20Visioning%20Shared%20Tomorrows.mp3",
   "music/Lenny%20Dee%20-%20The%20Dreamer.mp3",
   "music/Perfume%20-%20Computer%20City.mp3",
   "music/Kuedo%20-%20Work,%20Live%20and%20Sleep%20in%20Collapsing%20Space.mp3",
   "music/Ecferus%20-%20Fragmented%20Body.mp3",
   "music/Loke%20Rahbek%20&amp;%20Puce%20Mary%20-%20The%20Female%20Form.mp3",
   "music/Boy%20Harsher%20-%20Yr%20Body%20Is%20Nothing.mp3",
   "music/ALEKSEI%20TARUTS%20-%20tsunami.mp3",
   "music/Devika%20Shawty%20-%20Abyss%20%28feat.%20Delta%20X%29.mp3",
   "music/Pharmakon%20-%20Vacuum.mp3",
   "music/Hmot%20-%20I%20Have%20Been%20To%20Hell%20And%20Back%20And%20Let%20Me%20Tell%20You,%20It%20Was%20Beautiful.mp3",
   "music/Haus%20Arafna%20-%20Heart%20Beats%20Blood%20Flows.mp3",
   "music/Helena%20Hauff%20-%20Culmination%20Of%20Frustration.mp3",
   "music/Evian%20Christ%20-%20Fuck%20It.mp3",
   "music/Eomac%20-%20We%20Are%20All%20Going%20to%20Die.mp3",
   "music/Helena%20Hauff%20-%20We%20Only%20Want%20Tragedy.mp3",
   "music/Hmot%20-%20Count%20To%20Ten.mp3",
   "music/Coucou%20Chloe%20-%20Stealth%20%28feat.%20WWWINGS%29.mp3",
   "music/Cheena%20-%20Did%20I%20Tell%20You%20Last%20Night.mp3",
   "music/Cut%20Hands%20-%20Madwoman.mp3",
   "music/Hypnoskull%20vs.%20Tunnel%20-%20Damn%27%20Shit%21.mp3",
   "music/Helena%20Hauff%20-%20Do%20You%20Really%20Think%20Like%20That_.mp3",
   "music/Hypnoskull%20-%20Fuck%20You,%20And%20Fuck%20Your%20Moral.mp3",
   "music/Hypnoskull%20-%20Incredibly%20Agressive.mp3",
   "music/Prurient%20-%20Historically,%20Women%20Use%20Poison%20To%20Kill.mp3",
   "music/111X%20-%20Social%20Exclusion.mp3",
   "music/Charli%20XCX%20-%20Femmebot%20%28feat.%20Dorian%20Electra%20and%20Mykki%20Blanco%29.mp3",
   "music/Acid%20Jesus%20-%20Elektrosmog.mp3",
   "music/Ekman%20-%20I%20Am%20Not%20A%20Turing%20Machine,%20You%20Are.mp3",
   "music/M.E.S.H.%20-%20Follow%20&amp;%20Mute.mp3",
   "music/FALSE%20PRPHT%20-%20DELETE%20MY%20INTERNET%20BROWSER%20HISTORY.mp3",
   "music/Sissy%20Nobby%20-%20Bbhhm.mp3",
   "music/Pharmakon%20-%20No%20Natural%20Order.mp3",
   "music/Gesaffelstein%20-%20Depravity.mp3",
   "music/Mykki%20Blanco%20-%20Punish%20me%20for%20Fun.mp3",
   "music/DJ%20Delirium%20-%20Never%20Gonna%20Stop.mp3",
   "music/Sissy%20Nobby%20-%20Bbhhm.mp3",
   "music/Sutcliffe%20Ju%CC%88gend%20-%20Nothing%20Has%20Changed.mp3",
   "music/sega%20bodega%20-%20have%20u_.mp3",
   "music/Hypnoskull%20-%20Desire.mp3",
   "music/Croatian%20Amor%20and%20Lust%20For%20Youth%20-%20Sister%28Club%20Mix%29.mp3",
   "music/Internazionale%20-%20Pleasure%20And%20Shame.mp3",
   "music/Perturbator%20-%20Future%20Club.mp3",
   "music/Evian%20Christ%20-%20That%27s%20Me.mp3",
   "music/Haus%20Arafna%20-%20Paranoia.mp3",
   "music/Gesaffelstein%20-%20Obsession.mp3",
   "music/_vtol_%20-%20end.mp3",
   "music/Abyss%20X%20-%20Couldn%27t%20Care%20Less.mp3",
   "music/Sissel%20Wincent%20-%20Tricky%20Question%20Why.mp3",
   "music/Halcyon%20Veil%20-%20Toxe%20Xic.mp3",
   "music/Sega%20Bodega%20-%20Bush%20Baby.mp3",
   "music/Patricia%20-%20The%20Words%20Are%20Just%20Sounds.mp3",
   "music/wolf%20eyes%20-%20no%20answer.mp3",
   "music/Croatian%20Amor%20-%20Love%20Means%20Taking%20Action.mp3",
   "music/Grebenstein%20-%20No%20You%20Dont.mp3",
   "music/Not%20Waving%20-%20I%20Know%20I%20Know%20I%20Know.mp3",
   "music/sega%20bodega%20-%20cc%20%28feat.%20shygirl%29.mp3"
   ]
  )

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
  (into {} (map #(hash-map %2 (default-song %1 %2)) music-urls (range))))

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
