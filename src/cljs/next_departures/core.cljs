(ns next-departures.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [next-departures.events]
              [next-departures.subs]
              [next-departures.routes :as routes]
              [next-departures.views :as views]
              [next-departures.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn load-departures-loop []
  (re-frame/dispatch [:load-departures])
  (.setTimeout js/window load-departures-loop 30000))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (load-departures-loop)
  (dev-setup)
  (mount-root))
