# Introduction to Stefon

Stefon ***i)*** can be used as a stand alone product. Or ***ii)*** it can be embedded in your Clojure (or JVM) web architecture. The most basic use is from the repl, saving posts, with tags and assets. From there, you can add plugins for Database and Web Interface functions.



## Install / Run 
Execute `git clone git@github.com:twashing/stefon.git`. Then go to that directory, run the repl, and start using the functions in ***'stefon.shell***

## Shell CRUD functions
1. create 
2. retrieve 
3. update 
4. delete 
5. find 
6. list

## Plugin Interface
After attaching, you will receive a map of functions like below. , Expect to send and receive message in the shape described here. 
```clojure
{ :id your-channel-id :channel ch :sendfn sfn :recievefn rfn }
```

## Plugin Inclusion 
1. Write a plugin.clj file under your root namespace 
2. Give it a plugin function like [this](https://github.com/stefonweblog/stefon-datomic/blob/master/src/stefon_datomic/plugin.clj#l249). The function-map passed to your function will have these functions for your use 
```clojure
{ :system-started? shell/system-started? :start-system shell/start-system :attach-plugin shell/attach-plugin }
```


