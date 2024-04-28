package lin_aljbra;

import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

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
            System.out.println(a + " " + b + " " + c);
            E discriminant = subtract(multiply(b,b),multiply(valueOf(4),multiply(a,c)));
            if (discriminant.equals(valueOf(0))){
                E eigenValue = divide(multiply(valueOf(-1),b),multiply(valueOf(2),a));
                return new ArrayList<>(Arrays.asList(eigenValue));
            }
            E difference = pow(discriminant,divide(valueOf(1),valueOf(2)));
            System.out.println(discriminant);
            E eigenValue1 = divide(add(multiply(valueOf(-1),b),difference),multiply(valueOf(2),a));
            E eigenValue2 = divide(add(multiply(valueOf(-1),b),difference),multiply(valueOf(2),a));
            eigenvalues.add(eigenValue1);
            eigenvalues.add(eigenValue2);
            return eigenvalues;
        }
    }

    public Numeric approximateDominantEigenvector(int numIterations){
        if (width != height){
            throw new RuntimeException("Matrix must be square");
        } if (numIterations <= 0){
            throw new RuntimeException("Variable numIterations must be positive");
        }
        Numeric matrix = eval();
        Double[][] v = new Double[height][1];
        for (int i = 0; i < height; i++){
            v[i][0] = 1.0;
        }
        Numeric eigenVector = new Numeric(v);
        for (int i = 0; i < numIterations;i++){
            eigenVector = matrix.multiply(eigenVector);
            eigenVector = eigenVector.divide(eigenVector.get(0,0));
        }
        return eigenVector;
    }

    public final double approximateEigenvalue(Numeric eigenvector){
        Numeric matrix = eval();
        return dot(matrix.multiply(eigenvector),eigenvector) / dot(eigenvector,eigenvector);
    }

    private static double dot(Numeric a, Numeric b){
        double sum = 0;
        for (int i = 0; i < a.height; i++) {
            sum += a.get(i,0) * b.get(i,0);
        }
        return sum;
    }

    public final Numeric eval(){
        Double[][] newElements = new Double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newElements[i][j] = eval(get(i,j));
            }
        }
        return new Numeric(newElements);
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
    protected final double eval(E a){
        return elementType.eval(a);
    }

    private interface Operation<E> {
        E operate(E a, E b);
    }

    public static class Numeric extends Matrix<Numeric,Double> {

        public Numeric(Double[]... elements){
            super(ElementType.DECIMAL, Numeric.class,Double.class,elements);
        }

        public final static Numeric identity(int size){
            Double[][] elements = new Double[size][size];
            for (int i = 0; i < size;i++){
                elements[i][i] = 1.0;
            }
            return new Numeric(elements);
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
