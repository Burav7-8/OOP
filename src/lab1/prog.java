package lab1;

/**
 * 
 * @author Egor Poberegnyi
 * @version 1.00
 *
 */

public class prog {

public static void main(String args[]){
		

		int[] arr = {11, -1238, 45, -124124, 867, -3333112, 52123, 7878};
		
		// Вывод массива
		for(int i=0;i<arr.length;i++)
			System.out.println("arr["+i+"]="+arr[i]);
		System.out.println("\n");
		
		// Сортировка
		for(int i=0;i<arr.length-1;i++){
			 for(int j=i+1;j<arr.length;j++){
			  if(arr[i]>arr[j]){
			       int tmp=arr[i];
			       arr[i]=arr[j];
			       arr[j]=tmp;
			  }
			 }
			}
		// Вывод отсортированного массива
		for(int i=0;i<arr.length;i++)
			System.out.println("arr["+i+"]="+arr[i]);
	}

}
