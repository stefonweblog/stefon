# stefon

A composable blogging engine in Clojure. This project will try to approach the feature set of [Wordpress](http://codex.wordpress.org/WordPress_Features).
 * Core Data Model
   * posts (txt, rtf)
   * assets (images, audio, videos, documents)
   * tags / categories for content
 * Server component;
   * create post / asset
   * retrieve post / asset
   * update post / asset
   * delete post / asset
   * list posts / assets
   * find posts / assets
 * Plug-in support
 * Database component;
   * adapters for [Datomic](http://www.datomic.com), SQL([Postgres](http://www.postgresql.org), etc), NoSQL ([Mongo](http://www.mongodb.org), etc), cloud storage (AWS [SimpleDB](http://aws.amazon.com/simpledb), [S3](http://aws.amazon.com/s3))
 * Workflow component;
   * preview
   * collaboration
   * editor review
   * versioning
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
