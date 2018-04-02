#!/bin/bash

echo "cleaning..."

rm -f css/app.css
rm -rf resources/public/js/compiled/*
rm -f main.js

echo "compiling SASS..."
sass -I bower_components/foundation-sites/scss -I css css/app.scss css/app.css

echo "compiling CLJS"
lein with-profile production cljsbuild once

echo "uglyfying..."
uglifyjs --compress --mangle -o resources/public/js/compiled/main.min.js -- resources/public/js/compiled/main.js

mv resources/public/js/compiled/main.min.js resources/public/js/compiled/main.js

# lein cljsbuild once

echo "deploying..."
for i in images resources bower_components di-songs.html fonts index.html js css
do
  rsync -arzv "$i" deploy@myfutures.trade:/var/www/bitchcoin-in-production
done
