#!/bin/bash

rm -f css/app.css
rm -rf resources/public/js/compiled/*
rm -f main.js

sass -I bower_components/foundation-sites/scss -I css css/app.scss css/app.css

lein with-profile production cljsbuild once
# lein cljsbuild once

for i in resources bower_components di-songs.html fonts index.html js css
do
  rsync -arzv "$i" deploy@myfutures.trade:/var/www/bitchcoin-in-production
done
