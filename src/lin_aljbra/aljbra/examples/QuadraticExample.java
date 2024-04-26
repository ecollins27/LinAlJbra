package lin_aljbra.aljbra.examples;

import lin_aljbra.aljbra.*;

public class QuadraticExample {

    public static void main(String[] args){
        Expression a = new Variable('a'), b = new Variable('b'), c = new Variable('c');
        Expression quadraticFormula = quadratic(a,b,c);

        Expression aVal = Fraction.valueOf(1.3,1);
        Expression bVal = new Scalar(-7);
        Expression cVal = Scalar.TWO;
        Expression solution = quadraticFormula.replace(a,aVal).replace(b,bVal).replace(c,cVal).fullSimplify();

        System.out.println("The solution to 4/3 * x^2 - 7x + 2 is " + solution.toLaTeX() + " in LaTeX");
        System.out.println("The decimal approximation is " + solution.eval(null));
    }

    public static Expression quadratic(Expression A, Expression B, Expression C){
        Expression det = B.pow(Scalar.TWO).subtract(new Scalar(4).multiply(A).multiply(C));
        return B.negate().add(det.sqrt()).divide(Scalar.TWO.multiply(A));
    }
}
