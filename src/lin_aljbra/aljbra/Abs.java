package lin_aljbra.aljbra;

class Abs extends Expression {

    protected Expression operand;
    public Abs(Expression e) {
        this.operand = e;
    }

    @Override
    public Expression negate() {
        return this.multiply(Scalar.NEG_ONE);
    }

    @Override
    public Expression invert() {
        return this.pow(Scalar.NEG_ONE);
    }

    @Override
    public boolean contains(Expression e) {
        if (this.equals(e)){
            return true;
        }
        return operand.contains(e);
    }

    @Override
    public boolean isEvaluable() {
        return operand.isEvaluable();
    }

    @Override
    protected Expression __add__(Expression e) {
        return null;
    }

    @Override
    protected Expression __multiply__(Expression e) {
        return null;
    }

    @Override
    protected Expression __pow__(Expression e) {
        return null;
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        return false;
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        return false;
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        return false;
    }

    @Override
    public Expression derivative(Variable v) {
        return operand.divide(this).multiply(operand.derivative(v));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Abs && operand.equals(((Abs) o).operand);
    }

    @Override
    public double eval(VariableMap values) {
        return Math.abs(operand.eval(values));
    }

    @Override
    public Expression withDecimals() {
        return operand.withDecimals().abs();
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        return operand.replace(e,with).abs();
    }

    @Override
    public String toString() {
        return "|" + operand.toString() + "|";
    }

    @Override
    public String toLaTeX() {
        return "\\left|" + operand.toLaTeX() + "\\right|";
    }

    @Override
    public Expression simplify() {
        return operand.simplify().abs();
    }

    @Override
    public Expression abs() {
        return this;
    }

    protected static boolean isAbs(String str){
        return str.length() > 2 && str.charAt(0) == '|' && str.charAt(str.length() - 1) == '|';
    }

    protected static Expression parseAbs(String str){
        return ExpressionParser.parse(str.substring(1,str.length() - 1)).abs();
    }
}
