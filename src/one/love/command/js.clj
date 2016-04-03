(ns one.love.command.js
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.walk :as walk]))

; Original Source Code: https://github.com/kriyative/clojurejs/blob/master/src/clojurejs/js.clj
;
; Eclipse Public License - v 1.0

; THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.

; 1. DEFINITIONS

; "Contribution" means:

; a) in the case of the initial Contributor, the initial code and documentation distributed under this Agreement, and
; b) in the case of each subsequent Contributor:
; i) changes to the Program, and
; ii) additions to the Program;
; where such changes and/or additions to the Program originate from and are distributed by that particular Contributor. A Contribution 'originates' from a Contributor if it was added to the Program by such Contributor itself or anyone acting on such Contributor's behalf. Contributions do not include additions to the Program which: (i) are separate modules of software distributed in conjunction with the Program under their own license agreement, and (ii) are not derivative works of the Program.
; "Contributor" means any person or entity that distributes the Program.

; "Licensed Patents" mean patent claims licensable by a Contributor which are necessarily infringed by the use or sale of its Contribution alone or when combined with the Program.

; "Program" means the Contributions distributed in accordance with this Agreement.

; "Recipient" means anyone who receives the Program under this Agreement, including all Contributors.

; 2. GRANT OF RIGHTS

; a) Subject to the terms of this Agreement, each Contributor hereby grants Recipient a non-exclusive, worldwide, royalty-free copyright license to reproduce, prepare derivative works of, publicly display, publicly perform, distribute and sublicense the Contribution of such Contributor, if any, and such derivative works, in source code and object code form.
; b) Subject to the terms of this Agreement, each Contributor hereby grants Recipient a non-exclusive, worldwide, royalty-free patent license under Licensed Patents to make, use, sell, offer to sell, import and otherwise transfer the Contribution of such Contributor, if any, in source code and object code form. This patent license shall apply to the combination of the Contribution and the Program if, at the time the Contribution is added by the Contributor, such addition of the Contribution causes such combination to be covered by the Licensed Patents. The patent license shall not apply to any other combinations which include the Contribution. No hardware per se is licensed hereunder.
; c) Recipient understands that although each Contributor grants the licenses to its Contributions set forth herein, no assurances are provided by any Contributor that the Program does not infringe the patent or other intellectual property rights of any other entity. Each Contributor disclaims any liability to Recipient for claims brought by any other entity based on infringement of intellectual property rights or otherwise. As a condition to exercising the rights and licenses granted hereunder, each Recipient hereby assumes sole responsibility to secure any other intellectual property rights needed, if any. For example, if a third party patent license is required to allow Recipient to distribute the Program, it is Recipient's responsibility to acquire that license before distributing the Program.
; d) Each Contributor represents that to its knowledge it has sufficient copyright rights in its Contribution, if any, to grant the copyright license set forth in this Agreement.
; 3. REQUIREMENTS

; A Contributor may choose to distribute the Program in object code form under its own license agreement, provided that:

; a) it complies with the terms and conditions of this Agreement; and
; b) its license agreement:
; i) effectively disclaims on behalf of all Contributors all warranties and conditions, express and implied, including warranties or conditions of title and non-infringement, and implied warranties or conditions of merchantability and fitness for a particular purpose;
; ii) effectively excludes on behalf of all Contributors all liability for damages, including direct, indirect, special, incidental and consequential damages, such as lost profits;
; iii) states that any provisions which differ from this Agreement are offered by that Contributor alone and not by any other party; and
; iv) states that source code for the Program is available from such Contributor, and informs licensees how to obtain it in a reasonable manner on or through a medium customarily used for software exchange.
; When the Program is made available in source code form:

; a) it must be made available under this Agreement; and
; b) a copy of this Agreement must be included with each copy of the Program.
; Contributors may not remove or alter any copyright notices contained within the Program.

; Each Contributor must identify itself as the originator of its Contribution, if any, in a manner that reasonably allows subsequent Recipients to identify the originator of the Contribution.

