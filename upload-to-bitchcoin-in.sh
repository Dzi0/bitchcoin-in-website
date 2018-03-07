#!/bin/bash

sass -I bower_components/foundation-sites/scss -I css css/app.scss css/app.css

lein cljsbuild once

for i in bower_components di-songs.html fonts index.html js css main.js
do
  rsync -arzv "$i" deploy@myfutures.trade:/var/www/bitchcoin-in-production
done
