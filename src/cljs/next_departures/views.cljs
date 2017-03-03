(ns next-departures.views
  (:require [re-frame.core :as re-frame]
            [cljs-time.core :as core]
            [cljs-time.local :as tl]
            [cljs-time.coerce :as tc]
            [cljs-time.format :as tf]
))


;; home

(def departure-time-formatter (tf/formatter "HH:mm"))

(defn departure-time [t]
  [:span (tf/unparse departure-time-formatter (core/to-default-time-zone (tc/from-long t)))])

(defn departure [d]
  [:tr.departure
   [:td.distance (:distance d) "m"]
   [:td.short-name (get-in d [:place :pattern :route :shortName])]
   [:td.headsign (get-in d [:place :pattern :headsign])]
   [:td.departure-time [departure-time (:departure-time d)]]
   ])

(defn home-panel []
  (let [departures (re-frame/subscribe [:departures])]
    (fn []
      [:div
       [:h1 "Next Departures"]
       (into [:table.departures]
             (for [d @departures]
               [departure d]))])))


;; about

(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [show-panel @active-panel])))