; 4. COMMERCIAL DISTRIBUTION

; Commercial distributors of software may accept certain responsibilities with respect to end users, business partners and the like. While this license is intended to facilitate the commercial use of the Program, the Contributor who includes the Program in a commercial product offering should do so in a manner which does not create potential liability for other Contributors. Therefore, if a Contributor includes the Program in a commercial product offering, such Contributor ("Commercial Contributor") hereby agrees to defend and indemnify every other Contributor ("Indemnified Contributor") against any losses, damages and costs (collectively "Losses") arising from claims, lawsuits and other legal actions brought by a third party against the Indemnified Contributor to the extent caused by the acts or omissions of such Commercial Contributor in connection with its distribution of the Program in a commercial product offering. The obligations in this section do not apply to any claims or Losses relating to any actual or alleged intellectual property infringement. In order to qualify, an Indemnified Contributor must: a) promptly notify the Commercial Contributor in writing of such claim, and b) allow the Commercial Contributor to control, and cooperate with the Commercial Contributor in, the defense and any related settlement negotiations. The Indemnified Contributor may participate in any such claim at its own expense.

; For example, a Contributor might include the Program in a commercial product offering, Product X. That Contributor is then a Commercial Contributor. If that Commercial Contributor then makes performance claims, or offers warranties related to Product X, those performance claims and warranties are such Commercial Contributor's responsibility alone. Under this section, the Commercial Contributor would have to defend claims against the other Contributors related to those performance claims and warranties, and if a court requires any other Contributor to pay any damages as a result, the Commercial Contributor must pay those damages.

; 5. NO WARRANTY

; EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Each Recipient is solely responsible for determining the appropriateness of using and distributing the Program and assumes all risks associated with its exercise of rights under this Agreement , including but not limited to the risks and costs of program errors, compliance with applicable laws, damage to or loss of data, programs or equipment, and unavailability or interruption of operations.

; 6. DISCLAIMER OF LIABILITY

; EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, NEITHER RECIPIENT NOR ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

; 7. GENERAL

; If any provision of this Agreement is invalid or unenforceable under applicable law, it shall not affect the validity or enforceability of the remainder of the terms of this Agreement, and without further action by the parties hereto, such provision shall be reformed to the minimum extent necessary to make such provision valid and enforceable.

; If Recipient institutes patent litigation against any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Program itself (excluding combinations of the Program with other software or hardware) infringes such Recipient's patent(s), then such Recipient's rights granted under Section 2(b) shall terminate as of the date such litigation is filed.

; All Recipient's rights under this Agreement shall terminate if it fails to comply with any of the material terms or conditions of this Agreement and does not cure such failure in a reasonable period of time after becoming aware of such noncompliance. If all Recipient's rights under this Agreement terminate, Recipient agrees to cease use and distribution of the Program as soon as reasonably practicable. However, Recipient's obligations under this Agreement and any licenses granted by Recipient relating to the Program shall continue and survive.

; Everyone is permitted to copy and distribute copies of this Agreement, but in order to avoid inconsistency the Agreement is copyrighted and may only be modified in the following manner. The Agreement Steward reserves the right to publish new versions (including revisions) of this Agreement from time to time. No one other than the Agreement Steward has the right to modify this Agreement. The Eclipse Foundation is the initial Agreement Steward. The Eclipse Foundation may assign the responsibility to serve as the Agreement Steward to a suitable separate entity. Each new version of the Agreement will be given a distinguishing version number. The Program (including Contributions) may always be distributed subject to the version of the Agreement under which it was received. In addition, after a new version of the Agreement is published, Contributor may elect to distribute the Program (including its Contributions) under the new version. Except as expressly stated in Sections 2(a) and 2(b) above, Recipient receives no rights or licenses to the intellectual property of any Contributor under this Agreement, whether expressly, by implication, estoppel or otherwise. All rights in the Program not expressly granted under this Agreement are reserved.

; This Agreement is governed by the laws of the State of New York and the intellectual property laws of the United States of America. No party to this Agreement will bring a legal action under this Agreement more than one year after the cause of action arose. Each party waives its rights to a jury trial in any resulting litigation.


