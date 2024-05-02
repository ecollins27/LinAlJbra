package lin_aljbra;

import lin_aljbra.aljbra.Decimal;
import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;

abstract class ElementType<E> {

    public static final ElementType<Double> NUMERIC = new ElementType<Double>() {
        @Override
        public String toLaTeX(Double aDouble) {
            return String.valueOf(aDouble);
        }

        @Override
        public Double add(Double a, Double b) {
            return a + b;
        }

        @Override
        public Double subtract(Double a, Double b) {
            return a - b;
        }

        @Override
        public Double multiply(Double a, Double b) {
            return a * b;
        }

        @Override
        public Double divide(Double a, Double b) {
            return a / b;
        }

        @Override
        public Double pow(Double a, Double b) {
            return Math.pow(a, b);
        }

        @Override
        public Double valueOf(long n) {
            return Double.valueOf(n);
        }

        @Override
        public double eval(Double a) {
            return a.doubleValue();
        }

        @Override
        public Expression symbolic(Double a) {
            return new Decimal(a);
        }
    };

    public static final ElementType<Expression> SYMBOLIC = new ElementType<Expression>() {
        @Override
        public String toLaTeX(Expression expression) {
            return expression.toLaTeX();
        }

        @Override
        public Expression add(Expression a, Expression b) {
            return a.add(b);
        }

        @Override
        public Expression subtract(Expression a, Expression b) {
            return a.subtract(b);
        }

        @Override
        public Expression multiply(Expression a, Expression b) {
            return a.multiply(b);
        }

        @Override
        public Expression divide(Expression a, Expression b) {
            return a.divide(b);
        }

        @Override
        public Expression pow(Expression a, Expression b) {
            return a.pow(b);
        }

        @Override
        public Expression valueOf(long n) {
            return new Scalar(n);
        }

        @Override
        public double eval(Expression a) {
            return a.eval(null);
        }

        @Override
        public Expression symbolic(Expression a) {
            return a;
        }
    };
    public abstract String toLaTeX(E e);
    public abstract E add(E a, E b);
    public abstract E subtract(E a, E b);
    public abstract E multiply(E a, E b);
    public abstract E divide(E a, E b);
    public abstract E pow(E a, E b);
    public abstract E valueOf(long n);
    public abstract double eval(E a);
    public abstract Expression symbolic(E a);
}
