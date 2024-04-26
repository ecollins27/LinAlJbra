package lin_aljbra.aljbra;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public class Fraction extends Expression {

    Scalar num,den;

    Fraction(Scalar num, Scalar den){
        this.num = num;
        this.den = den;
        if (this.den.isNegative()){
            this.den = (Scalar) den.negate();
            this.num = (Scalar) num.negate();
        }
    }

    public static Expression valueOf(double n){
        Scalar constant = new Scalar((long)n);
        BigDecimal num = BigDecimal.valueOf(n).remainder(BigDecimal.ONE);
        int length = 0;
        while (num.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0){
            num = num.scaleByPowerOfTen(1);
            length++;
        }
        return constant.add(new Scalar(num.longValue()).divide(new Scalar((long)Math.pow(10,length))));
    }

    public final Scalar getNum(){
        return num;
    }

    public final Scalar getDen(){
        return den;
    }
    public static Expression valueOf(double n, int repeatingLength){
        BigDecimal num = BigDecimal.valueOf(n);
        if (repeatingLength > getNumDecimalDigits(num)){
            throw new RuntimeException("Repetition length must be less than or equal to the number of digits right of decimal point");
        }
        int length = 0;
        while (getNumDecimalDigits(num) > repeatingLength){
            num = num.scaleByPowerOfTen(1);
            length++;
        }
        Scalar constant = new Scalar(num.longValue());
        BigDecimal decimal = num.remainder(BigDecimal.ONE).scaleByPowerOfTen(repeatingLength);
        return constant.add(new Scalar(decimal.longValue()).divide(new Scalar((long)(Math.pow(10,repeatingLength) - 1)))).divide(new Scalar((long)(Math.pow(10,length))));
    }

    private static int getNumDecimalDigits(BigDecimal n){
        int length = 0;
        while (n.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0){
            n = n.scaleByPowerOfTen(1);
            length++;
        }
        return length;
    }
    @Override
    public Expression negate() {
        return num.negate().divide(den);
    }

    @Override
    public Expression invert() {
        return den.divide(num);
    }

    @Override
    public Expression derivative(Variable v) {
        return Scalar.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Fraction && num.equals(((Fraction) o).num) && den.equals(((Fraction) o).den);
    }

    @Override
    public double eval(VariableMap values) {
        BigDecimal quotient = BigDecimal.valueOf(num.eval(values)).divide(BigDecimal.valueOf(den.eval(values)), MathContext.DECIMAL128);
        return quotient.doubleValue();
    }

    @Override
    public Expression withDecimals() {
        return num.withDecimals().divide(den.withDecimals());
    }

    @Override
    public boolean contains(Expression e) {
        if (this.equals(e)){
            return true;
        }
        return num.contains(e) || den.contains(e);
    }

    @Override
    public Expression replace(Expression e, Expression with) {
        if (this.equals(e)){
            return with;
        }
        return num.replace(e,with).divide(den.replace(e,with));
    }

    @Override
    public String toString() {
        return num.toString() + " / " + den.toString();
    }

    @Override
    public String toLaTeX() {
        return "\\frac{" + num.toLaTeX() + "}{" + den.toLaTeX() +"}";
    }

    @Override
    public Expression simplify() {
        ArrayList<long[]> numFactors = num.primeFactorization();
        ArrayList<long[]> denFactors = den.primeFactorization();
        simplify(numFactors,denFactors);
        return new Scalar(numFactors).divide(new Scalar(denFactors));
    }

    @Override
    public boolean isEvaluable() {
        return true;
    }

    @Override
    public Expression abs() {
        return num.abs().divide(den);
    }

    @Override
    protected Expression __add__(Expression e) {
        Scalar num2,den2;
        if (e instanceof Scalar){
            num2 = (Scalar) e;
            den2 = Scalar.ONE;
        } else {
            num2 = ((Fraction)e).num;
            den2 = ((Fraction)e).den;
        }
        Scalar lcm = lcm(den,den2);
        Scalar factor1 = getFactor(lcm,den);
        Scalar factor2 = getFactor(lcm,den2);
        return num.multiply(factor1).add(num2.multiply(factor2)).divide(lcm);
    }

    @Override
    protected Expression __multiply__(Expression e) {
        Scalar num2,den2;
        if (e instanceof Scalar){
            num2 = (Scalar) e;
            den2 = Scalar.ONE;
        } else {
            num2 = ((Fraction) e).num;
            den2 = ((Fraction) e).den;
        }
        ArrayList<long[]> multipliedNum = Scalar.merge(num.primeFactorization(),num2.primeFactorization());
        ArrayList<long[]> multipliedDen = Scalar.merge(den.primeFactorization(),den2.primeFactorization());
        simplify(multipliedNum,multipliedDen);
        if (multipliedDen.size() > 0) {
            return new Fraction(new Scalar(multipliedNum), new Scalar(multipliedDen));
        } else {
            return new Scalar(multipliedNum);
        }
    }

    @Override
    protected Expression __pow__(Expression e) {
        if (e instanceof Decimal){
            return new Decimal(Math.pow(this.eval(null),((Decimal) e).value));
        }
        return num.pow(e).divide(den.pow(e));
    }

    @Override
    protected boolean isAdditionCompatible(Expression e) {
        return e instanceof Scalar || e instanceof Fraction;
    }

    @Override
    protected boolean isMultiplicationCompatible(Expression e) {
        return e instanceof Scalar || e instanceof Fraction;
    }

    @Override
    protected boolean isPowCompatible(Expression e) {
        return (num.isPowCompatible(e) && den.isPowCompatible(e)) || e instanceof Decimal;
    }

    private static int indexOf(long n, ArrayList<long[]> arrayList){
        for (int i = 0; i < arrayList.size();i++){
            if (arrayList.get(i)[0] == n){
                return i;
            }
        }
        return -1;
    }

    private Scalar lcm(Scalar a, Scalar b){
        ArrayList<long[]> primeFactors = a.primeFactorization();
        for (int i = 0; i < b.primeFactorization().size();i++){
            long[] factor = b.primeFactorization().get(i);
            int index = indexOf(factor[0],primeFactors);
            if (index == -1){
                primeFactors.add(factor);
            } else {
                primeFactors.get(index)[1] = Math.max(factor[1],primeFactors.get(index)[1]);
            }
        }
        return new Scalar(primeFactors);
    }

    boolean isNegative(){
        return num.isNegative();
    }

    private Scalar getFactor(Scalar lcm, Scalar num){
        ArrayList<long[]> primeFactors = lcm.primeFactorization();
        for (int i = 0; i < num.primeFactorization().size();i++){
            long[] factor = num.primeFactorization().get(i);
            int index = indexOf(factor[0],primeFactors);
            if (factor[1] == primeFactors.get(index)[1]){
                primeFactors.remove(index);
            } else {
                primeFactors.get(index)[1] -= factor[1];
            }
        }
        return new Scalar(primeFactors);
    }

    static void simplify(ArrayList<long[]> numFactors, ArrayList<long[]> denFactors){
        for (int i = denFactors.size() - 1; i >= 0; i--){
            long[] factor = denFactors.get(i);
            int index = indexOf(factor[0],numFactors);
            if (index >= 0){
                if (numFactors.get(index)[1] == factor[1]){
                    numFactors.remove(index);
                    denFactors.remove(i);
                } else if (numFactors.get(index)[1] > factor[1]){
                    denFactors.remove(i);
                    numFactors.get(index)[1] -= factor[1];
                } else {
                    denFactors.get(i)[1] -= numFactors.get(index)[1];
                    numFactors.remove(index);
                }
            }
        }
    }
}
