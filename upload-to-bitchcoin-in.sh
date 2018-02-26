#!/bin/bash

rsync -arzv * deploy@bitchcoin.in:/var/www/bitchcoin-in-production
