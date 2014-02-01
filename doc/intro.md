## Introduction

Stefon ***i)*** can be used as a stand alone product. Or ***ii)*** it can be embedded in your Clojure (or JVM) web architecture. The most basic use is from the repl, saving posts, with tags and assets. From there, you can add plugins for Database and Web Interface functions.


### Install / Run
Execute `git clone git@github.com:twashing/stefon.git`. Then go to that directory, run the repl, and start using the functions in ***'stefon.shell***


```
git clone git@github.com:twashing/stefon.git
cd stefon
lein repl
=> (require '[stefon.shell :as shell])
=> (shell/start-system)
=> (shell/create :post "My Title" "My Content" "type/txt" "01/30/2014" "01/30/2014" [] [])
=> (shell/create :post "Bits and Bobs" "The most basic use of Stefon is from the repl, saving posts, with tags and assets." "type/txt" "01/30/2014" "01/30/2014" [] [])
=> (shell/create :post "Something Else" "It was a dark and stormy night." "type/txt" "01/30/2014" "01/30/2014" [] [])
=>
=> (shell/find :post {:title "Something Else"})
({:tags [], :assets [], :modified-date "01/30/2014", :created-date "01/30/2014", :content-type "type/txt", :content "It was a dark and stormy night.", :id "5c4f7562-9e9d-47e7-aec2-4583b217787b", :title "Something Else"})
=> 
=> (shell/list :post)
[#stefon.domain.Post{:id "6bd677ba-8a4f-4bc8-a491-84f3d328bb96", :title "My Title", :content "My Content", :content-type "type/txt", :created-date "01/30/2014", :modified-date "01/30/2014", :assets [], :tags []} #stefon.domain.Post{:id "5a60b772-d4cb-4eb3-a501-cf3cdeb38480", :title "Bits and Bobs", :content "The most basic use of Stefon is from the repl, saving posts, with tags and assets.", :content-type "type/txt", :created-date "01/30/2014", :modified-date "01/30/2014", :assets [], :tags []} #stefon.domain.Post{:id "2ef3908f-e5f6-4bb1-88b2-db9ac6eaba1d", :title "Something Else", :content "It was a dark and stormy night.", :content-type "type/txt", :created-date "01/30/2014", :modified-date "01/30/2014", :assets [], :tags []}]
=>

;; if you want more debugging
=> (require '[taoensso.timbre :as timbre])
=> (timbre/set-level! :trace)
```


### Shell CRUD functions
1. Posts
  * `(create :post title content content-type created-date modified-date assets tags)`
  * `(retrieve :post id)`
  * `(update :post id update-map)`
  * `(delete :post id)`
  * `(find :post param-map)`
  * `(list :post)`
2. Assets
  * `(create :asset name type asset)`
  * `(retrieve :asset id)`
  * `(update :asset id update-map)`
  * `(delete :asset id)`
  * `(find :asset param-map)`
  * `(list :asset)`
3. Tags
  * `(create :tag name)`
  * `(retrieve :tag id)`
  * `(update :tag id update-map)`
  * `(delete :tag id)`
  * `(find :tag param-map)`
  * `(list :tag)`


### Plugin Inclusion
1. Write ***plugin.clj*** file under your root namespace
2. Give it `plugin` and `plugin-ack` functions like this. Here's [an example](https://github.com/stefonweblog/stefon/blob/master/plugins/heartbeat/plugin.clj)

  ```clojure
  (defn plugin
    "Step 1: Simply send back this plugin's handler function"
    []
    receivefn)

  (defn plugin-ack
    "Step 2: We're going to expect an acknowledgement with the following keys: '(:id :sendfn :receivefn :channel)"
    [result-map])
  ```

3. When Stefon starts up, the `plugin-ack` function will give you a result-map with the below fields. The :channel is a core.async channel that the kernel uses to send messages to your plugin. :sendfn and :receivefn will be how your plugin communicates to the kernel.
  ```clojure
  { :id :channel :sendfn :receivefn }
  ```


### Plugin CRUD functions

This just provides first pass examples of calling CRUD from a plugin

1. Posts
  ```clojure
  (sendfn {:id id
           :message {:stefon.post.create
                     {:parameters {:title title :content content :content-type content-type :created-date cdate :modified-date mdate :assets [] :tags []}}}}))))
  ```

2. Assets
  ```clojure
  (sendfn {:id id
           :message {:stefon.asset.create
                     {:parameters {:name name :type type :asset asset}}}}))))
  ```

3. Tags
  ```clojure
  (sendfn {:id id
           :message {:stefon.tag.create
                     {:parameters {:name name}}}}))))
  ```
