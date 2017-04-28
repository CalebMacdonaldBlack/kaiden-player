(ns kaiden-player.routes.home
  (:require [kaiden-player.layout :as layout]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [amazonica.aws.s3 :as s3]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response redirect content-type]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [kaiden-player.auth :refer [logout login]]
            [clojure.java.io :as io]
            [ring.util.codec :refer [url-encode]]
            [clojure.tools.logging :as log])
  (:import (com.amazonaws SdkClientException)))

(def cred {:endpoint "ap-southeast-2"})

(defn home-page [request]
  (if-not (authenticated? request)
    (redirect "/login")
    (layout/render "home.html")))

(defn song-link [title]
  (str "/songs/" (url-encode title) ".mp3"))

(def songs ["wFsLGh81GeI","l9PxOanFjxQ","i3EvYpGdZIk","MmZexg8sxyk","cjVQ36NhbMk","ShlW5plD_40","QCyIY10KBnk","9vMh9f41pqE","olhMrdEr4Kw","b8y-XpFnojI","Bznxx12Ptl0","KnL2RJZTdA4","XYgSHOWNE0M","h8YcG6EMjg0","YqeW9_5kURI","KI8ihk2f8Kw","PVjiKRfKpPI","kt0g4dWxEBo","r00ikilDxW4","TUT46ZR5CC8","d-diB65scQU","vtPk5IUbdH0","yTjfMczkOYo","J7MQDULNIdU","TheUtUU3miE","YpSz69Z2cYo","OuQ4w8KafqY","OeOuLGNJx6I","TvJ2DyCe11M","S5FCdx7Dn0o","u1l6yEBZ3g4","yX6FsTIq6ls","WLTI2rWAlV4","JRfuAukYTKg","bmXumtgwtak","X3aCTwXNTX8","0Gjx-ZQuQ_Y","OPf0YbXqDm0","0RiQDCWsk-U","pVSGZEud00s","aB16fJpoj-I","PIh2xe4jnpk","yRFyKRlPfEg","2T2IUkSC_1A","mtIbcXlFHGI","6Tou8-Cz8is","e-ORhEE9VVg","WA4iX5D9Z64","nfWlot6h_JM","AYtdz2hrq9M","-KT-r2vHeMM","t-yCg-0-baE","oqyM0Qbnp3g","9lh5Xk_3yXA","FC3y9llDXuM","KTJ7nAlGc7I","q6-ZGAGcJrk","6tf_hCmP_4I","f7ld-3nZUxA","iq2WJ2r0NkM","cVp2C--VSZE","bXsmGSnq3lE","RisWH8iMLdE","Qk9GjbbueHo","wN0jj0QSGpU","LDZX4ooRsWs","IcseamG7ReY","7UUPawNC5Lg","6_VxMatc9uE","jvghTBQg5kA","52fEIlugfHI","98WtmW-lfeE","e82VE8UtW8A","pa14VNsdSYM","f2dJxFIV28Y","SYM-RJwSGQ8","CFWX0hWCbng","3taEuL4EHAg","hHUbLv4ThOo","PiX3f9pcfOg","RsM0RLAK1nY","oBZvcxC38CQ","ZeJkbqjQvnk","-TKm4L9Ybik","0HDdjwpPM3Y","r1dquH_KOQc","aa911_8TP2s","bty4-TgBV9I","cIFspx_BMN0","y6Sxv-sUYtM","CibjkFL2yfc","8eJDTcDUQxQ","4xmckWVPRaI","ZBR2G-iI3-I","AP8JVnxEVGE","HMUDVMiITOU","rMbATaj7Il8","hpPb7cwxncQ","FotCW5OIFZc","lJp-HER1RvI","iS1g8G_njx8","kTHNpusq654","0EWbonj7f18","7RMQksXpQSk","7PCkvCPvDXk","nlcIKh6sBtc","oC-GflRB0y4","T4o7YqMb8qw","tkXNEmtf9tk","ZJL4UGSbeFg","XfR9iY5y94s","4H5I6y1Qvz0","gCYcHz2k5x0","YR5ApYxkU-U","zUwEIt9ez7M","P3CxhBIrBho","ecRvHLgnQY4","IDZqmF3zS04","tuK6n2Lkza0","s9MszVE7aR4","BfOdWSiyWoc","ytBMyqNCcvU","YxxmZhvkHa8","nU1VfYYKMDk","Zi_XLOBDo_Y","O-zpOMYRi0w","Gz2GVlQkn4Q","oQfAZVsz6KM","9Cyokaj3BJU","THt5u-i2d9k","mD9vKtkm5GQ","TRwrbCDNWSw","uAsV5-Hv-7U","0mYBSayCsH0","nppJyBUtb5o","FgvfRSzmMoU","_lK4cX5xGiQ","-ScjucUV8v0","t4H_Zoh7G5A","j5-yKhDd64s","BYE4CVhVkhw","DmeUuoxyt_E","BJk6gZuPKRE","N6voHeEa3ig","BGpzGu9Yp6Y","PsO6ZnUZI0g","kUzRHTSgSqo","1y6smkh6c-0","UoPplpBPQxQ","ffej15-Dgl0","PWgvGjAhvIw","YlUKcNNmywk","J9FImc2LOr8","DJ6CcEOmlYU","6xCkg2pWmAw","K0K46C82v9o","up7pvPqNkuU","4z9TdDCWN7g","bSwL9deXNW8","59-ttKvESEQ","BnO3nijfYmU","uelHwf8o7_U","OOAMfUJ3tsc","XNtTEibFvlQ","bbr60I0u2Ng","BqDjMZKf-wg","vhwDxNqWtxk","FtgWjjOMBO4","KRzMtlZjXpU","RQ9_TKayu9s","nJODhAWuZEk","JjlJBmwF3PI","DxSAZMV63u8","JPtQ4K-evBo","SYs2HHYqmxw","t6HSlZBNwUQ","lCuMeX7LjWY","iWIADZKU9dw","Bo-qweh7nbQ","0QWhAN3q1AQ","wyx6JDQCslE","E0CazRHB0so","_nrDLWoDVCI","UC86yQAzaxg","L_jWHffIx5E","mRH8_A5P9ZM","HjVeazhEug0","gzoEK545j64","0KSOMA3QBU0","3O1_3zBUKM8","VWl7WjAtBME","GnXFJOXvL_A","CGyEd0aKWZE","kdemFfbS5H0","Wt88GMJmVk0","F21aifX0lZY","05j25PHA7l8","VFGbSWP-G-o","RFnI-JmQh84","4JipHEz53sU","QHg9VVkE9Bk","4U_RvUYINpo","JZIVmKOdrBk","VDvr08sCPOc","Hq5i-6cJMJs","Om4eqmF1hlM","8UFIYGkROII","RaCodgL9cvk","rMqayQ-U74s","CduA0TULnow","LOZuxwVk7TU","edP0L6LQzZE","QR_qa3Ohwls","iP6XpLQM2Cs","5NPBIwQyPWE","TIy3n2b7V9k","WfM7jaXHH8Y","h3cE9iXIx9c","V5bYDhZBFLA","JwQZQygg3Lk","D6X4At_rxFE","CevxZvSJLk8","63OdRmba5Xk","ewRjZoRtu0Y","I_izvAbhExY","a5N7RNQUKts","v5rOdF9rUKI","S9bCLPwzSC0","FWOsbGP5Ox4","tAp9BKosZXs","kPBzTxZQG5Q","zYxkezUr8MQ","ahWmkV0mtvk","updoMIHMBbU","onzL0EM1pKY","SkTt9k4Y-a8","KQ6zr6kCPj8","fLexgOxsZu0","kVpv8-5XWOI","4JKAid8Z-rA","XWJrPzAUzAs","3s3vHFyybxk","6EEW-9NDM5k","fHC05_9b0gw","rY0WxgSXdEE","fJ9rUzIMcZQ","OBwS66EBUcY","ABpgvWHJAGU","btPJPFnesV4","ox-lfowevqA","j50ZssEojtM","T6j4f8cHBIM","7wfYIMyS_dI","qMxX-QOV9tI","DKfBCarK9g4","5qm8PH4xAss","fWNaR-rxAic","JM7e5tsYOi8","NOubzHCUt48","SeIJmciN8mo","NisCkxU544c","rYE1S6r3bkg","TRzj1ibTBpc","swRF0Hn1_zM","GSjlxbxAymM","eK8Ri0COF1w","cMg5cQd5f50","8fijggq5R6w","w4s6H4ku6ZY","wXcdYBh3hgg","ezzsB4WTGz4","YVkUvmDQ3HY","68ugkg9RePc","IRvGZffXhfk","6Ejga4kJUts","o8VZX4sHn-4","gAjR4_CbPpQ","BaAC-xmX3Fc","2BxVBziFvtU","MJv3bCpbCWg","9bZkp7q19f0","r8_-waVr5Yc","EAmChFTLP4w","fGx6K90TmCI","X0gtzHJphVg","UOFSdN0zk5Q","qlJ9n15L3gk","desJKYvdq9A","04F4xlWSFh0","ZpUYjpKg9KY","YHjdTZ-myCU","xat1GVnl8-k","SDTZ7iX4vTQ","ktvTqknDobU","InRDF_0lfHk","Y8yQuivSEio","yyDUC1LUXSU","wWhtcU4-xAM","CR8logunPzQ","wBzqOa9y02I","8UVNT4wvIGY","uCDxmApyFyQ","fdHCec23BKE","lqCyTM1bF6Q","r0U0AlLVqpk","LnSp9rgfel8","E9YwyfX33LU","bjSpO2B6G4s","psuRGfAaju4","mqWq_48LxWQ","SihoKQ-uLqE","uxUATkpMQ8A","3soskkvYBgM","lIOWW28pmso","aIsI8mPr3qc","XD1cxSE25ck","bHHUhcV2eVY","ooZwmeUfuXg","8afv17Ff0_8","EhZWQ5_dcNY","_BVxUhoYcj0","2vC7tTr3X54","PPtSKimbjOU","XEjLoHdbVeE","eC99JhQq-3w","YJVmu6yttiw","ufEejvMEP64","KFzT7ms5kwU","5NV6Rdv1a3I","qQkBeOisNM0","KFSTi17tnxk","gH476CxJxfg","ASO_zypdnsQ","RbtPXFlZlHg","RBumgq5yVrA","OpQFFLBMEPI","kYtGl1dX5qI","QK8mJJJvaes","UxxajLWwzqY"])

