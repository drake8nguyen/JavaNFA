import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
//        Regex re = new Parser("a").parse();
//        NFA nfa = new NFA(re);
//        for (NFAState state: nfa.allStates) {
//            System.out.println("STATE IN MACHINE");
//            List<Map.Entry<Character, Object>> transitions = nfa.transition(state);
//            state.printDetails();
//            for (Map.Entry<Character, Object> transition : transitions) {
//                System.out.println("The next states details:");
//                ((NFAState) transition.getValue()).printDetails();
//            }
//        }


//        Regex re = new Parser("a*").parse();
//        NFA nfa = new NFA(re);
//        System.out.println(nfa.allStates.size());
//        for (NFAState state: nfa.allStates) {
//            System.out.println("STATE IN MACHINE");
//            List<Map.Entry<Character, Object>> transitions = nfa.transition(state);
//            state.printDetails();
//            for (Map.Entry<Character, Object> transition : transitions) {
//                System.out.println("The next states details:");
//                ((NFAState) transition.getValue()).printDetails();
//            }
//        }
//        assert(nfa.allStates.size() == 3);


//        Regex re = new Parser("ab").parse();
//        NFA nfa = new NFA(re);
//        System.out.println(nfa.allStates.size());
//        for (NFAState state: nfa.allStates) {
//            System.out.println("STATE IN MACHINE");
//            List<Map.Entry<Character, Object>> transitions = nfa.transition(state);
//            state.printDetails();
//            for (Map.Entry<Character, Object> transition : transitions) {
//                System.out.println("The next states details:");
//                ((NFAState) transition.getValue()).printDetails();
//            }
//        }
//        assert(nfa.allStates.size() == 4);

//        Regex re = new Parser("ab*").parse();
//        NFA nfa = new NFA(re);
//        System.out.println(nfa.allStates.size());
//        for (NFAState state: nfa.allStates) {
//            System.out.println("STATE IN MACHINE");
//            List<Map.Entry<Character, Object>> transitions = nfa.transition(state);
//            state.printDetails();
//            for (Map.Entry<Character, Object> transition : transitions) {
//                System.out.println("The next states details:");
//                ((NFAState) transition.getValue()).printDetails();
//            }
//        }
//        assert(nfa.allStates.size() == 5);

//        Regex re = new Parser("ab*|c").parse();
//        NFA nfa = new NFA(re);
//        System.out.println(nfa.allStates.size());
//        for (NFAState state: nfa.allStates) {
//            System.out.println("STATE IN MACHINE");
//            List<Map.Entry<Character, Object>> transitions = nfa.transition(state);
//            state.printDetails();
//            for (Map.Entry<Character, Object> transition : transitions) {
//                System.out.println("The next states details:");
//                ((NFAState) transition.getValue()).printDetails();
//            }
//        }
//        assert(nfa.allStates.size() == 8);

        Regex re = new Parser("((a*b*)*)*").parse();
        String s = "abbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbbabbaabbb";
        NFA nfa = new NFA(re);
        if (nfa.match(s, 8)) {
            System.out.println("Success");
        } else
            System.out.println("Not a match");


        Regex re2 = new Parser("a*b*c*d*xxx|((ccdd*dd)*)*f*z*d*t*r*w*h*u*h*b*n*m*l*c|(aaaaaaaaaaaabbbbbbcccc*ccffffffffffrrrrrrr*rr)*").parse();
        String s2 = "aaaaaaaaaaaabbbbbbccccccffffffffffrrrrrrrrr";
        NFA nfa2 = new NFA(re2);
        if (nfa2.match(s2, 8)) {
            System.out.println("Success");
        } else
            System.out.println("Not a match");

    }

}
