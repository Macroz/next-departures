(ns next-departures.digitransit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-frame.core :as re-frame]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def nearest-query "
query foo {
  nearest(lat:60.168577, lon:24.945958, filterByPlaceTypes:[DEPARTURE_ROW]) {
    edges {
      node {
        distance
        place {
          ... on DepartureRow {
            stop {
              name
            }
            pattern {
              headsign
              route {
                shortName
              }
            }
            stoptimes {
              realtimeState
              serviceDay
              scheduledArrival
              realtimeArrival
            }
          }
        }
      }
    }
  }
}")

(defn add-departure-time [d]
  (let [t (* 1000 (+ (get-in d [:place :stoptimes 0 :serviceDay])
                     (get-in d [:place :stoptimes 0 :scheduledArrival])))]
    (assoc d :departure-time t)))

(defn load-departures [callback]
  (go (let [response (<! (http/post "https://api.digitransit.fi/routing/v1/routers/hsl/index/graphql"
                                   {:with-credentials? false
                                    :timeout 2000
                                    :json-params {:query nearest-query}
                                    }))
            edges (get-in response [:body :data :nearest :edges])
            departures (map (comp add-departure-time :node) edges)]
        (callback departures))))
