import java.util.*;

public class Main {

    public static void sort(int[] numbers) {
        int numMerges = mergeSort(numbers, 0, numbers.length,0);
        System.out.println(numMerges);
    }

    public static int mergeSort(int[] numbers, int startIndex, int endIndex, int numMerges) {
        if( endIndex <= startIndex+1) {
            return numMerges;
        }
        int middle = (endIndex-startIndex)/2 + startIndex;
        numMerges = mergeSort(numbers,startIndex,middle,numMerges);
        numMerges = mergeSort(numbers,middle,endIndex,numMerges);
        merge(numbers,startIndex,middle,endIndex);
        numMerges++;
        return numMerges;
    }

    public static void merge(int[] numbers, int startIndex, int middle, int endIndex) {
        int index1=startIndex, index2=middle, index3=0;
        int[] tmp = new int[endIndex-startIndex];
        while(index1<middle && index2<endIndex) {
            if(numbers[index1] <= numbers[index2]) {
                numbers[index3] = numbers[index1];
                index1++;
            } else {
                numbers[index3] = numbers[index2];
                index2++;
            }
            index3++;
        }
        for(; index1 < middle; index1++) {
            numbers[index3++] = numbers[index1];
        }

        for(; index2 < endIndex; index2++) {
            numbers[index3++] = numbers[index2];
        }
        System.arraycopy(tmp,0,numbers,startIndex,tmp.length);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] numbers = new int[scanner.nextInt()];

        int index = 0;
        do {
            numbers[index++] = scanner.nextInt();
        } while(index < numbers.length);
        sort(numbers);
    }
}
