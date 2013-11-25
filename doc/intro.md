## Introduction 

Stefon ***i)*** can be used as a stand alone product. Or ***ii)*** it can be embedded in your Clojure (or JVM) web architecture. The most basic use is from the repl, saving posts, with tags and assets. From there, you can add plugins for Database and Web Interface functions.


### Install / Run 
Execute `git clone git@github.com:twashing/stefon.git`. Then go to that directory, run the repl, and start using the functions in ***'stefon.shell***


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
1. Write a ***plugin.clj*** file under your root namespace 
2. Give it a plugin function like [this](https://github.com/stefonweblog/stefon-datomic/blob/master/src/stefon_datomic/plugin.clj#l249). The function-map passed to your function will have these functions for your use 

```clojure
{ :system-started? shell/system-started? :start-system shell/start-system :attach-plugin shell/attach-plugin }
```


### Plugin Attaching
... 


### Plugin Interface
After attaching, you will receive a map of functions like below. Expect to send and receive message in the shape described here. 
```clojure
{ :id your-channel-id :channel ch :sendfn sfn :recievefn rfn }
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


