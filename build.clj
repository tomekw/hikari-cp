(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'hikari-cp/hikari-cp)
(def version "3.3.0")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]
                :scm {:url "https://github.com/tomekw/hikari-cp"
                      :connection "scm:git:git://github.com/tomekw/hikari-cp.git"
                      :developerConnection "scm:git:ssh://git@github.com/tomekw/hikari-cp.git"
                      :tag (str "v" version)}
                :pom-data [[:description "A Clojure wrapper to HikariCP JDBC connection pool"]
                           [:url "https://github.com/tomekw/hikari-cp"]
                           [:licenses
                            [:license
                             [:name "Eclipse Public License"]
                             [:url "http://www.eclipse.org/legal/epl-v10.html"]]]]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn deploy [_]
  (dd/deploy {:installer :remote
              :artifact jar-file
              :pom-file (b/pom-path {:lib lib :class-dir class-dir})}))
