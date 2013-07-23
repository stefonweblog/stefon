(ns stefon.domain)


(defn gen-post-type []

  (defrecord Post [id title content created-date]))

(defn gen-asset-type []

  (defrecord Asset [id asset type]))

(defn gen-tag-type []

  (defrecord Tag [id name]))
