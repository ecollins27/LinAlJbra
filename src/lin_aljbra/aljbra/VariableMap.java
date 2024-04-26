package lin_aljbra.aljbra;

import java.util.*;

public class VariableMap implements Map<Variable,Double>  {

    int numBuckets;
    VariableNode[] buckets;
    public VariableMap(){
        numBuckets = 11;
        buckets = new VariableNode[numBuckets];
    }

    public VariableMap(int initialSize){
        numBuckets = initialSize;
        buckets = new VariableNode[numBuckets];
    }

    private int hash(Variable variable){
        long product = 1;
        for (int i = 0; i < variable.name.length();i++){
            product *= variable.name.charAt(i);
        }
        return (int)(product % numBuckets);
    }

    @Override
    public int size() {
        int sum = 0;
        for (int i = 0; i < buckets.length;i++){
            if (buckets[i] != null){
                sum += buckets[i].size();
            }
        }
        return sum;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Double)){
            throw new RuntimeException("Value must be a Double");
        }
        for (int i = 0; i < numBuckets;i++){
            if (buckets[i] != null && buckets[i].containsValue((Double) value)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Double get(Object key) {
        if (!(key instanceof Variable)){
            throw new RuntimeException("Key must be a Variable");
        }
        int hash = hash((Variable) key);
        if (buckets[hash] == null){
            return null;
        }
        return buckets[hash].getValue((Variable) key);
    }

    @Override
    public Double put(Variable key, Double value) {
        if (!(key instanceof Variable)){
            throw new RuntimeException("Key must be a Variable");
        }
        int hash = hash((Variable) key);
        if (buckets[hash] == null){
            buckets[hash] = new VariableNode(key,value);
            return null;
        }
        return buckets[hash].addNext(key,value);
    }

    @Override
    public Double remove(Object key) {
        if (!(key instanceof Variable)){
            throw new RuntimeException("Key must be a Variable");
        }
        int hash = hash((Variable) key);
        if (buckets[hash].variable.equals(key)){
            Double value = buckets[hash].value;
            buckets[hash] = buckets[hash].next;
            return value;
        }
        return buckets[hash].remove((Variable) key);
    }

    @Override
    public void putAll(Map<? extends Variable, ? extends Double> m) {
        Set<? extends Entry<? extends Variable, ? extends Double>> entrySet = m.entrySet();
        for (Entry<? extends Variable, ? extends Double> entry: entrySet){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < numBuckets;i++){
            buckets[i] = null;
        }
    }

    @Override
    public Set<Variable> keySet() {
        Set<Variable> keySet = new HashSet<>();
        for (int i = 0; i < numBuckets;i++){
            if (buckets[i] != null){
                buckets[i].keySet(keySet);
            }
        }
        return keySet;
    }

    @Override
    public Collection<Double> values() {
        ArrayList<Double> values = new ArrayList<>();
        for (int i = 0; i < numBuckets;i++){
            if (buckets[i] != null){
                buckets[i].values(values);
            }
        }
        return values;
    }

    @Override
    public Set<Entry<Variable, Double>> entrySet() {
        Set<Entry<Variable, Double>> entrySet = new HashSet<>();
        for (int i = 0; i < numBuckets;i++){
            if (buckets[i] != null){
                buckets[i].entrySet(entrySet);
            }
        }
        return entrySet;
    }

    private class VariableNode {
        Variable variable;
        Double value;
        VariableNode next;

        protected VariableNode(Variable variable, Double value){
            this.variable = variable;
            this.value = value;
            this.next = null;
        }

        protected Double addNext(Variable variable, Double value){
            if (this.variable.equals(variable)){
                Double oldValue = this.value;
                this.value = value;
                return oldValue;
            } else if (next != null){
                return next.addNext(variable,value);
            }
            next = new VariableNode(variable,value);
            return null;
        }

        protected Double remove(Variable variable){
            if (next != null && next.equals(variable)){
                Double value = next.value;
                next = next.next;
                return value;
            } else if (next != null){
                return next.remove(variable);
            }
            return null;
        }

        protected Double getValue(Variable variable){
            if (variable.equals(variable)){
                return value;
            } else if (next != null){
                return next.getValue(variable);
            }
            return null;
        }

        protected int size(){
            return 1 + (next == null? 0:next.size());
        }

        protected boolean containsValue(Double value){
            if (this.value == value){
                return true;
            } else if (next != null){
                return next.containsValue(value);
            }
            return false;
        }

        protected void keySet(Set<Variable> keySet){
            keySet.add(variable);
            if (next != null){
                next.keySet(keySet);
            }
        }

        protected void values(ArrayList<Double> valueSet){
            valueSet.add(value);
            if (next != null){
                next.values(valueSet);
            }
        }

        protected void entrySet(Set<Entry<Variable,Double>> entrySet){
            entrySet.add(new AbstractMap.SimpleEntry<>(variable,value));
            if (next != null){
                next.entrySet(entrySet);
            }
        }
    }
}
