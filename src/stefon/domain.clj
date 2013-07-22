(ns stefon.domain)


(defn gen-post-type []

  (defrecord Post [title content created-date]))

(defn gen-asset-type []

  (defrecord Asset [asset type]))

(defn gen-tag-type []

  (defrecord Tag [name]))
