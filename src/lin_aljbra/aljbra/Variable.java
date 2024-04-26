package lin_aljbra.aljbra;

public class Variable extends Expression {

    String name;
    String laTeXName;

    public Variable(char name){
        this.name = "" + name;
        this.laTeXName = this.name;
    }

    public Variable(char name, String subtext){
        if (subtext.length() > 0) {
            this.name = name + "_{" + subtext + "}";
        } else {
            this.name = "" + name;
        }
        this.laTeXName = this.name;
    }
    public Variable(char name,String subtext,String laTeXName){
        if (subtext.length() > 0) {
            this.name = name + "_{" + subtext + "}";
        } else {
            this.name = "" + name;
        }
        if (laTeXName.length() > 0 && laTeXName.charAt(0) != '\\'){
            throw new RuntimeException("LaTeX labels for Variables must begin with \"\\\"");
        }
        this.laTeXName = laTeXName;
    }

    public String getName(){
        return name;
    }

    public String getLaTeXName(){
        return laTeXName;
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
    public Expression derivative(Variable v) {
        return this.equals(v)? Scalar.ONE: Scalar.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Variable && !(o instanceof Constant) && name.equals(((Variable) o).name);
    }

    @Override
    public double eval(VariableMap values) {
        return values.get(name);
    }

    @Override
    public Expression withDecimals() {
        return this;
    }

    @Override
    public boolean contains(Expression e) {
        return this.equals(e);
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toLaTeX() {
        return laTeXName;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean isEvaluable() {
        return false;
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

    private final static boolean isLegalCharacter(char c){
        String illegalCharacters = "+-*/^()[]{}|.,>=<";
        return !illegalCharacters.contains(String.valueOf(c));
    }

    protected final static boolean isVariable(String str){
        if (str.length() == 1){
            return true;
        } else if (str.length() == 3){
            return str.charAt(1) == '_';
        } else if (str.length() > 3){
            return str.charAt(1) == '_' && str.charAt(2) == '{' && getMatchingDelimeter(str,2,'{','}') == str.length() - 1;
        }
        return false;
    }
    protected final static Expression parseVariable(String str){
        if (str.length() == 1){
            return new Variable(str.charAt(0));
        } else if (str.length() == 3){
            return new Variable(str.charAt(0),"" + str.charAt(2));
        }
        return new Variable(str.charAt(0),str.substring(3,str.length() - 1));
    }
}
