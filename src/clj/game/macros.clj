(ns game.macros)

(defmacro effect [& expr]
  `(fn ~['state 'side 'card 'targets]
     ~(let [actions (map #(if (#{:runner :corp} (second %))
                            (concat [(first %) 'state (second %)] (drop 2 %))
                            (concat [(first %) 'state 'side] (rest %)))
                         expr)]
        `(let ~['runner '(:runner @state)
                'corp '(:corp @state)
                'corp-reg '(get-in @state [:corp :register])
                'runner-reg '(get-in @state [:runner :register])
                'current-ice '(when-let [run (:run @state)]
                                (when (> (:position run) 0) ((:ices run) (dec (:position run)))))
                'target '(first targets)]
           ~@actions))))

(defmacro req [& expr]
  `(fn ~['state 'side 'card 'targets]
     (let ~['runner '(:runner @state)
            'corp '(:corp @state)
            'run '(:run @state)
            'current-ice '(when (and run (> (:position run) 0)) ((:ices run) (dec (:position run))))
            'access '(:access @state)
            'corp-reg '(get-in @state [:corp :register])
            'runner-reg '(get-in @state [:runner :register])
            'target '(first targets)
            'installed '(#{:rig :servers} (first (:zone card)))
            'remotes '(map #(str "Server " %) (range (count (get-in corp [:servers :remote]))))
            'servers '(concat ["HQ" "R&D" "Archives"] remotes)
            'tagged '(> (get-in @state [:runner :tag]) 0)
            'this-server '(let [s (-> card :zone rest butlast)
                                r (:server run)]
                            (and (= (first r) (first s))
                                 (= (last r) (last s))))]
        ~@expr)))

(defmacro msg [& expr]
  `(fn ~['state 'side 'card 'targets]
     (let ~['runner '(:runner @state)
            'corp '(:corp @state)
            'corp-reg '(get-in @state [:corp :register])
            'runner-reg '(get-in @state [:runner :register])
            'run '(:run @state)
            'current-ice '(when (and run (> (:position run) 0)) ((:ices run) (dec (:position run))))
            'target '(first targets)]
       (str ~@expr))))
