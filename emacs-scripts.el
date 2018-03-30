;;;
;;; extract songs
;;;
;;; cat songs.html| grep -Eoi '<a [^>]+>' | grep -Eo 'href="[^\"]+"' |  grep -Eo '(music/)[^/"]+'

;;;
;;;
;;; extract songs and rsycn them to server one by one
;;;
;;;
;; cat di-songs.html| grep -Eoi '<a [^>]+>' | grep -Eo 'href="[^\"]+"' |  grep -Eo '(music/) [^/"]+' | ruby -e 'require "uri"; STDIN.each_line {|l| print(URI.unescape(l.chomp) + "\0")}' | xargs -0 -I % sh -c 'rsync -avz "%"  deploy@myfutures.trade:/var/www/bitchcoin-in-production/music'

;;;
;;;
;;; start local server to serve files with symlinks to music
;;;
;;;
;;; ruby -run -ehttpd . -p8000

;;;
;;;
;;; compile sass once for build or so
;;;
;;;
;;; sass -I bower_components/foundation-sites/scss -I css css/app.scss css/app.css

;;;
;;;
;;; watch scss files
;;;
;;;
;;; sass --watch -I ../bower_components/foundation-sites/scss -I . app.scss
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
