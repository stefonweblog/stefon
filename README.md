# Stefon

A composable blogging engine in Clojure. This project will try to approach the feature set of [Wordpress](http://codex.wordpress.org/WordPress_Features).
 * Core Data Model
   * posts (txt, rtf, md)
   * assets (images, audio, videos, documents)
   * tags / categories for content
 * Server component;
   * create post / asset / tag
   * retrieve post / asset
   * update post / asset / tag
   * delete post / asset / tag
   * list posts / assets / tags
   * find posts / assets / tags
 * Workflow component; possibly model workflow with lamina?
   * preview
   * collaboration
   * editor review
   * versioning
 * Plug-in support; I want to determine what's the best interface & messages to pass between stefon and plug-ins; are possible solutions: nrepl protocol, core.async
   * communication between plug-ins
   * way to declare 'stefon' service
   * way to list possible actions (namespace qualify action names)
   * way to publish actions
   * way to listen for feedback from a plug-in
   * way to pass binary data (asset(s)) between stefon and plug-in
 * Database component;
   * adapters for [Datomic](http://www.datomic.com), SQL([Postgres](http://www.postgresql.org), etc), NoSQL ([Mongo](http://www.mongodb.org), etc), cloud storage (AWS [SimpleDB](http://aws.amazon.com/simpledb), [S3](http://aws.amazon.com/s3))
 * Web UI component;
   * wyswyg editor, themes
   * embeddable in Compojure or Pedestal
 * Authentication & Authorization; OpenID
 * Spam Detection
 * Commenting component; default or an external comments service, like disqus or discourse
 * Administration Console
 * Import / Export
 * Multi-lang / Internationalization


## Usage

TBD

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
