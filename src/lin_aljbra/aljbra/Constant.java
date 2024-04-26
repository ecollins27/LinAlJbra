package lin_aljbra.aljbra;

public class Constant extends Variable {

    public final static Constant PI = new Constant('π',"","\\pi",Math.PI);
    public final static Constant PHI = new Constant('Φ',"\\phi",(1 + Math.sqrt(5)) / 2.0);
    public final static Constant E = new Constant('e',Math.E);
    public final static Constant I = new Constant('i',Double.NaN);

    double value;
    public Constant(char name, double value) {
        super(name);
        this.value = value;
    }

    public Constant(char name, String subtext,double value){
        super(name,subtext);
        this.value = value;
    }

    public Constant(char name, String subtext, String laTeXName, double value){
        super(name,subtext,laTeXName);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean isEvaluable() {
        return !this.isI();
    }

    @Override
    public double eval(VariableMap values) {
        if (this.isI()){
            throw new RuntimeException("Cannot evaluate imaginary value");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Constant)){
            return false;
        }
        if (this.isI() && ((Constant) o).isI()){
            return true;
        }
        return o instanceof Constant && name.equals(((Constant) o).name) && ((Constant) o).value == value;
    }

    @Override
    public Expression derivative(Variable v) {
        return Scalar.ZERO;
    }

    @Override
    protected Expression __add__(Expression e) {
        return new Decimal(value + ((Decimal)e).value);
    }

    @Override
    protected Expression __multiply__(Expression e) {
        return new Decimal(value * ((Decimal)e).value);
    }

    @Override
    protected Expression __pow__(Expression e) {
        if (this.isI()){
            if (e instanceof Scalar){
                long value = ((Scalar) e).value;
                if (value < 0){
                    value *= 3;
                }
                value = Math.abs(value);
                long pow = value / 4;
                long mod = value % 2;
                if (mod == 0) {
                    return Scalar.NEG_ONE.pow(new Scalar(pow + 1));
                } else {
                    return Scalar.NEG_ONE.pow(new Scalar(pow + 1)).multiply(Constant.I);
                }
            }
        }
        return new Decimal(Math.pow(value,((Decimal)e).value));
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        return e instanceof Decimal;
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        return e instanceof Decimal;
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        if (this.isI()){
            return e instanceof Scalar || e instanceof Fraction;
        }
        return e instanceof Decimal;
    }

    protected static boolean isConstant(String str){
        return str.equals("e") || str.equals("pi") || str.equals("phi") || str.equals("π") || str.equals("Φ") || str.equals("i");
    }

    protected static Expression parseConstant(String str){
        if (str.equals("e")){
            return Constant.E;
        } else if (str.equals("pi") || str.equals("π")){
            return Constant.PI;
        } else if (str.equals("i")){
            return Constant.I;
        } else {
            return Constant.PHI;
        }
    }

    private boolean isI(){
        return Double.isNaN(value) && name.equals("i");
    }
}
