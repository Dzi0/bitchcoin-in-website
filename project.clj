(defproject bitchcoin-in-website "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 [enfocus "2.1.1"]

                 [reagent "0.6.2"]
                 [reagent-utils "0.3.1"]
                 [re-frame "0.9.2"]]
  :plugins [[lein-figwheel "0.5.13"]]
  :clean-targets [:target-path "out"]

  :target-path "target/%s"

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {
   ;;
   ;; local dev
   ;;
   :dev
   {:dependencies [[binaryage/devtools "0.9.9"]
                   [com.cemerick/piggieback "0.2.1"]
                   [figwheel-sidecar "0.5.13"]
                   [org.clojure/tools.nrepl "0.2.11"]]

    :plugins [[lein-figwheel "0.5.13"]
              [lein-cljsbuild "1.1.7"]]

    :source-paths ["src_cljs" "env/dev"]

    ;;
    ;; build cljs
    ;;
    :cljsbuild {:builds [{:id "dev"
                          :source-paths ["src_cljs"]
                          :figwheel {:on-jsload "bitchcoin-in-website.core/mount-root"}
                          :compiler {:main ^:skip-aot bitchcoin-in-website.core
                                     :output-to "main.js" ; FIXME
                                     :output-dir "resources/public/js/compiled/out"
                                     :asset-path "/js/compiled/out"
                                     :source-map-timestamp true
                                     :optimizations :none
                                     :preloads [devtools.preload]
                                     :closure-defines {goog.DEBUG true}}}]}

    }
   ;;
   ;; production
   ;;
   :production
   {:plugins [[lein-cljsbuild "1.1.7"]]
    :omit-source true
    :aot :all
    :main bitchcoin-in-website.core
    :cljsbuild
    {:builds {:app {:id "production"
                    :source-paths ["src_cljs"]
                    :compiler {:main bitchcoin-in-website.core
                               :output-to "resources/public/js/compiled/main.js"
                               :optimizations :advanced
                               :pretty-print true
                               :pseudo-names true}}}}}
   })
