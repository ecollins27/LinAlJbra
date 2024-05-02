package lin_aljbra;

import lin_aljbra.aljbra.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Matrix<M extends Matrix<M,E>,E> {

    E[][] elements;
    int width,height;
    Class<E> elementClass;
    Class<M> matrixClass;
    ElementType<E> elementType;

    public Matrix(ElementType<E> elementType, Class<M> matrixClass, Class<E> elementClass, E[][] elements){
        this(elementType,matrixClass,elementClass);
        initElements(elements);
    }

    protected Matrix(ElementType<E> elementType, Class<M> matrixClass, Class<E> elementClass){
        this.elementType = elementType;
        this.elementClass = elementClass;
        this.matrixClass = matrixClass;
    }

    protected void initElements(E[][] elements){
        this.elements = elements;
        this.height = elements.length;
        this.width = elements[0].length;
    }

    public final E get(int i, int j){
        return elements[i][j];
    }

    public final int getHeight(){
        return height;
    }

    public final int getWidth(){
        return width;
    }

    public final M add(M m){
        return performOperation((M) this,m, (a, b) -> add(a,b));
    }

    public final M subtract(M m){
        return performOperation((M) this, m, (a, b) -> subtract(a,b));
    }

    public final M multiply(E e){
        return performOperation((M) this,null, (a, b) -> multiply(a, e));
    }

    public final M divide(E e){
        return performOperation((M) this, null, (a, b) -> divide(a, e));
    }

    public final M multiply(M m){
        int newWidth = m.getWidth();
        E[][] newElements = newArrayInstance(height, newWidth);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < newWidth; j++) {
                E sum = valueOf(0);
                for (int k = 0; k < width; k++) {
                    sum = add(sum, multiply(this.get(i, k), m.get(k , j)));
                }
                newElements[i][j] = sum;
            }
        }
        return newMatrixInstance(newElements);
    }

    public final M transpose(){
        E[][] newElements = newArrayInstance(width,height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[j][i] = get(i,j);
            }
        }
        return newMatrixInstance(newElements);
    }

    public final E det(){
        if (width != height){
            throw new RuntimeException("Matrix must be square");
        } if (width == 2){
            return subtract(multiply(get(0,0), get(1,1)), multiply(get(0,1), get(1,0)));
        }
        E sum = valueOf(0);
        for (int j = 0; j < width; j++) {
            if (j % 2 == 0){
                sum = add(sum, multiply(get(0,j), cofactor(0,j).det()));
            } else {
                sum = subtract(sum, multiply(get(0,j), cofactor(0,j).det()));
            }
        }
        return sum;
    }

    public final M inverse(){
        E det = det();
        E[][] newElements = newArrayInstance(height,width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[i][j] = divide(cofactor(j,i).det(), det);
                if ((i + j) % 2 == 1){
                    newElements[i][j] = multiply(newElements[i][j], valueOf(-1));
                }
            }
        }
        return newMatrixInstance(newElements);
    }

    public final M ref(){
        RREFManager<M,E> rrefManager = new RREFManager<>((M)this);
        return rrefManager.ref();
    }

    public final M rref(){
        RREFManager<M,E> rrefManager = new RREFManager<>((M)this);
        return rrefManager.rref();
    }

    public final M augment(M matrix){
        if (height != matrix.height){
            throw new RuntimeException("Matrix of incompatible dimensions");
        }
        E[][] newElements = newArrayInstance(height,width + matrix.width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < (width + matrix.width); j++) {
                newElements[i][j] = j < width? get(i,j): matrix.get(i, j - width);
            }
        }
        return newMatrixInstance(newElements);
    }

    public ArrayList<E> exactEigenvalues(){
        ArrayList<E> eigenvalues = new ArrayList<>();
        if (width != height){
            throw new RuntimeException("Matrix must be square");
        } if (width >= 3){
            throw new RuntimeException("Cannot find exact eigenvalues of matrix width dimensions >= 3");
        } else if (width == 1){
            return get(0,0).equals(valueOf(0))? new ArrayList<>(): new ArrayList<>(Arrays.asList(get(0,0)));
        } else {
            E a = valueOf(1);
            E b = multiply(valueOf(-1),add(get(0,0),get(1,1)));
            E c = subtract(multiply(get(0,0),get(1,1)),multiply(get(1,0),get(0,1)));
            E discriminant = subtract(multiply(b,b),multiply(valueOf(4),multiply(a,c)));
            if (discriminant.equals(valueOf(0))){
                E eigenValue = divide(multiply(valueOf(-1),b),multiply(valueOf(2),a));
                return new ArrayList<>(Arrays.asList(eigenValue));
            }
            E difference = pow(discriminant,divide(valueOf(1),valueOf(2)));
            E eigenValue1 = divide(add(multiply(valueOf(-1),b),difference),multiply(valueOf(2),a));
            E eigenValue2 = divide(add(multiply(valueOf(-1),b),difference),multiply(valueOf(2),a));
            eigenvalues.add(eigenValue1);
            eigenvalues.add(eigenValue2);
            return eigenvalues;
        }
    }

    E[][] pad(E... entry){
        E[][] array = newArrayInstance(entry.length,1);
        for (int i = 0; i < entry.length; i++) {
            array[i][0] = entry[i];
        }
        return array;
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!get(i,j).equals(valueOf(0))){
                    return false;
                }
            }
        }
        return true;
    }

    protected M normalize(){
        E magnitude = getMagnitude();
        return this.divide(magnitude);
    }
    
    protected E getMagnitude(){
        E sum = valueOf(0);
        for (int i = 0; i < height; i++) {
            sum = add(sum,pow(get(i,0),valueOf(2)));
        }
        return pow(sum,divide(valueOf(1),valueOf(2)));
    }

    public M getColumn(int index){
        E[][] newElements = newArrayInstance(height,1);
        for (int i = 0; i < height; i++) {
            newElements[i][0] = get(i,index);
        }
        return newMatrixInstance(newElements);
    }

    public M getRow(int index){
        E[][] newElements = newArrayInstance(1,width);
        for (int i = 0; i < width; i++) {
            newElements[0][i] = get(index,i);
        }
        return newMatrixInstance(newElements);
    }

    public final double[] approximateEigenvalues(){
        return getRoots(getCharacteristicCoefficients());
    }

    private static Matrix.Numeric pad(Matrix.Numeric matrix){
        int length = Math.max(matrix.width,matrix.height);
        Double[][] newElements = new Double[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (j >= matrix.width){
                    newElements[i][j] = 0.0;
                } else if (i >= matrix.height){
                    newElements[i][j] = 0.0;
                } else {
                    newElements[i][j] = matrix.get(i,j);
                }
            }
        }
        return new Matrix.Numeric(newElements);
    }

    private static double dot(Numeric a, Numeric b){
        double sum = 0;
        for (int i = 0; i < a.height; i++) {
            sum += a.get(i,0) * b.get(i,0);
        }
        return sum;
    }

    private E dot(M a, M b){
        E sum = valueOf(0);
        for (int i = 0; i < a.height; i++) {
            sum = add(sum,multiply(a.get(i,0),b.get(i,0)));
        }
        return sum;
    }

    public final Numeric toNumeric(){
        Double[][] newElements = new Double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[i][j] = toNumeric(get(i,j));
            }
        }
        return new Numeric(newElements);
    }

    public final Symbolic toSymbolic(){
        Expression[][] newElements = new Expression[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[i][j] = toSymbolic(get(i,j));
            }
        }
        return new Symbolic(newElements);
    }

    private static double[] getRoots(double[] p){
        double[] polynomial = p.clone();
        int numRoots = numRoots(p);
        double[] roots = new double[numRoots];
        for (int i = 0; i < numRoots; i++) {
            roots[i] = newtonsMethod(polynomial);
            polynomial = divide(polynomial,new double[]{1,-roots[i]})[0];
        }
        return roots;
    }

    private static double newtonsMethod(double[] p){
        double[] derivative = derivative(p);
        double difference = Integer.MAX_VALUE;
        double guess = 0;
        while (eval(derivative,guess) == 0){
            guess += 0.1;
        }
        while (Math.abs(difference) > 0.0001){
            double prevGuess = guess;
            guess -= eval(p,guess) / eval(derivative,guess);
            difference = guess - prevGuess;
        }
        return guess;
    }

    private static double eval(double[] p, double x){
        double sum = 0;
        for (int i = 0; i < p.length; i++) {
            sum += p[i] * Math.pow(x,p.length - 1 - i);
        }
        return sum;
    }

    private static int numRoots(double[] polynomial){
        double[] derivative = derivative(polynomial);
        double[][] sequence = new double[polynomial.length][0];
        sequence[0] = polynomial;
        sequence[1] = derivative;
        for (int i = 2; i < sequence.length; i++) {
            sequence[i] = multiply(divide(sequence[i - 2], sequence[i - 1])[1], -1);
        }
        boolean positiveLastSign = polynomial[0] > 0;
        boolean negativeLastSign = (polynomial.length % 2 == 0)? polynomial[0] < 0: polynomial[0] > 0;
        int positive = 0;
        int negative = 0;
        for (int i = 1; i < sequence.length; i++) {
            if (sequence[i].length == 0){
                continue;
            } if (positiveLastSign && sequence[i][0] < 0){
                positive++;
                positiveLastSign = false;
            } else if (!positiveLastSign && sequence[i][0] > 0){
                positive++;
                positiveLastSign = true;
            } if (negativeLastSign && ((sequence[i].length % 2 == 0 && sequence[i][0] > 0) || (sequence[i].length % 2 == 1 && sequence[i][0] < 0))){
                negative++;
                negativeLastSign = false;
            } else if (!negativeLastSign && ((sequence[i].length % 2 == 0 && sequence[i][0] < 0) || (sequence[i].length % 2 == 1 && sequence[i][0] > 0))){
                negative++;
                negativeLastSign = true;
            }
        }
        return negative - positive;
    }

    private static double[][] divide(double[] p1, double[] p2){
        double[] polynomial = p1.clone();
        double[] result = new double[p1.length - p2.length + 1];
        for (int i = 0; i < result.length; i++) {
            if (polynomial.length < p2.length){
                return new double[][]{result,polynomial};
            }
            result[i] = polynomial[0] / p2[0];
            polynomial = trim(subtract(polynomial,multiply(p2,result[i])));;
        }
        if (polynomial.length == 0){
            return new double[][]{result, new double[]{0}};
        }
        return new double[][]{result,polynomial};
    }

    private static double[] multiply(double[] p, double n){
        double[] result = new double[p.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = n * p[i];
        }
        return result;
    }

    private static double[] subtract(double[] p1, double[] p2){
        double[] result = new double[p1.length];
        for (int i = 0; i < result.length; i++) {
            if (i < p2.length){
                result[i] = p1[i] - p2[i];
            } else {
                result[i] = p1[i];
            }
        }
        return result;
    }

    private static double[] trim(double[] polynomial){
        int index = 0;
        while (index < polynomial.length && Math.round(polynomial[index] * 100000) == 0){
            index++;
        }
        double[] result = new double[polynomial.length - index];
        for (int i = 0; i < result.length; i++) {
            result[i] = polynomial[i + index];
        }
        return result;
    }

    private static double[] derivative(double[] polynomial){
        double[] derivative = new double[polynomial.length - 1];
        for (int i = 0; i < derivative.length; i++) {
            derivative[i] = polynomial[i] * (derivative.length - i);
        }
        return derivative;
    }

    private final double[] getCharacteristicCoefficients(){
        Expression characteristic = characteristicPolynomial().fullSimplify().withDecimals();
        Variable lambda = new Variable('l',"","\\lambda");
        double[] coefficients = new double[height + 1];
        coefficients[0] = 1;
        for (int i = 1; i < coefficients.length; i++) {
            Expression derivative = characteristic;
            int factorial = 1;
            for (int j = 0; j < i; j++) {
                derivative = derivative.subtract(new Decimal(coefficients[j]).multiply(lambda.pow(new Decimal(height - j))));
            }
            for (int j = 0; j < (height - i); j++) {
                factorial *= (j + 1);
                derivative = derivative.derivative(lambda);
            }
            coefficients[i] = ((Decimal) derivative.withDecimals()).getValue() / (factorial == 0? 1:factorial);
        }
        return coefficients;
    }

    public final Expression characteristicPolynomial(){
        if (width != height){
            throw new RuntimeException("Matrix must be square");
        }
        Symbolic matrix = this.toSymbolic();
        Variable lambda = new Variable('l',"","\\lambda");
        matrix = Symbolic.identity(height).multiply(lambda).subtract(matrix);
        return matrix.det();
    }

    private M cofactor(int x, int y){
        E[][] newElements = newArrayInstance(height - 1, width - 1);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i != x && j != y){
                    newElements[i < x? i:(i - 1)][j < y? j: (j - 1)] = this.get(i,j);
                }
            }
        }
        return newMatrixInstance(newElements);
    }

    protected M performOperation(M A, M B, Operation<E> operation){
        E[][] newElements = newArrayInstance(height,width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[i][j] = operation.operate(A.get(i,j), B == null? null:B.get(i,j));
            }
        }
        return newMatrixInstance(newElements);
    }

    protected final E[][] newArrayInstance(int height, int width){
        return (E[][]) Array.newInstance(elementClass,height,width);
    }

    protected final E[] newArrayInstance(int length){
        return (E[]) Array.newInstance(elementClass,length);
    }

    protected M newMatrixInstance(E[][] elements){
        try {
            Constructor<M> constructor = matrixClass.getConstructor(elements.getClass());
            return constructor.newInstance(new Object[]{elements});
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public final String toString(){
        int[] maxLengths = new int[width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maxLengths[j] = Math.max(maxLengths[j], this.get(i,j).toString().length() + 2);
            }
        }
        String toString = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String elementString = this.get(i,j).toString();
                while (elementString.length() < maxLengths[j]){
                    elementString += " ";
                }
                toString += elementString;
            }
            toString += "\n";
        }
        return toString;
    }

    protected final E add(E a, E b){
        return elementType.add(a,b);
    }
    protected final E subtract(E a, E b){
        return elementType.subtract(a,b);
    }
    protected final E multiply(E a, E b){
        return elementType.multiply(a,b);
    }
    protected final E divide(E a, E b){
        return elementType.divide(a,b);
    }
    protected final E pow(E a, E b){
        return elementType.pow(a,b);
    }
    protected final E valueOf(long a){
        return elementType.valueOf(a);
    }
    protected final String toLaTeX(E a){
        return elementType.toLaTeX(a);
    }
    protected final double toNumeric(E a){
        return elementType.eval(a);
    }
    protected final Expression toSymbolic(E a){
        return elementType.symbolic(a);
    }

    private interface Operation<E> {
        E operate(E a, E b);
    }

    public static class Numeric extends Matrix<Numeric,Double> {

        public Numeric(Double[]... elements){
            super(ElementType.NUMERIC, Numeric.class,Double.class,elements);
        }

        public Numeric(double[]... elements){
            super(ElementType.NUMERIC, Matrix.Numeric.class, Double.class);
            Double[][] newElements = newArrayInstance(elements.length, elements[0].length);
            for (int i = 0; i < newElements.length; i++) {
                for (int j = 0; j < elements[i].length; j++) {
                    newElements[i][j] = elements[i][j];
                }
            }
            initElements(newElements);
        }

        public final Numeric round(int numDecimals){
            double t = Math.pow(8,numDecimals);
            return performOperation(this,null,(a,b) -> Math.round(a * t) / t);
        }

        public final static Numeric identity(int size){
            Double[][] elements = new Double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i == j){
                        elements[i][j] = 1.0;
                    } else {
                        elements[i][j] = 0.0;
                    }
                }
            }
            return new Numeric(elements);
        }

        public Matrix.Numeric[] QRDecomposition(){
            Matrix.Numeric Q = getColumn(0);
            Matrix.Numeric R = newMatrixInstance(pad(Q.getMagnitude()));
            Q = Q.normalize();
            for (int i = 1; i < width; i++) {
                Matrix.Numeric column = getColumn(i);
                Double[] dots = newArrayInstance(Q.width);
                for (int j = 0; j < dots.length; j++) {
                    Matrix.Numeric qColumn = Q.getColumn(j);
                    dots[j] = dot(column,qColumn);
                    column = column.subtract(qColumn.multiply(dots[j]));
                }
                R = R.augment(newMatrixInstance(pad(dots)));
                if (!column.isEmpty()){
                    Double[] zeros = newArrayInstance(Q.width + 1);
                    for (int j = 0; j < zeros.length; j++) {
                        if (j == zeros.length - 1){
                            zeros[j] = column.getMagnitude();
                        } else {
                            zeros[j] = valueOf(0);
                        }
                    }
                    R = R.transpose().augment(newMatrixInstance(pad(zeros))).transpose();
                    Q = Q.augment(column.normalize());
                }
            }
            Matrix.Numeric[] QR = (Matrix.Numeric[]) Array.newInstance(matrixClass,2);
            QR[0] = Q;
            QR[1] = R;
            return QR;
        }
    }

    public static class Symbolic extends Matrix<Matrix.Symbolic, Expression> {

        public Symbolic(Expression[]... elements){
            super(ElementType.SYMBOLIC, Matrix.Symbolic.class, Expression.class, elements);
        }

        public Symbolic(long[]... elements){
            super(ElementType.SYMBOLIC, Matrix.Symbolic.class, Expression.class);
            Expression[][] newElements = newArrayInstance(elements.length, elements[0].length);
            for (int i = 0; i < newElements.length; i++) {
                for (int j = 0; j < elements[i].length; j++) {
                    newElements[i][j] = new Scalar(elements[i][j]);
                }
            }
            initElements(newElements);
        }

        public Matrix.Symbolic simplify(){
            return performOperation(this, null, (a, b) -> a.fullSimplify());
        }

        public final static Matrix.Symbolic identity(int size){
            Expression[][] elements = new Expression[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    elements[i][j] = i == j? Scalar.ONE: Scalar.ZERO;
                }
            }
            return new Matrix.Symbolic(elements);
        }
    }
}
