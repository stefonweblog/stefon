
;; Datomic example code
;; Demonstrates using datalog with Clojure defrecords
(use '[datomic.api :only [q db] :as d])
 
;;; http://www.lshift.net/blog/2010/08/21/some-relational-algebra-with-datatypes-in-clojure-12
(defrecord Supplier [number name status city])
(defrecord Part [number name colour weight city])
(defrecord Shipment [supplier part quantity])
 
;; sample data
(def suppliers
  #{(Supplier. "S1" "Smith" 20 "London")
    (Supplier. "S2" "Jones" 10 "Paris")
    (Supplier. "S3" "Blake" 30 "Paris")})
(def parts
  #{(Part. "P1" "Nut" "Red" 12.0 "London")
    (Part. "P2" "Bolt" "Green" 17.0 "Paris")
    (Part. "P3" "Screw" "Blue" 17.0 "Oslo")})
(def shipments
  #{(Shipment. "S1" "P1" 300)
    (Shipment. "S2" "P2" 200)
    (Shipment. "S2" "P3" 400)})
 
;; helper fns
(defn tuplify
  "Returns a vector of the vals at keys ks in map."
  [m ks]
  (mapv #(get m %) ks))
(defn maps->rel
  "Returns the tuplification by ks of x, a collection
   of items that support key lookup"
  [x ks]
  (mapv #(tuplify % ks) x))
 
;; working directly with Clojure defrecords
(q '[:find ?name
     :where ["Paris" ?name]]
   (maps->rel suppliers [:city :name]))
 
(q '[:find ?name
     :in $suppliers $shipments
     :where
     [$suppliers ?supplier ?name "Paris"]
     [$shipments ?supplier]]
   (maps->rel suppliers [:number :name :city])
   (maps->rel shipments [:supplier]))

