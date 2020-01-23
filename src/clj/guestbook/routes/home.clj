(ns guestbook.routes.home
    (:require
      [bouncer.core :as b]
      [bouncer.validators :as v]
      [guestbook.layout :as layout]
      [guestbook.db.core :as db]
      [clojure.java.io :as io]
      [guestbook.middleware :as middleware]
      [ring.util.response]
      [ring.util.http-response :as response]
      ))

;(defn home-page [request]
;  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn home-page [{:keys [flash] :as request}]
      (layout/render
        request
        "home.html"
        (merge {:messages (db/get-messages)}
               (select-keys flash [:name :message :errors])))
      )

;(defn home-page [request]
;      (layout/render
;        request
;        "home.html"
;        {:messages (db/get-messages)}))

(defn about-page [request]
      (layout/render request "about.html"))


(defn validate-message [params]
      (first
        (b/validate
          params
          :name v/required
          :message [v/required [v/min-count 10]])))

(defn save-message! [{:keys [params]}]
      (if-let [errors (validate-message params)]
              (-> (response/found "/")
                  (assoc :flash (assoc params :errors errors)))
              (do
                (db/save-message! params)
                (response/found "/"))))

;(defn save-message! [{:keys [params]}]
;      (db/save-message!
;        (assoc params :timestamp (java.util.Date.)))
;      (println "gotcha!")
;      (println params)                                      ;
;      (response/found "/"))


(defn home-routes []
      [""
       {:middleware [middleware/wrap-csrf
                     middleware/wrap-formats]}
       ["/" {:get home-page}]
       ["/about" {:get about-page}]
       ["/message" {:post save-message!}]])

