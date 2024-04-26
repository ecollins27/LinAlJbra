package lin_aljbra.aljbra;

import java.util.*;

class Product extends Expression {

    Expression[] terms;

    Product(Expression... terms){
        this.terms = terms;
        Arrays.sort(this.terms,null);
    }
    @Override
    public Expression negate() {
        Expression product = Scalar.ONE;
        boolean containsNegative = false;
        for (Expression term: terms){
            if (!term.equals(Scalar.NEG_ONE)){
                product = product.multiply(term);
            } else {
                containsNegative = true;
            }
        }
        if (containsNegative){
            return product;
        } else {
            return product.multiply(Scalar.NEG_ONE);
        }
    }

    @Override
    public Expression invert() {
        Expression product = Scalar.ONE;
        for (Expression term: terms){
            product = product.multiply(term.invert());
        }
        return product;
    }

    @Override
    public Expression derivative(Variable v) {
        Expression sum = Scalar.ZERO;
        for (int i = 0; i < terms.length;i++){
            Expression product = Scalar.ONE;
            for (int j = 0; j < terms.length;j++){
                if (j == i){
                    product = product.multiply(terms[j].derivative(v));
                } else {
                    product = product.multiply(terms[j]);
                }
            }
            sum = sum.add(product);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product) || ((Product) o).terms.length != terms.length){
            return false;
        }
        for (int i = 0; i < terms.length;i++){
            if (!terms[i].equals(((Product) o).terms[i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public double eval(VariableMap values) {
        double product = 1;
        for (Expression term: terms){
            product *= term.eval(values);
        }
        return product;
    }

    @Override
    public Expression withDecimals() {
        Expression product = Decimal.ONE;
        for (Expression term: terms){
            product = product.multiply(term.withDecimals());
        }
        return product;
    }

    @Override
    public boolean contains(Expression e) {
        if (this.equals(e)){
            return true;
        }
        for (Expression term: terms){
            if (term.contains(e)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        Expression product = Scalar.ONE;
        for (Expression term: terms){
            product = product.multiply(term.replace(e,with));
        }
        return product;
    }

    @Override
    public String toString() {
        Expression num = Scalar.ONE,den = Scalar.ONE;
        for (Expression term: terms){
            if (term instanceof Fraction){
                num = num.multiply(((Fraction) term).num);
                den = den.multiply(((Fraction) term).den);
            } else if (term instanceof Exponential && (((Exponential) term).exponent instanceof Scalar)) {
                if (((Scalar) ((Exponential) term).exponent).isNegative()) {
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else if (term instanceof Exponential && (((Exponential) term).exponent instanceof Decimal)) {
                if (((Decimal) ((Exponential) term).exponent).isNegative()) {
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            }  else if (term instanceof Exponential && (((Exponential) term).exponent instanceof Fraction)){
                if (((Fraction) ((Exponential) term).exponent).isNegative()){
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else if (term instanceof Exponential && ((Exponential) term).exponent instanceof Product){
                if (((Product) ((Exponential) term).exponent).getCoefficient().eval(null) < 0){
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else {
                num = num.multiply(term);
            }
        }
        if (den.equals(Scalar.ONE)){
            return basicToString();
        } else {
            String toString = "";
            if (num instanceof Sum && ((Sum) num).terms.length > 1){
                toString += "(" + num.toString() + ")";
            } else if (num instanceof Exponential && !((Exponential) num).exponent.equals(Scalar.ONE)){
                toString += "(" + num.toString() + ")";
            } else {
                toString += num.toString();
            }
            if (den instanceof Sum && ((Sum) den).terms.length > 1){
                toString += " / (" + den.toString() + ")";
            } else if (den instanceof Product && ((Product) den).terms.length > 1){
                toString += " / (" + den.toString() + ")";
            } else if (den instanceof Exponential && !((Exponential) den).exponent.equals(Scalar.ONE)){
                toString += " / (" + den.toString() + ")";
            } else {
                toString += " / " + den.toString();
            }
            return toString;
        }
    }

    private String basicToString(){
        String toString = "";
        for (int i = 0; i < terms.length;i++){
            if (!toString.isEmpty() && !toString.equals("-") && !terms[i].equals(Scalar.NEG_ONE)){
                toString += " * ";
            }
            if (terms[i].equals(Scalar.NEG_ONE)){
                toString = "-" + toString;
            } else if (terms[i] instanceof Sum && ((Sum) terms[i]).terms.length > 1) {
                toString += "(" + terms[i].toString() +")";
            } else {
                toString += terms[i].toString();
            }
        }
        return toString;
    }

    @Override
    public String toLaTeX() {
        Expression num = Scalar.ONE,den = Scalar.ONE;
        for (Expression term: terms){
            if (term instanceof Fraction){
                num = num.multiply(((Fraction) term).num);
                den = den.multiply(((Fraction) term).den);
            } else if (term instanceof Exponential && (((Exponential) term).exponent instanceof Scalar)) {
                if (((Scalar) ((Exponential) term).exponent).isNegative()) {
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else if (term instanceof Exponential && (((Exponential) term).exponent instanceof Fraction)){
                if (((Fraction) ((Exponential) term).exponent).isNegative()){
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else if (term instanceof Exponential && ((Exponential) term).exponent instanceof Product){
                if (((Product) ((Exponential) term).exponent).getCoefficient().eval(null) < 0){
                    den = den.multiply(term.invert());
                } else {
                    num = num.multiply(term);
                }
            } else {
                num = num.multiply(term);
            }
        }
        if (den.equals(Scalar.ONE)){
            return basicToLaTeX();
        } else {
            return "\\frac{" + num.toLaTeX() + "}{" + den.toLaTeX() + "}";
        }
    }

    private String basicToLaTeX(){
        String toLaTeX = "";
        for (int i = 0; i < terms.length;i++){
            if (terms[i].equals(Scalar.NEG_ONE)){
                toLaTeX = "-" + toLaTeX;
            } else {
                toLaTeX += terms[i].toLaTeX();
            }
        }
        return toLaTeX;
    }

    @Override
    public Expression simplify() {
        ArrayList<Expression[]> map = new ArrayList<>();
        Queue<Expression> queue = new LinkedList<>(Arrays.asList(terms));
        while (!queue.isEmpty()){
            Expression simplified = queue.poll().simplify();
            Expression base,exponent;
            if (simplified instanceof Product){
                for (Expression eTerm: ((Product) simplified).terms){
                    queue.add(eTerm);
                }
                continue;
            } else if (simplified instanceof Exponential){
                base = ((Exponential) simplified).base;
                exponent = ((Exponential) simplified).exponent;
            } else {
                base = simplified;
                exponent = Scalar.ONE;
            }
            int index = indexOf(base,map);
            if (index >= 0){
                map.get(index)[1] = map.get(index)[1].add(exponent);
            } else {
                map.add(new Expression[]{base,exponent});
            }
        }
        Expression product = Scalar.ONE;
        for (Expression[] term: map){
            product = product.multiply(term[0].pow(term[1].simplify()));
        }
        return product;
    }

    @Override
    public boolean isEvaluable() {
        for (Expression term: terms){
            if (!term.isEvaluable()){
                return false;
            }
        }
        return true;
    }

    @Override
    protected Expression __add__(Expression e) {
        Expression coefficient = getCoefficient();
        Expression term = getTerm();
        if (e instanceof Product){
            Expression coefficient2 = ((Product) e).getCoefficient();
            return coefficient.add(coefficient2).multiply(term);
        }
        return coefficient.add(Scalar.ONE).multiply(term);
    }

    @Override
    protected Expression __multiply__(Expression e) {
        if (e instanceof Product){
            Expression product = this;
            for (Expression eTerm: ((Product) e).terms){
                product = product.multiply(eTerm);
            }
        }
        boolean addTerm = true;
        for (Expression term: terms){
            if (term.isMultiplicationCompatible(e) || e.isMultiplicationCompatible(term)){
                addTerm = false;
            }
        }
        if (addTerm){
            Expression[] newTerms = Arrays.copyOf(terms,terms.length + 1);
            newTerms[terms.length] = e;
            return new Product(newTerms);
        } else {
            Expression[] newTerms = terms.clone();
            for (int i = 0; i < newTerms.length;i++){
                if (newTerms[i].isMultiplicationCompatible(e)){
                    newTerms[i] = newTerms[i].multiply(e);
                    break;
                } else if (e.isMultiplicationCompatible(newTerms[i])){
                    newTerms[i] = e.multiply(newTerms[i]);
                    break;
                }
            }
            return new Product(newTerms);
        }
    }

    @Override
    protected Expression __pow__(Expression e) {
        Expression product = Scalar.ONE;
        for (Expression term: terms){
            product = product.multiply(term.pow(e));
        }
        return product;
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        Expression term = getTerm();
        if (e instanceof Product){
            return ((Product) e).getTerm().equals(term);
        }
        return e.equals(term);
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        return !(e instanceof Sum);
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        return !e.equals(Scalar.NEG_ONE);
    }

    Expression getCoefficient(){
        Expression coefficient = Scalar.ONE;
        for (Expression term: terms){
            if (term instanceof Fraction || term instanceof Scalar || term instanceof Decimal){
                coefficient = coefficient.multiply(term);
            }
        }
        return coefficient;
    }

    Expression getTerm(){
        Expression term = Scalar.ONE;
        for (Expression expression: terms){
            if (!(expression instanceof Scalar) && !(expression instanceof Fraction) && !(expression instanceof Decimal)){
                term = term.multiply(expression);
            }
        }
        return term;
    }

    private int indexOf(Expression e,ArrayList<Expression[]> arrayList){
        for (int i = 0; i < arrayList.size();i++){
            if (arrayList.get(i)[0].equals(e)){
                return i;
            }
        }
        return -1;
    }

    protected static boolean isProduct(String str){
        for (int i = 0; i < str.length();i++){
            if (str.charAt(i) == '*' || str.charAt(i) == '/'){
                return true;
            } else if (str.charAt(i) == '('){
                i = getMatchingDelimeter(str,i);
            }
        }
        return false;
    }

    protected static Expression parseProduct(String str){
        Expression product = Scalar.ONE;
        int prev = 0;
        for (int i = 0; i < str.length();i++){
            if (str.charAt(i) == '*' || str.charAt(i) == '/'){
                product = product.multiply(ExpressionParser.parse(str.substring(prev + (prev == 0? 0:1),i)).pow(str.charAt(prev) == '/'? Scalar.NEG_ONE: Scalar.ONE));
                prev = i;
            } else if (str.charAt(i) == '('){
                i = getMatchingDelimeter(str,i);
            }
        }
        product = product.multiply(ExpressionParser.parse(str.substring(prev + (prev == 0? 0:1))).pow(str.charAt(prev) == '/'? Scalar.NEG_ONE: Scalar.ONE));
        return product;
    }
}
