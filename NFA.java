import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class NFA {
    public NFAState startState = null;
    private List<NFAState> allStates = new ArrayList<>();
    NFA() {
        newState();
    }

    Object newState() {
        NFAState s = new NFAState();
        if (startState == null) {
            startState = s;
            s.isStart = true;
        }
        allStates.add(s);
        return s;
    }

    void newTransition(Object start, char c, Object end) {
        ((NFAState) start).addTransition(c, (NFAState) end);
    }

    void makeFinal(Object s) {
        if (!allStates.contains((NFAState) s))
            throw new RuntimeException();
        ((NFAState) s).isFinal = true;
    }


    NFA(Regex re) {
        if (re instanceof RChar) {
            newState();
            NFAState finalState = (NFAState) newState();
            makeFinal(finalState);
            this.newTransition(startState, ((RChar) re).c, finalState);
        } else if (re instanceof ROr) {
            newState();
            NFA leftNFA = new NFA (((ROr) re).left);
            NFA rightNFA = new NFA (((ROr) re).right);
            this.newTransition(startState, '#', leftNFA.startState);
            leftNFA.startState.isStart = false;
            this.newTransition(startState, '#', rightNFA.startState);
            rightNFA.startState.isStart = false;
            for (NFAState s: leftNFA.allStates)
                allStates.add(s);
            for (NFAState s: rightNFA.allStates)
                allStates.add(s);
        } else if (re instanceof RSeq) {
            NFA leftNFA = new NFA (((RSeq) re).left);
            NFA rightNFA = new NFA (((RSeq) re).right);
            leftNFA.unmakeFinalAndConnect(rightNFA.startState);
            rightNFA.startState.isStart = false;
            for (NFAState s: leftNFA.allStates)
                allStates.add(s);
            for (NFAState s: rightNFA.allStates)
                allStates.add(s);
            startState = leftNFA.startState;
        } else if (re instanceof RStar) {
            newState();
            NFA star = new NFA(((RStar) re).re);
            newTransition(startState, '#', star.startState);
            star.startState.isStart = false;
            star.unmakeFinalAndConnect(startState);
            for (NFAState s: star.allStates)
                allStates.add(s);
            makeFinal(startState);
        }

    }

    public List<Object> states() {
        List<Object> ret = new ArrayList<>();
        for (NFAState s: allStates) {
            ret.add((Object) s);
        }
        return ret;
    }

    public Object start_state() {
        return (Object) startState;
    }

    public List<Object> final_states() {
        List<Object> finalStates = new ArrayList<>();
        for (NFAState s: allStates)
            if (s.isFinal)
                finalStates.add(s);
        return finalStates;
    }

    public void unmakeFinalAndConnect(NFAState nextStartState) {
        List<Object> finalStates = final_states();
        for (Object f: finalStates) {
            ((NFAState) f).isFinal = false;
            newTransition(f, '#', nextStartState);
        }
    }

    public List<Map.Entry<Character, Object>> transition(Object state) {
        NFAState s = (NFAState) state;
        return s.getAllNextStates();
    }

    private class RecursiveMatch extends RecursiveTask<Boolean> {
        private String s;
        private NFAState currState;
        private List<NFAState> onEmpty;
        private List <NFAState> onC;
        private int Threshold;
        private HashMap<String, AtomicBoolean> memo;


        RecursiveMatch(String s, NFAState currState, int threshold, List<NFAState> onEmpty, List<NFAState> onC, HashMap memo) {
            this.s = s;
            this.currState = currState;
            this.Threshold = threshold;
            this.onEmpty = onEmpty;
            this.onC = onC;
            this.memo = memo;
        }

        protected Boolean compute() {
            String k = s + currState.toString();
            if (memo.containsKey(k))
                return memo.get(k).get();
            if ((onEmpty != null) && (onEmpty.size() > Threshold)) {
                return ForkJoinTask.invokeAll(createSubTasksOnEmpty()).stream().anyMatch(n -> n.compute() == true);
            } else if ((onC != null) && (onC.size() > Threshold)) {
                return ForkJoinTask.invokeAll(createSubTasksOnC()).stream().anyMatch(n -> n.compute() == true);
            } else {
                memo.put(k, new AtomicBoolean());
                if (s.length() == 0) {
                    if (currState.isFinal) {
                        memo.get(k).set(true);
                        return true;
                    }
                    if (onEmpty != null) {
                        for (NFAState nextState : onEmpty) {
                            RecursiveMatch rm = new RecursiveMatch("", nextState, Threshold, nextState.nextStates.get('#'), null, memo);
                            if (rm.compute()) {
                                memo.get(k).set(true);
                                return true;
                            }
                        }
                    }
                } else {
                    if (onC != null)
                    {
                        for (NFAState nextState: onC) {
                            RecursiveMatch rm = new RecursiveMatch(s.substring(1), nextState, Threshold, nextState.nextStates.get('#'), nextState.nextStates.get(s.charAt(0)), memo);
                            if (rm.compute()) {
                                memo.get(k).set(true);
                                return true;
                            }
                        }
                    }
                    if (onEmpty != null) {
                        for (NFAState nextState : onEmpty) {
                            RecursiveMatch rm = new RecursiveMatch(s, nextState, Threshold, nextState.nextStates.get('#'), nextState.nextStates.get(s.charAt(0)), memo);
                            if (rm.compute()) {
                                memo.get(k).set(true);
                                return true;
                            }
                        }
                    }
                }
                memo.get(k).set(false);
                return false;
            }
        }

        private List<RecursiveMatch> createSubTasksOnEmpty () {
            List<List<NFAState>> subListOnEmpty = chopped(onEmpty, onEmpty.size()/2);
            List<RecursiveMatch> dividedTasks = new ArrayList<>();
            dividedTasks.add(new RecursiveMatch(s, currState, Threshold, subListOnEmpty.get(0), onC, memo));
            dividedTasks.add(new RecursiveMatch(s, currState, Threshold, subListOnEmpty.get(1), onC, memo));
            return dividedTasks;
        }

        private List<RecursiveMatch> createSubTasksOnC () {
            List<List<NFAState>> subListOnC = chopped(onC, onC.size()/2);
            List<RecursiveMatch> dividedTasks = new ArrayList<>();
            dividedTasks.add(new RecursiveMatch(s, currState, Threshold, onEmpty, subListOnC.get(0), memo));
            dividedTasks.add(new RecursiveMatch(s, currState, Threshold, onEmpty, subListOnC.get(1), memo));
            return dividedTasks;
        }

        private <T> List<List<T>> chopped(List<T> list, final int subListSize) {
            List<List<T>> parts = new ArrayList<>();
            final int N = list.size();
            for (int i = 0; i < N; i += subListSize) {
                parts.add(new ArrayList<T>(
                        list.subList(i, Math.min(N, i + subListSize)))
                );
            }
            return parts;
        }
    }


    boolean match(String s, int nthreads) {
        RecursiveMatch rm;
        ForkJoinPool pool = new ForkJoinPool(nthreads);
        HashMap<String, AtomicBoolean> memo = new HashMap<>();
        if (s == "") {
            rm = new RecursiveMatch(s, startState, 4, startState.nextStates.get('#'), null, memo);
        }
        else {
            rm = new RecursiveMatch(s, startState, 4, startState.nextStates.get('#'), startState.nextStates.get(s.charAt(0)), memo);
        }
        return pool.invoke(rm);
    }

}