(defmacro assert-args [fnname & pairs]
  `(do (when-not ~(first pairs)
         (throw (IllegalArgumentException.
                 ~(str fnname " requires " (second pairs)))))
       ~(let [more (nnext pairs)]
          (when more
            (list* `assert-args fnname more)))))

(defn- sexp-reader [source]
  "Wrap `source' in a reader suitable to pass to `read'."
  (new java.io.PushbackReader (io/reader source)))

(defn- unzip [s]
  (let [parts (partition 2 s)]
    [(into (empty s) (map first parts))
     (into (empty s) (map second parts))]))

;; (= (unzip [:foo 1 :bar 2 :baz 3]) [[:foo :bar :baz] [1 2 3]])

(defn- re? [expr] (= (class expr) java.util.regex.Pattern))

(def ^:dynamic *inline-if* false)
(def ^:dynamic *quoted* false)

(def ^:dynamic *print-pretty* false)
(defmacro with-pretty-print [& body]
  `(binding [*print-pretty* true]
     ~@body))

(def ^:dynamic *indent* 0)
(defmacro with-indent [[& increment] & body]
  `(binding [*indent* (+ *indent* (or ~increment 4))]
     ~@body))

(def ^:dynamic *in-block-exp* false)
(defmacro with-block [& body]
  `(binding [*in-block-exp* true]
     ~@body))

(defn- newline-indent []
  (if *print-pretty*
    (do
      (newline)
      (print (apply str (repeat *indent* " "))))
    (print " ")))

(defmacro with-parens [[& [left right]] & body]
  `(do
     (print (or ~left "("))
     ~@body
     (print (or ~right ")"))))

(defn- jskey [x]
  (let [x (if (and (coll? x) (seq x)) (first x) x)]
    (if (symbol? x) (name x) x)))

(defn- dotsymbol? [s]
  (and (symbol? s) (.startsWith (name s) ".")))

(declare emit-str)

(defn- sym->property [s]
  "Transforms symbol or keyword into property access form."
  (binding [*quoted* true]
    (emit-str
      (if (dotsymbol? s)
        (symbol (subs (name s) 1))
        s))))

(defmulti emit jskey)

(defn- emit-delimited [delimiter args & [emitter]]
  (when-not (empty? args)
    ((or emitter emit) (first args))
    (doseq [arg (rest args)]
      (print delimiter)
      ((or emitter emit) arg))))

(defn- emit-map [expr]
  (with-parens ["{" "}"]
    (binding [*inline-if* true]
      (emit-delimited ","
                      (seq expr)
                      (fn [[key val]]
                        (emit key)
                        (print " : ")
                        (emit val))))))

(defn- emit-set [expr]
  (with-parens ["{" "}"]
    (binding [*inline-if* true]
      (emit-delimited ","
                      (seq expr)
                      (fn [key]
                        (emit key)
                        (print " : true"))))))

(defn- emit-vector [expr]
  (with-parens ["[" "]"]
    (binding [*inline-if* true]
      (emit-delimited "," (seq expr)))))

(defn- emit-re [expr]
  (print (str "/" (apply str (replace {\/ "\\/"} (str expr))) "/")))

(defn- emit-symbol [expr]
  (if *quoted* (print "'"))
  (print
   (if *quoted*
     (name expr)
     (apply str (replace {\- "_" \* "__" \? "p" \! "f" \= "_eq"} (name expr)))))
  (if *quoted* (print "'")))

(defn- emit-keyword [expr]
  (binding [*quoted* true]
    (emit-symbol expr)))

