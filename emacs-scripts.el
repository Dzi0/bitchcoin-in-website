;;;
;;; extract songs
;;;
;;; cat songs.html| grep -Eoi '<a [^>]+>' | grep -Eo 'href="[^\"]+"' |  grep -Eo '(music/)[^/"]+'

;;;
;;; make playlist from lst of songs
;;;
(defun music-to-playlist ()
  (insert "<li><a href=\"")
  (end-of-line)
  (insert "\"></a></li>")
  (beginning-of-line)
  (forward-line)
  (forward-word)
  (backward-word)
  (if (equal "music" (current-word))
      (music-to-playlist)))
