(ns e2e.core
  (:require [cljs.test :refer-macros [deftest is async use-fixtures]]
            [promesa.core :refer [then finally]]
            ["http" :as http]
            ["serve-handler" :as serve-handler]
            ["taiko" :refer [openBrowser goto closeBrowser text]]))

; Server public/ on a static server.
(use-fixtures :once
  (let [server (.createServer
                http
                #(serve-handler %1 %2 #js {:public "public/"}))]
    {:before #(.listen server 5000)
     :after  #(.close server)}))

; Change debug to true to see the browser performing actions.
(def debug false)
(def browser-opts (if debug
                    #js {:headless false :observe true}
                    #js {}))

(deftest app-works
  (let [test-string "cljs-app is running!"]
    (async done
           (-> (openBrowser browser-opts)
               (then #(goto "http://localhost:5000"))
               (then #(.exists (text test-string)))
               (then #(is % (str "Text '" test-string "' should exist in page")))
               (finally #(closeBrowser))
               (then #(done))))))