(defn- unary-operator? [op]
  (and (symbol? op) (contains? #{"++" "--" "!"} (name op))))

(defn- emit-unary-operator [op arg]
  (print (name op))
  (emit arg))

(defn- infix-operator? [op]
  (and (symbol? op)
       (contains? #{"and" "or" "+" "-" "/" "*" "%"
                    ">" ">=" "<" "<=" "==" "===" "!=" "!=="
                    "instanceof"}
                  (name op))))

(defn- emit-infix-operator [op & args]
  (let [lisp->js {"and" "&&"
                  "or" "||"}
        js-op (get lisp->js (name op) (name op))]
    (with-parens []
      (emit-delimited (str " " js-op " ") args))))

(defn- emit-function-call [fun & args]
  (emit fun)
  (with-parens []
    (with-indent [] (emit-delimited ", " args))))

(defn- emit-method-call [recvr selector & args]
  (emit recvr)
  (emit selector)
  (with-parens []
    (with-indent [] (emit-delimited ", " args))))

(def ^:dynamic *return-expr* false)
(defmacro with-return-expr [[& [new-val]] & body]
  `(binding [*return-expr* (if *return-expr*
                             (do
                               (print "return ")
                               false)
                             (or ~new-val false))]
     ~@body))  

(def ^:dynamic *in-fn-toplevel* true)
(defn- emit-function-form [form]
  (binding [*inline-if* true
            *in-fn-toplevel* false]
    (let [[fun & args] form
          method? (fn [f] (and (symbol? f) (= \. (first (name f)))))
          invoke-method (fn [[sel recvr & args]]
                          (apply emit-method-call recvr sel args))
          new-object? (fn [f] (and (symbol? f) (= \. (last (name f)))))
          invoke-fun (fn [fun & args]
                       (with-parens [] (emit fun))
                       (with-parens [] (emit-delimited "," args)))]
      (cond
       (unary-operator? fun) (apply emit-unary-operator form)
       (infix-operator? fun) (apply emit-infix-operator form)
       (keyword? fun) (let [[map & default] args] (emit `(get ~map ~fun ~@default)))
       (method? fun) (invoke-method form)
       (new-object? fun) (emit `(new ~(symbol (apply str (drop-last (str fun)))) ~@args))
       (coll? fun) (apply invoke-fun form)
       true (apply emit-function-call form)))))

(defn emit-statement [expr]
  (binding [*inline-if* false]
    (if (and (coll? expr) (= 'defmacro (first expr))) ; cracks are showing
      (emit expr)
      (do
        (newline-indent)
        (emit expr)
        (print ";")))))

(defn emit-statements [exprs]
  (doseq [expr exprs]
    (emit-statement expr)))

(defn emit-statements-with-return [exprs]
  (binding [*return-expr* false]
    (doseq [expr (butlast exprs)]
      (emit-statement expr)))
  (emit-statement (last exprs)))

(defmethod emit "def" [[_ name value]]
  (print "var ")
  (emit-symbol name)
  (print " = ")
  (binding [*inline-if* true]
    (emit value)))

(def ^:dynamic *macros* (ref {}))
(defn- macro? [n] (and (symbol? n) (contains? @*macros* (name n))))
(defn- get-macro [n] (and (symbol? n) (get @*macros* (name n))))
(defn- undef-macro [n]
  (when (macro? n)
    (when *print-pretty* (println "// undefining macro" n))
    (dosync (alter *macros* dissoc (name n)))))

(defmethod emit "defmacro" [[_ mname args & body]]
  (dosync
   (alter *macros*
          conj
          {(name mname) (eval `(clojure.core/fn ~args ~@body))}))
  nil)

(defn- emit-macro-expansion [form]
  (let [[mac-name & args] form
        mac (get-macro mac-name)
        macex (apply mac args)]
    (emit macex)))

(defn- emit-docstring [docstring]
  (when *print-pretty*
    (let [lines (string/split-lines docstring)]
      (newline-indent)
      (print (str "/* " (first lines)))
      (doseq [line (rest lines)]
        (newline-indent)
        (print (str "   " line)))
      (print " */"))))

(defn- ignorable-arg? [n]
  (and (symbol? n) (.startsWith (name n) "_")))

(def ^:dynamic *temp-sym-count* nil)
(defn tempsym []
  (dosync
   (ref-set *temp-sym-count* (+ 1 @*temp-sym-count*))
   (symbol (str "_temp_" @*temp-sym-count*))))