(defn upload-song
  ([request]
   (upload-song request 1))
  ([request repeat-count]
   (log/debug (str "Attempt: " repeat-count))
   (try
     (let [link (get (:params request) "link")
           title (get (:params request) "title")
           file-size (get (:params request) "filesize")]
       (log/debug (str "Uploading \nURL: " link
                       "\nTitle: " title
                       "\nSize: " file-size))
       (let [song-stream (clojure.java.io/input-stream link)]
         (response/ok (s3/put-object cred :bucket-name "kaiden-player"
                                     :key (str title ".mp3")
                                     :input-stream song-stream
                                     :metadata {:content-length (read-string file-size)}
                                     :return-values "ALL_OLD")))
       (song-link title))
     (catch SdkClientException e (if (> repeat-count 100)
                                     (throw e)
                                     (do (Thread/sleep 5000)
                                         (upload-song request (inc repeat-count))))))))

(defn upload-playlist [request]
  (prn "===================================================================")
  (doseq [song songs]
    (let [link (str "http://www.youtubeinmp3.com/fetch/?format=json&filesize=1&video=https://www.youtube.com/watch?v=" song)]
      (prn link)
      (try
        (upload-song {:params (json/parse-string (:body (client/get link)))})
        (catch Exception e
          (spit "failed-songs.txt" link :append true))))))

(defn get-song-titles [_]
  (let [list (:object-summaries (s3/list-objects cred :bucket-name "kaiden-player"))]
    (map #(:key %) list)))

(defn get-song [request]
  (let [title (get-in request [:params :title])]
    (:input-stream (s3/get-object "kaiden-player" title))))


(defroutes home-routes
           (GET "/" [] home-page)
           (GET "/uploadplaylist" [] upload-playlist)
           (GET "/login" [] (layout/render "login.html"))
           (GET "/logout" [] logout)
           (POST "/login" [] login)
           (POST "/songs" [] #(response/created (upload-song %)))
           (GET "/songs" [] #(response/ok (get-song-titles %)))
           (GET "/songs/:title" [] #(response/ok (get-song %)))
           (GET "/test" [] (io/input-stream "test.mp3")))
