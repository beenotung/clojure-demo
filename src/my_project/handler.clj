(ns my-project.handler
  (:require
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :as response]
            [clojure.java.io :as io])            
  (:use 
        [hiccup.page :only (html5)]
        [hiccup.form :ref :all]))       
        
(if (not (.exists (io/as-file "data")))
  (.mkdir (java.io.File. "data")))

(def words 
  (if (.exists (io/as-file "data/words"))        
    (read-string (slurp "data/words"))
    []))

(defroutes app-routes
  (GET "/" [] 
    (html5 
      [:head                 
       [:title "clojure app"]
       [:body
        [:h1 "Hello World from clojure"]
        [:ul
         (for [x ["hi" "words"]]
           [:li [:a {:href (str "/" x)} x]])]]]))                                                                                                    
  (GET "/hi" [] 
    (html5
      [:head 
       [:title "Greeting"]]
      [:body
       [:h1  "Hi vistor"]]))
  (GET "/words" [] 
    (html5    
      [:head
       [:title "Word List"]]
      [:body
       [:h1 "Word List"]
       (form-to [:post "/word"]   
         [:label {:for "word"} "word: "]       
         [:input {:id "word" :name "word" :type "text"}]
         " "
         (submit-button "submit")                                                               
         [:ul
          (for [word words] 
            [:li word])])]))
  (POST "/word" [word]
    (do
      (def words (conj words word))
      (spit "data/words" (with-out-str (pr words)))
      (response/redirect "/words")))   
  (route/not-found "Not Found"))

(def app  
  (wrap-defaults app-routes api-defaults))

