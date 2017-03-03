(ns next-departures.events
    (:require [re-frame.core :as re-frame]
              [next-departures.db :as db]
              [next-departures.digitransit :as digitransit]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 :load-departures
 (fn [db _]
   (digitransit/load-departures #(re-frame/dispatch [:loaded-departures %]))
   db))

(re-frame/reg-event-db
 :loaded-departures
 (fn [db [_ departures]]
   (assoc db :departures departures)))
