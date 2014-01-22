# Stefon

## Overview

Stefon is a composable blog engine written in Clojure. This project will *try* to approach the feature set of [Wordpress](http://codex.wordpress.org/WordPress_Features). The architecture consists of a small kernel with a plugin system wrapped around it. And composable means you could choose to only use a core server component, posting txt entries, let's say with an in-memory data store. If you want a little more, you can choose to add a DB adapter out to Datomic, Import / Export support, etc. This is all still within a running Clojure repl. So additionally, you might choose to add a Web UI (or tablet or smartphone UI), and so on.

See the [Introduction Page](doc/intro.md) for more detailed usage information.

## Install / Run
 * See the [Install / Run](doc/intro.md#install--run) section on the Introduction Page

## Plugin Inclusion
 * See the [Plugin Inclusion](doc/intro.md#plugin-inclusion) section on the Introdcution Page

## Functionality

 * Core Data Model
   * posts (txt, rtf, md)
   * assets (images, audio, videos, documents)
   * tags / categories for content
 * Server component
   * create post / asset / tag
   * retrieve post / asset
   * update post / asset / tag
   * delete post / asset / tag
   * list posts / assets / tags
   * find posts / assets / tags
 * Shell; wrapper where you can run crud commands at a terminal

## Plug-in System
   * communication between plug-ins
   * way to declare 'stefon' service
   * way to list possible actions (namespace qualify action names)
   * way to publish actions
   * way to listen for feedback from a plug-in
   * way to pass binary data (asset(s)) between stefon and plug-in

### Plugins

This is just an initial poke at the plugins I'd like to see for the working system. Some of these are already in development.
   * Database component
     * adapters for [Datomic](http://www.datomic.com), SQL([Postgres](http://www.postgresql.org), etc), NoSQL ([Mongo](http://www.mongodb.org), etc), cloud storage (AWS [SimpleDB](http://aws.amazon.com/simpledb), [S3](http://aws.amazon.com/s3))
   * Web UI component
     * wyswyg editor, themes
     * embeddable in Compojure or Pedestal
   * Workflow component; possibly model workflow with lamina?
     * preview
     * collaboration
     * editor review
     * versioning
   * Authentication & Authorization; OpenID
   * Spam Detection
   * Commenting component; default or an external comments service, like disqus or discourse
   * Administration Console
   * Import / Export
   * Multi-lang / Internationalization


# License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