(defn- emit-simple-binding [vname val]
  (emit (if (ignorable-arg? vname) (tempsym) vname))
  (print " = ")
  (binding [*inline-if* true]
    (emit val)))

(declare emit-var-bindings
         emit-destructured-seq-binding
         emit-destructured-map-binding)

(defn- destructuring-form? [form]
  (or (map? form) (vector? form)))

(defn- binding-form? [form]
  (or (symbol? form) (destructuring-form? form)))

(defn- binding-special? [form]
  (contains? #{'& :as} form))

(defn- emit-binding [vname val]
  (binding [*inline-if* true]
    (let [emitter (cond
                   (vector? vname) emit-destructured-seq-binding
                   (map? vname)    emit-destructured-map-binding
                   :else           emit-simple-binding)]
      (emitter vname val))))

(defn- emit-destructured-seq-binding [vvec val]
  (let [temp (tempsym)]
    (print (str temp " = "))
    (emit val)
    (loop [vseq vvec, i 0, seen-rest? false]
      (when (seq vseq)
        (let [vname (first vseq)
              vval  (second vseq)]
          (print ", ")
          (condp = vname
            '&  (cond
                  seen-rest?
                    (throw (Exception. "Unsupported binding form, only :as can follow &"))
                  (not (symbol? vval))
                    (throw (Exception. "Unsupported binding form, & must be followed by exactly one symbol"))
                  :else
                    (do (emit-binding vval `(.slice ~temp ~i))
                        (recur (nnext vseq) (inc i) true)))
            :as (cond
                  (not= (count (nnext vseq)) 0)
                    (throw (Exception. "Unsupported binding form, nothing must follow after :as <binding>"))
                  (not (symbol? vval))
                    (throw (Exception. "Unsupported binding form, :as must be followed by a symbol"))
                  :else
                    (emit-binding vval temp))
            (do (emit-binding vname `(get ~temp ~i))
                (recur (next vseq) (inc i) seen-rest?))))))))

(defn- emit-destructured-map-binding [vmap val]
  (let [temp     (tempsym)
        defaults (get vmap :or)
        keysmap  (reduce #(assoc %1 %2 (keyword %2))
                  {}
                  (mapcat vmap [:keys :strs :syms]))
        vmap     (merge (dissoc vmap :or :keys :strs :syms) keysmap)]
    (print (str temp " = "))
    (emit val)
    (doseq [[vname vkey] vmap]
      (print ", ")
      (cond
        (not (and (binding-form? vname)
                  (or (some #(% vkey) #{keyword? number? binding-form?}))))
          (throw (Exception. "Unsupported binding form, binding symbols must be followed by keywords or numbers"))

        :else
          (if-let [[_ default] (find defaults vname)]
            (emit-binding vname `(get ~temp ~vkey ~default))
            (emit-binding vname `(get ~temp ~vkey)))))))

(defn- emit-var-bindings [bindings]
  (binding [*return-expr* false]
    (emit-delimited
      ", "
      (partition 2 bindings)
      (fn [[vname val]]
        (emit-binding vname val)))))

(defn- emit-function [fdecl]
  (let [docstring (if (string? (first fdecl))
                    (first fdecl)
                    nil)
        fdecl     (if (string? (first fdecl))
                    (rest fdecl)
                    fdecl)
        args      (first fdecl)
        dargs?    (or (some destructuring-form? args)
                      (some binding-special? args)
                      (some ignorable-arg? args))
        body      (rest fdecl)]
    (assert-args fn
      (vector? args) "a vector for its bindings")
    (if dargs?
      (do
        (print "function () {")
        (with-indent []
          (newline-indent)
          (print "var ")
          (emit-binding args '(Array.prototype.slice.call arguments))
          (print ";")))
      (do
        (print "function (")
        (binding [*return-expr* false] (emit-delimited ", " args))
        (print ") {")))
    (with-indent []
      (when docstring
        (emit-docstring docstring))
      (binding [*return-expr* true]
        (emit-statements-with-return body)))
    (newline-indent)
    (print "}")))

(defmethod emit "fn" [[_ & fdecl]]
  (with-return-expr []
    (with-block (emit-function fdecl))))

(defmethod emit "defn" [[_ name & fdecl]]
  (assert-args defn (symbol? name) "a symbol as its name")
  (undef-macro name)
  (emit-symbol name)
  (print " = ")
  (with-block
    (emit-function fdecl)))

(defmethod emit "if" [[_ test consequent & [alternate]]]
  (let [emit-inline-if (fn []
                         (with-return-expr []
                           (with-parens []
                             (emit test)
                             (print " ? ")
                             (emit consequent)
                             (print " : ")
                             (emit alternate))))
        emit-block-if (fn []
                        (print "if (")
                        (binding [*return-expr* false
                                  *inline-if* true]
                          (emit test))
                        (print ") {")
                        (with-block
                          (with-indent []
                            (emit-statement consequent)))
                        (newline-indent)
                        (print "}")
                        ;; alternate might be `0`, which js equates as `nil`
                        (when-not (nil? alternate)
                          (print " else {")
                          (with-block
                            (with-indent []
                              (emit-statement alternate)))
                          (newline-indent)
                          (print "}")))]
    (if (and *inline-if* consequent)
      (emit-inline-if)
      (emit-block-if))))

(defmethod emit "do" [[_ & exprs]]
  (if *inline-if*
    (do
      (print "(function(){")
      (binding [*return-expr* true]
        (emit-statements-with-return exprs))
      (print "})()"))
    (emit-statements-with-return exprs)))

(defmethod emit "let" [[_ bindings & exprs]]
  (let [emit-var-decls (fn []
                         (print "var ")
                         (binding [*return-expr* false]
                           (with-block (emit-var-bindings bindings))
                           (print ";"))
                         (emit-statements-with-return exprs))]
    (if (or (not *in-fn-toplevel*) *inline-if*)
      (with-return-expr []
        (print "(function () {")
        (with-indent []
          (newline-indent)
          (binding [*return-expr* true]
            (emit-var-decls)))
        (newline-indent)
        (print " }).call(this)"))
      (binding [*in-fn-toplevel* false]
        (emit-var-decls)))))

(defmethod emit "new" [[_ class & args]]
  (with-return-expr []
    (binding [*inline-if* true]
      (print "new ")
      (emit class)
      (with-parens [] (emit-delimited "," args)))))

(defmethod emit "return" [[_ value]]
  (print "return ")
  (emit value))

(defmethod emit 'nil [_]
  (with-return-expr []
    (print "null")))

(defmethod emit "get" [args]
  (let [[_ map key default]  args
        default? (> (count args) 3)
        emit-get
          (fn []
            (emit map)
            (if (dotsymbol? key)
              (emit key)
              (do
                (print "[")
                (emit key)
                (print "]"))))]
    (with-return-expr []
      (if default?
        ;; FIXME Should be able to re-use code for
        ;; inline if and contains? macro here.
        ;; FIXME Also, `map` will be evaluated twice (once in
        ;; the `in` test, and once in output of `emit-get`
        (with-parens []
          (print (sym->property key))
          (print " in ")
          (emit map)
          (print " ? ")
          (emit-get)
          (print " : ")
          (emit default))
        (emit-get)))))

(defmethod emit "set!" [[_ & apairs]]
  (binding [*return-expr* false
            *in-fn-toplevel* false
            *inline-if* true]
    (let [apairs (partition 2 apairs)]
      (emit-delimited " = " (first apairs))
      (doseq [apair (rest apairs)]
        (print ";")
        (newline-indent)
        (emit-delimited " = " apair)))))

(defmethod emit "try" [[_ expr & clauses]]
  (print "try {")
  (with-indent []
    (with-block
      (emit-statement expr)))
  (newline-indent)
  (print "}")
  (doseq [[clause & body] clauses]
    (cond (= clause 'catch)
          (let [[evar expr] body]
            (with-block
              (print " catch (")
              (emit-symbol evar)
              (print ") {")
              (with-indent [] (emit-statement expr))
              (newline-indent)
              (print "}")))

          (= clause 'finally)
          (with-block
            (print " finally {")
            (with-indent [] (doseq [expr body] (emit-statement expr)))
            (newline-indent)
            (print "}")))))

(def ^:dynamic *loop-vars* nil)
(defmethod emit "loop" [[_ bindings & body]]
  (let [emit-for-block (fn []
                         (print "for (var ")
                         (binding [*return-expr* false
                                   *in-block-exp* false]
                           (emit-var-bindings bindings))
                         (print "; true;) {")
                         (with-indent []
                           (binding [*loop-vars* (first (unzip bindings))]
                             (emit-statements-with-return body))
                           (newline-indent)
                           (print "break;"))
                         (newline-indent)
                         (print "}"))]
    (if (or (not *in-fn-toplevel*) *inline-if*)
      (with-return-expr []
        (print "(function () {")
        (binding [*return-expr* true]
          (with-indent []
            (newline-indent)
            (emit-for-block))
          (newline-indent))
        (print "}).call(this)"))
      (binding [*in-fn-toplevel* false]
        (emit-for-block)))))

(defmethod emit "recur" [[_ & args]]
  (binding [*return-expr* false]
    (let [tmp (tempsym)]
      (print "var" (emit-str tmp) "= ")
      (emit-vector args)
      (println ";")
      (emit-statements (map (fn [lvar i] `(set! ~lvar (get ~tmp ~i)))
                            *loop-vars*
                            (range (count *loop-vars*))))))
  (newline-indent)
  (print "continue"))

(defmethod emit "dokeys" [[_ [lvar hash] & body]]
  (binding [*return-expr* false]
    (print "for (var ")
    (emit lvar)
    (print " in ")
    (emit hash)
    (print ") {")
    (with-indent []
      (emit-statements body))
    (newline-indent)
    (print "}")))

(defmethod emit "inline" [[_ js]]
  (with-return-expr []
    (print js)))

(defmethod emit "quote" [[_ expr]]
  (binding [*quoted* true]
    (emit expr)))

(defmethod emit "throw" [[_ expr]]
  (binding [*return-expr* false]
    (print "throw ")
    (emit expr)))

(defmethod emit :default [expr]
  (if (and (coll? expr) (not *quoted*) (macro? (first expr)))
    (emit-macro-expansion expr)
    (with-return-expr []
      (cond
       (map? expr) (emit-map expr)
       (set? expr) (emit-set expr)
       (vector? expr) (emit-vector expr)
       (re? expr) (emit-re expr)
       (keyword? expr) (emit-keyword expr)
       (string? expr) (pr expr)
       (symbol? expr) (emit-symbol expr)
       (char? expr) (print (format "'%c'" expr))
       (and *quoted* (coll? expr)) (emit-vector expr)
       (coll? expr) (emit-function-form expr)
       true (print expr)))))

(defn emit-str [expr]
  (binding [*return-expr* false
            *inline-if* true]
    (with-out-str (emit expr))))

(defn js-emit [expr] (emit expr))

(defn pre-process [form]
  (walk/prewalk (fn [form]
                  (cond (= form '=)
                        '==

                        (and (list? form)
                             ('#{cond-> -> ->>} (first form)))
                        (macroexpand-1 form)

                        (and (list? form)
                             (symbol? (first form))
                             (.startsWith (name (first form)) ".-"))
                        (let [[field obj] form]
                          (symbol (str (pre-process obj) "." (subs (name field) 2))))

                        :else
                        form))
                form))

(defn js [& exprs]
  "Translate the Clojure subset `exprs' to a string of javascript code."
  (binding [*temp-sym-count* (ref 999)]
    (with-out-str
      (if (< 1 (count exprs))
        (emit-statements (pre-process exprs))
        (js-emit (pre-process (first exprs)))))))
