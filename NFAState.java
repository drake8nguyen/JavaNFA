import java.util.*;

public class NFAState {
    public boolean isFinal = false;
    public boolean isStart = false;
    public HashMap<Character, List<NFAState>> nextStates = new HashMap<>();

    public NFAState() {}

    public void addTransition(Character c, NFAState nextState) {
        if (!(('a' <= c && c <= 'z') || c == '#')) {
            throw new RuntimeException();
        }
        if (!nextStates.containsKey(c))
            nextStates.put(c, new ArrayList<>()); // can I do get and then new?
        nextStates.get(c).add(nextState);
    }

    static class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;
        public MyEntry(final K key) {
            this.key = key;
        }
        public MyEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    public List<Map.Entry<Character, Object>> getAllNextStates () { // is there key value pair in java
        List <Map.Entry<Character, Object>> allNextStates = new ArrayList<>();
        for (Map.Entry<Character, List<NFAState>> entry: nextStates.entrySet()) {
            for (NFAState state: entry.getValue()) {
                allNextStates.add(new MyEntry<Character, Object>(entry.getKey(), (Object) state));
            }
        }
        return allNextStates;
    }

    public void printDetails() {
        if (isStart) {
            System.out.println("This is the start state");
        }
        for (Character c: nextStates.keySet()) {
            System.out.println(c);
        }
        if (isFinal) {
            System.out.println("This is a final state");
        }
    }
}
