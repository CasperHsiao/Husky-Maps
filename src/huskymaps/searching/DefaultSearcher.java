package huskymaps.searching;

import autocomplete.Autocomplete;
import autocomplete.DefaultTerm;
import autocomplete.Term;
import huskymaps.graph.Node;
import huskymaps.graph.StreetMapGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see Searcher
 */
public class DefaultSearcher extends Searcher {
    private Term[] terms;
    private List<Node> allNodes;
    private List<String> allNames;

    public DefaultSearcher(StreetMapGraph graph) {
        int size = graph.allNodes().size();
        List<Term> temp = new ArrayList<Term>();
        this.allNodes = new ArrayList<Node>();
        this.allNames = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            Node node = graph.allNodes().get(i);
            if (node.name() != null) {
                Term term = createTerm(node.name(), node.importance());
                temp.add(term);
                this.allNodes.add(node);
                this.allNames.add(node.name());
            }
        }
        this.terms = temp.toArray(Term[]::new);
    }

    @Override
    protected Term createTerm(String name, int weight) {
        return new DefaultTerm(name, weight);
    }

    @Override
    protected Autocomplete createAutocomplete(Term[] termsArray) {
        return new Autocomplete(termsArray);
    }

    @Override
    public List<String> getLocationsByPrefix(String prefix) {
        Autocomplete temp = createAutocomplete(this.terms);
        Term[] result = temp.findMatchesForPrefix(prefix);
        List<String> list = new ArrayList<String>();
        for (Term t : result) {
            list.add(t.query());
        }
        Set<String> set = new HashSet<String>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    @Override
    public List<Node> getLocations(String locationName) {
        if (!this.allNames.contains(locationName)) {
            throw new IllegalArgumentException();
        }
        int index = this.allNames.indexOf(locationName);
        boolean next = true;
        List<Node> result = new ArrayList<Node>();
        while (next) {
            result.add(this.allNodes.get(index));
            index++;
            if (this.allNames.get(index).equals(locationName)) {
                next = true;
            } else {
                next = false;
            }
        }
        return result;
    }
}
