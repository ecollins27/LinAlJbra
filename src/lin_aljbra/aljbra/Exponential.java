package lin_aljbra.aljbra;

class Exponential extends Expression {

    Expression base,exponent;

    Exponential(Expression base, Expression exponent){
        this.base = base;
        this.exponent = exponent;
    }
    @Override
    public Expression negate() {
        return this.multiply(Scalar.NEG_ONE);
    }

    @Override
    public Expression invert() {
        if (exponent.equals(Scalar.NEG_ONE)){
            return base;
        }
        return new Exponential(base,exponent.negate());
    }

    @Override
    public Expression derivative(Variable v) {
        Expression sum = exponent.divide(base).multiply(base.derivative(v));
        sum = sum.add(Log.ln(base).multiply(exponent.derivative(v)));
        return this.multiply(sum);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Exponential && base.equals(((Exponential) o).base) && exponent.equals(((Exponential) o).exponent);
    }

    @Override
    public double eval(VariableMap values) {
        return Math.pow(base.eval(values),exponent.eval(values));
    }

    @Override
    public Expression withDecimals() {
        return base.withDecimals().pow(exponent.withDecimals());
    }

    @Override
    public boolean contains(Expression e) {
        if (this.equals(e)){
            return true;
        }
        return base.contains(e) || exponent.contains(e);
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        return base.replace(e,with).pow(exponent.replace(e,with));
    }

    @Override
    public String toString() {
        String toString = "";
        if (base instanceof Sum && ((Sum) base).terms.length > 1){
            toString += "(" + base.toString() + ")";
        } else if (base instanceof Product && ((Product) base).terms.length > 1){
            toString += "(" + base.toString() + ")";
        } else {
            toString += base.toString();
        }

        if (exponent instanceof Sum && ((Sum) exponent).terms.length > 1){
            toString += "^(" + exponent.toString() + ")";
        } else if (exponent instanceof Product && ((Product) exponent).terms.length > 1){
            toString += "^(" + exponent.toString() + ")";
        } else if (exponent instanceof Fraction){
            toString += "^(" + exponent.toString() + ")";
        } else {
            toString += "^" + exponent.toString();
        }
        return toString;
    }

    @Override
    public String toLaTeX() {
        if (exponent instanceof Scalar && ((Scalar) exponent).isNegative()){
            return "\\frac{1}{" + base.pow(exponent.negate()).toLaTeX() + "}";
        } else if (exponent instanceof Fraction){
            if (((Fraction) exponent).num.isNegative()){
                return "\\frac{1}{" + base.pow(exponent.negate()).toLaTeX() + "}";
            }
            String toLaTeX = "";
            if (((Fraction) exponent).den.equals(Scalar.TWO)) {
                toLaTeX += "\\sqrt{";
            } else {
                toLaTeX += "\\sqrt[" + ((Fraction) exponent).den.toLaTeX() +"]{";
            }
            return toLaTeX + base.pow(((Fraction) exponent).num).toLaTeX() + "}";
        }
        String toLaTeX = "";
        if (base instanceof Sum && ((Sum) base).terms.length > 1){
            toLaTeX += "(" + base.toLaTeX() + ")";
        } else if (base instanceof Product && ((Product) base).terms.length > 1){
            toLaTeX += "(" + base.toLaTeX() + ")";
        } else if (base instanceof Scalar && ((Scalar) base).isNegative()){
            toLaTeX += "(" + base.toLaTeX() + ")";
        } else {
            toLaTeX += base.toLaTeX();
        }
        return toLaTeX + "^{" + exponent.toLaTeX() + "}";
    }

    @Override
    public Expression simplify() {
        Expression simplifiedBase = base.simplify();
        Expression simplifiedExponent = exponent.simplify();
        return base.simplify().pow(exponent.simplify());
    }

    @Override
    public boolean isEvaluable() {
        return base.isEvaluable() && exponent.isEvaluable();
    }

    @Override
    protected Expression __add__(Expression e) {
        return null;
    }

    @Override
    protected Expression __multiply__(Expression e) {
        if (e instanceof Exponential){
            if (base.equals(((Exponential) e).base)) {
                return base.pow(exponent.add(((Exponential) e).exponent));
            } else {
                return this.invert().multiply(e.invert()).invert();
            }
        } else {
            return base.pow(exponent.add(Scalar.ONE));
        }
    }

    @Override
    protected Expression __pow__(Expression e) {
        return base.pow(exponent.multiply(e));
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        return false;
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        if (e instanceof Exponential){
            if (base.equals(((Exponential) e).base)){
                return true;
            } else if (exponent instanceof Scalar && ((Scalar) exponent).isNegative()){
                if (((Exponential) e).exponent instanceof Scalar && ((Scalar) ((Exponential) e).exponent).isNegative()){
                    return base.isMultiplicationCompatible(((Exponential) e).base) || ((Exponential) e).base.isMultiplicationCompatible(base);
                } else if (((Exponential) e).exponent instanceof Fraction && ((Fraction) ((Exponential) e).exponent).isNegative()){
                    return base.isMultiplicationCompatible(((Exponential) e).base) || ((Exponential) e).base.isMultiplicationCompatible(base);
                } else {
                    return false;
                }
            } else if (exponent instanceof Fraction && ((Fraction) exponent).isNegative()){
                if (((Exponential) e).exponent instanceof Scalar && ((Scalar) ((Exponential) e).exponent).isNegative()){
                    return base.isMultiplicationCompatible(((Exponential) e).base) || ((Exponential) e).base.isMultiplicationCompatible(base);
                } else if (((Exponential) e).exponent instanceof Fraction && ((Fraction) ((Exponential) e).exponent).isNegative()){
                    return base.isMultiplicationCompatible(((Exponential) e).base) || ((Exponential) e).base.isMultiplicationCompatible(base);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return base.equals(e) && !(exponent instanceof Fraction);
        }
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        return true;
    }

    protected static boolean isExponential(String str){
        for (int i = 0; i < str.length();i++){
            if (str.charAt(i) == '^'){
                return true;
            } else if (str.charAt(i) == '('){
                i = getMatchingDelimeter(str,i);
            }
        }
        return false;
    }

    protected static Expression parseExponential(String str){
        int prev = 0;
        for (int i = 0; i < str.length();i++){
            if (str.charAt(i) == '^'){
                return ExpressionParser.parse(str.substring(0,i)).pow(ExpressionParser.parse(str.substring(i+1)));
            } else if (str.charAt(i) == '('){
                i = getMatchingDelimeter(str,i);
            }
        }
        return Scalar.ONE;
    }
}
